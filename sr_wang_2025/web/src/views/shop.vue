<script setup>
document.title = "药店管理";

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
// 可选项列表
const options = ref([
  {
    value: "审核中",
    label: "审核中",
  },
  {
    value: "已通过",
    label: "已通过",
  },
  {
    value: "未通过",
    label: "未通过",
  },
]);
// 添加表单的数据
const addFormData = reactive({});
// 更新表单的数据
const updateFormData = reactive({});
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  typee: "药店",
});
// 表单规则
const rules = reactive({
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  pwd: [{ required: true, message: "请输入密码", trigger: "blur" }],
  namee: [{ required: true, message: "请输入昵称", trigger: "blur" }],
  img: [{ required: true, message: "请输入头像", trigger: "blur" }],
  typee: [
    {
      required: true,
      message: "请输入类型，管理员、药店、用户",
      trigger: "blur",
    },
  ],
  statuss: [
    {
      required: true,
      message: "请输入状态，审核中、已通过、未通过",
      trigger: "blur",
    },
  ],
  shopcontent: [{ required: true, message: "请输入店铺介绍", trigger: "blur" }],
  shopimgs: [{ required: true, message: "请输入店铺资质", trigger: "blur" }],
  address: [{ required: true, message: "请输入地址", trigger: "blur" }],
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
  queryFormRef.value.resetFields();
  queryParams.pageNum = 1;
  handleQuery();
}

//处理行选择变化事件
function handleSelectionChange(selection) {
  removeIds.value = selection.map((item) => item.id);
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
    url: "/api/userr/page",
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
    request({
      url: "/api/userr/delete",
      data: {
        ids: removeIds.value,
      },
    }).then(() => {
      ElMessage.success("删除成功");
      resetQuery();
    });
  });
}

//修改状态
function updateStatus(row, status) {
  ElMessageBox.confirm(`确认${status}?`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(function () {
    request({
      url: "/api/userr/update",
      data: {
        id: row.id,
        statuss: status,
      },
    }).then(() => {
      ElMessage.success("操作成功");
      resetQuery();
    });
  });
}

//使用节流函数封装添加提交操作
const handleAddSubmit = useThrottleFn(() => {
  addFormRef.value.validate((valid) => {
    if (valid) {
      request({
        url: "/api/userr/add",
        data: addFormData,
      }).then(() => {
        ElMessage.success("添加成功");
        closeAddDialog();
        resetQuery();
      });
    }
  });
}, 500);
//使用节流函数封装更新提交操作
const handleUpdateSubmit = useThrottleFn(() => {
  updateFormRef.value.validate((valid) => {
    if (valid) {
      request({
        url: "/api/userr/update",
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

//当页数发生变化时更新查询
function onCurrentPageSizeUpdate(size) {
  queryParams.pageSize = size;
  handleQuery();
}

//组件挂载时执行初始化查询
onMounted(() => {
  mainStore.active = router.currentRoute.value.path; // 初始化当前激活菜单项
  handleQuery();
});
</script>

<template>
  <el-card style="margin: 10px">
    <el-form ref="queryFormRef" :model="queryParams" :inline="true">
      <el-form-item label="关键字" prop="keyword" style="margin-bottom: 0">
        <el-input
          v-model="queryParams.keyword"
          placeholder="关键字"
          clearable
          style="width: 200px"
        />
      </el-form-item>

      <el-form-item label="状态" prop="statuss" style="margin-bottom: 0">
        <el-select
          v-model="queryParams.statuss"
          placeholder="请选择"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item style="margin-bottom: 0">
        <el-button type="primary" @click="handleQuery">
          <el-icon>
            <Search />
          </el-icon>
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <el-icon>
            <RefreshRight />
          </el-icon>
          重置
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
      :border="true"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="用户名" align="center" prop="username" />
      <el-table-column label="店铺名称" align="center" prop="namee" />
      <el-table-column label="店铺图片" align="center" prop="img">
        <template #default="scope">
          <el-image
            :preview-teleported="true"
            style="width: 50px; height: 50px"
            :src="scope.row.img"
            :preview-src-list="[scope.row.img]"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column label="资质图片" align="center" prop="shopimgs">
        <template #default="scope">
          <el-image
            v-for="item in scope.row.shopimgs.split(',')"
            :preview-teleported="true"
            style="width: 50px; height: 50px; margin-left: 5px"
            :src="item"
            :preview-src-list="[item]"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="statuss" />
      <el-table-column label="注册时间" align="center" prop="createtime">
        <template #default="scope">
          {{ moment(scope.row.createtime).format("YYYY-MM-DD HH:mm:ss") }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right" width="220">
        <template #default="scope">
          <el-button
            v-if="scope.row.statuss == '审核中'"
            type="primary"
            link
            size="small"
            @click="updateStatus(scope.row, '已通过')"
          >
            通过
          </el-button>
          <el-button
            v-if="scope.row.statuss == '审核中'"
            type="primary"
            link
            size="small"
            @click="updateStatus(scope.row, '未通过')"
          >
            不通过
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 15px"
      v-if="total > 0"
      :total="total"
      :current-page="queryParams.pageNum"
      :page-size="queryParams.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      @update:current-page="onCurrentPageUpdate"
      @update:page-size="onCurrentPageSizeUpdate"
    />
  </el-card>
</template>

<style scoped></style>
