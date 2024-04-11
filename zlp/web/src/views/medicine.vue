<script setup>
document.title = "药品管理";

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
// 添加表单的数据
const addFormData = reactive({});
// 更新表单的数据
const updateFormData = reactive({});
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
});
// 表单规则
const rules = reactive({
  shopid: [{ required: true, message: "请输入药店ID", trigger: "blur" }],
  namee: [{ required: true, message: "请输入药品名称", trigger: "blur" }],
  description: [{ required: true, message: "请输入药品描述", trigger: "blur" }],
  img: [{ required: true, message: "请输入图片", trigger: "blur" }],
  price: [{ required: true, message: "请输入单价，元", trigger: "blur" }],
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
    url: "/api/medicine/page",
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
      url: "/api/medicine/delete",
      data: {
        ids: removeIds.value,
      },
    }).then(() => {
      ElMessage.success("删除成功");
      resetQuery();
    });
  });
}

//使用节流函数封装添加提交操作
const handleAddSubmit = useThrottleFn(() => {
  addFormRef.value.validate((valid) => {
    if (valid) {
      request({
        url: "/api/medicine/add",
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
        url: "/api/medicine/update",
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
    url: "/api/userr/options",
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
  listOptions();
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

      <el-form-item label="店铺" prop="shopid" style="margin-bottom: 0">
        <el-select
          v-model="queryParams.shopid"
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
      :border="true"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="药品名称" align="center" prop="namee" />
      <el-table-column
        label="药品描述"
        align="center"
        prop="description"
        show-overflow-tooltip
      />
      <el-table-column label="图片" align="center" prop="img">
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
      <el-table-column label="单价，元" align="center" prop="price" />
      <el-table-column label="创建时间" align="center" prop="createtime">
        <template #default="scope">
          {{ moment(scope.row.createtime).format("YYYY-MM-DD HH:mm:ss") }}
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
      <el-form-item label="药品名称" prop="namee">
        <el-input v-model="addFormData.namee" placeholder="请输入药品名称" />
      </el-form-item>
      <el-form-item label="药品描述" prop="description">
        <el-input
          v-model="addFormData.description"
          placeholder="请输入药品描述"
          type="textarea"
          :autosize="{ minRows: 3 }"
        />
      </el-form-item>
      <el-form-item label="单价，元" prop="price">
        <el-input
          v-model="addFormData.price"
          placeholder="请输入单价，元"
          type="number"
        />
      </el-form-item>
      <el-form-item label="图片" prop="img">
        <el-upload
          style="
            width: 178px;
            height: 178px;
            display: block;
            border: 1px dashed var(--el-border-color);
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            transition: var(--el-transition-duration-fast);
          "
          :action="mainStore.setting.uploadURL"
          :show-file-list="false"
          :on-success="handleAddAvatarSuccess"
        >
          <img
            v-if="addFormData.img"
            :src="addFormData.img"
            style="
              width: 178px;
              height: 178px;
              display: block;
              object-fit: cover;
            "
          />
          <el-icon
            v-else
            style="
              font-size: 28px;
              color: #8c939d;
              width: 178px;
              height: 178px;
              text-align: center;
            "
          >
            <Plus />
          </el-icon>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button @click="closeAddDialog">取 消</el-button>
        <el-button type="primary" @click="handleAddSubmit">确 定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog
    v-model="updateDialog"
    title="修改"
    width="600px"
    append-to-body
    @close="closeUpdateDialog"
  >
    <!-- 修改 -->
    <el-form
      ref="updateFormRef"
      :model="updateFormData"
      label-width="80px"
      :rules="rules"
    >
      <el-form-item label="药品名称" prop="namee">
        <el-input v-model="updateFormData.namee" placeholder="请输入药品名称" />
      </el-form-item>
      <el-form-item label="药品描述" prop="description">
        <el-input
          v-model="updateFormData.description"
          placeholder="请输入药品描述"
          type="textarea"
          :autosize="{ minRows: 3 }"
        />
      </el-form-item>
      <el-form-item label="单价，元" prop="price">
        <el-input
          v-model="updateFormData.price"
          placeholder="请输入单价，元"
          type="number"
        />
      </el-form-item>
      <el-form-item label="图片" prop="img">
        <el-upload
          style="
            width: 178px;
            height: 178px;
            display: block;
            border: 1px dashed var(--el-border-color);
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            transition: var(--el-transition-duration-fast);
          "
          :action="mainStore.setting.uploadURL"
          :show-file-list="false"
          :on-success="handleUpdateAvatarSuccess"
        >
          <img
            v-if="updateFormData.img"
            :src="updateFormData.img"
            style="
              width: 178px;
              height: 178px;
              display: block;
              object-fit: cover;
            "
          />
          <el-icon
            v-else
            style="
              font-size: 28px;
              color: #8c939d;
              width: 178px;
              height: 178px;
              text-align: center;
            "
          >
            <Plus />
          </el-icon>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button @click="closeUpdateDialog">取 消</el-button>
        <el-button type="primary" @click="handleUpdateSubmit">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped></style>
