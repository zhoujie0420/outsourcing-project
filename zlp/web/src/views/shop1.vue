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
  namee: [{ required: true, message: "请输入店铺名称", trigger: "blur" }],
  img: [{ required: true, message: "请上传店铺图片", trigger: "blur" }],
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
  updateFormData.img = response.data.url;
}

//更新图片成功后的处理
function handleUpdateAvatarSuccess(response) {
  mainStore.curuser.img = response.data.url;
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

//使用节流函数封装更新提交操作
const handleUpdateSubmit = useThrottleFn(() => {
  updateFormRef.value.validate((valid) => {
    if (valid) {
      request({
        url: "/api/userr/update",
        data: mainStore.curuser,
      }).then(() => {
        ElMessage.success("修改成功");
      });
    }
  });
}, 500);

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
  //handleQuery();
});
</script>

<template>
  <el-row style="margin-top: 10px">
    <el-col :span="8"></el-col>
    <el-col :span="8">
      <el-card>
        <el-form
          ref="updateFormRef"
          :model="mainStore.curuser"
          label-width="80px"
          :rules="rules"
        >
          <el-form-item label="店铺名称" prop="namee">
            <el-input
              v-model="mainStore.curuser.namee"
              placeholder="请输入店铺名称"
            />
          </el-form-item>
          <el-form-item label="店铺图片" prop="img">
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
                v-if="mainStore.curuser.img"
                :src="mainStore.curuser.img"
                class="avatar"
                style="width: 178px; height: 178px; display: block"
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
          <el-form-item label="店铺介绍" prop="namee">
            <el-input
              v-model="mainStore.curuser.shopcontent"
              placeholder="请输入店铺介绍"
              type="textarea"
              :autosize="{ minRows: 3 }"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <div style="text-align: center">
            <el-button type="primary" @click="handleUpdateSubmit">
              确定
            </el-button>
          </div>
        </template>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped></style>
