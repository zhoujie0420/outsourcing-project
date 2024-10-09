<script setup>
document.title = "地址管理";

import moment from "moment"; // 导入时间处理库
import { onMounted, reactive, ref } from "vue"; // 导入 Vue 相关模块
import { ElForm, ElMessage, ElMessageBox } from "element-plus"; // 导入 Element Plus 组件
import request from "@/utils/request"; // 导入请求模块
import { useThrottleFn } from "@vueuse/core"; // 导入节流函数
import { Delete, Plus, RefreshRight } from "@element-plus/icons-vue"; // 导入 Element Plus 图标
import { useMainStore } from "@/utils/store.js"; // 导入主要的 store
import router from "@/utils/router.js";

// 主要的 store 实例
const mainStore = useMainStore();
// 查询表单的引用
const queryFormRef = ref(ElForm);
// 添加表单的引用
const addFormRef = ref(ElForm);
// 更新表单的引用
const updateFormRef = ref(ElForm);
// 数据总数
const total = ref(0);
// 分页数据列表
const pageDataList = ref([]);
// 添加对话框的可见性控制
const addDialog = ref(false);
// 更新对话框的可见性控制
const updateDialog = ref(false);
// 加载状态控制
const loading = ref(false);
// 删除操作选择的项的 ID 列表
const removeIds = ref([]);
// 可选项列表，例如性别
const options = ref([
  {
    value: "男",
    label: "男",
  },
  {
    value: "女",
    label: "女",
  },
]);
//药品 店铺
const activeName = ref("药品");
// 添加表单的数据
const addFormData = reactive({});
// 更新表单的数据
const updateFormData = reactive({});
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  typee: "药品",
});
// 表单规则
const rules = reactive({
  name: [{ required: true, message: "请输入联系人", trigger: "blur" }],
  phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
  address: [{ required: true, message: "请输入详细地址", trigger: "blur" }],
});

//打开添加对话框
function openAddDialog() {
  addDialog.value = true;
}
//打开更新对话框
function openUpdateDialog(row) {
  updateDialog.value = true;
  Object.assign(updateFormData, { ...row });
}
//关闭添加对话框，并重置表单
function closeAddDialog() {
  addDialog.value = false;
  addFormRef.value.resetFields();
  addFormRef.value.clearValidate();
}
//关闭更新对话框，并重置表单
function closeUpdateDialog() {
  updateDialog.value = false;
  updateFormRef.value.resetFields();
  updateFormRef.value.clearValidate();
}
//重置查询条件，并执行查询
function resetQuery() {
  queryParams.pageNum = 1;
  handleQuery();
}
//处理行选择变化事件
function handleSelectionChange(selection) {
  removeIds.value = selection.map((item) => item);
}
//添加图片成功后的处理
function handleAddAvatarSuccess(response) {
  addFormData.img = response.data.url;
}
//更新图片成功后的处理
function handleUpdateAvatarSuccess(response) {
  updateFormData.img = response.data.url;
}
//执行查询操作
function handleQuery() {
  loading.value = true;
  request({
    url: "/api/bookmark/page",
    data: queryParams,
  })
    .then(({ data }) => {
      pageDataList.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}
//执行删除操作
function handleDelete() {
  if (removeIds.value.length === 0) {
    ElMessage.warning("请勾选删除项");
    return;
  }

  ElMessageBox.confirm("确认删除?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(function () {
    removeIds.value.forEach((item) => {
      const index = pageDataList.value.indexOf(item);
      if (index !== -1) {
        // 使用splice方法删除该对象
        pageDataList.value.splice(index, 1);
      }
    });
    mainStore.curuser.address = JSON.stringify(pageDataList.value);
    request({
      url: "/api/userr/update",
      data: mainStore.curuser,
    }).then(() => {
      ElMessage.success("操作成功");
    });
  });
}

//使用节流函数封装添加提交操作
const handleAddSubmit = useThrottleFn(() => {
  addFormRef.value.validate((valid) => {
    if (valid) {
      pageDataList.value.push({
        name: addFormData.name,
        phone: addFormData.phone,
        address: addFormData.address,
      });
      mainStore.curuser.address = JSON.stringify(pageDataList.value);
      request({
        url: "/api/userr/update",
        data: mainStore.curuser,
      }).then(() => {
        ElMessage.success("操作成功");
        closeAddDialog();
      });
    }
  });
}, 500);
//使用节流函数封装更新提交操作
const handleUpdateSubmit = useThrottleFn(() => {
  updateFormRef.value.validate((valid) => {
    if (valid) {
      request({
        url: "/api/bookmark/update",
        data: updateFormData,
      }).then(() => {
        ElMessage.success("修改成功");
        closeUpdateDialog();
        resetQuery();
      });
    }
  });
}, 500);
// 加载下拉列表
function listOptions() {
  request({
    url: "",
    data: {},
  }).then(({ data }) => {
    options.value = data;
  });
}
//当当前页码发生变化时更新查询
function onCurrentPageUpdate(num) {
  queryParams.pageNum = num;
  handleQuery();
}

//详情
function openArticle(row, type) {
  if (type == "店铺") {
    mainStore.tmpData.shop = row;
  } else {
    mainStore.tmpData = row;
  }
  mainStore.tmpData.type = type;
  router.push("/article");
}
//切换tab
function onTabChange() {
  queryParams.typee = activeName.value;
  resetQuery();
}
//当页数发生变化时更新查询
function onCurrentPageSizeUpdate(size) {
  queryParams.pageSize = size;
  handleQuery();
}
//组件挂载时执行初始化查询
onMounted(() => {
  mainStore.active = router.currentRoute.value.path; // 初始化当前激活菜单项
  const address = mainStore.curuser.address;
  if (address) {
    pageDataList.value = JSON.parse(address);
  }
  //handleQuery();
});
</script>

<template>
  <el-card style="margin: 10px">
    <el-form ref="queryFormRef" :model="queryParams" :inline="true">
      <el-form-item style="margin-bottom: 0">
        <el-button type="success" @click="openAddDialog()">
          <el-icon>
            <Plus />
          </el-icon>
          新增
        </el-button>
        <el-button
          type="danger"
          :disabled="removeIds.length === 0"
          @click="handleDelete()"
        >
          <el-icon>
            <Delete />
          </el-icon>
          删除
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>

  <el-card style="margin: 10px">
    <el-table
      v-loading="loading"
      :data="pageDataList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="联系人" align="center" prop="name" />
      <el-table-column label="手机号" align="center" prop="phone" />
      <el-table-column
        label="详细地址"
        align="center"
        prop="address"
        show-overflow-tooltip
      />
    </el-table>
  </el-card>

  <el-dialog
    v-model="addDialog"
    title="添加"
    width="600px"
    append-to-body
    @close="closeAddDialog"
  >
    <!-- 新增 -->
    <el-form
      ref="addFormRef"
      :model="addFormData"
      label-width="80px"
      :rules="rules"
    >
      <el-form-item label="联系人" prop="name">
        <el-input v-model="addFormData.name" placeholder="请输入联系人" />
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input v-model="addFormData.phone" placeholder="请输入手机号" />
      </el-form-item>

      <el-form-item label="详细地址" prop="address">
        <el-input
          v-model="addFormData.address"
          placeholder="请输入内容"
          type="textarea"
          :autosize="{ minRows: 3 }"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button @click="closeAddDialog">取 消</el-button>
        <el-button type="primary" @click="handleAddSubmit">确 定</el-button>
      </div>
    </template>
  </el-dialog>

  <div style="height: 10px"></div>
</template>

<style scoped></style>
