<script setup>
document.title = "在线消息";

import { onMounted, onUnmounted, reactive, ref } from "vue"; // 导入 Vue 相关模块
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
const msg = ref("");
// 加载状态控制
const loading = ref(false);
const intervalId = ref({});
// 删除操作选择的项的 ID 列表
const removeIds = ref([]);
const activeName = ref();
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
  userid: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
  shopid: [{ required: true, message: "请输入店铺ID", trigger: "blur" }],
  content: [{ required: true, message: "请输入内容", trigger: "blur" }],
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

const sendMsg = useThrottleFn(() => {
  if (msg.value) {
    request({
      url: "/api/message/add",
      data: {
        userid: mainStore.curuser.id,
        shopid: activeName.value,
        content: msg.value,
        typee: "toshop",
      },
    }).then(({}) => {
      msg.value = "";
    });
  }
});

//执行查询操作
function handleQuery() {
  intervalId.value = setInterval(function () {
    if (queryParams.shopid) {
      request({
        url: "/api/message/list",
        data: queryParams,
      }).then(({ data }) => {
        pageDataList.value = data;
      });
    }
  }, 500);
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
      url: "/api/message/delete",
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
        url: "/api/message/add",
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
        url: "/api/message/update",
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
//切换tab
function onTabChange() {
  queryParams.userid = mainStore.curuser.id;
  queryParams.shopid = activeName.value;
}
//组件挂载时执行初始化查询
onMounted(() => {
  mainStore.active = router.currentRoute.value.path; // 初始化当前激活菜单项
  handleQuery();
});
onUnmounted(() => {
  clearInterval(intervalId.value);
});
</script>

<template>
  <el-card style="margin-top: 10px; height: 600px">
    <template #header> 在线消息 </template>
    <el-tabs tab-position="left" v-model="activeName" @tab-change="onTabChange">
      <el-tab-pane
        v-for="item in mainStore.contactData"
        :label="item.shop.namee"
        :name="item.shop.id"
      >
        <div style="height: 400px; border: 1px solid #f2f3f5; overflow-y: auto">
          <div v-for="item in pageDataList" style="margin-top: 10px">
            <div
              v-if="item.typee == 'touser'"
              style="text-align: left; padding: 10px"
            >
              {{ item.shop.namee }}：
              <span
                style="
                  background-color: #f2f3f5;
                  padding: 10px;
                  border-radius: 10px;
                "
              >
                {{ item.content }}
              </span>
            </div>
            <div>

            </div>
            <div
              v-if="item.typee == 'toshop'"
              style="text-align: right; padding: 10px"
            >
              <span
                style="
                  background-color: #95ec69;
                  padding: 10px;
                  border-radius: 10px;
                "
              >
                {{ item.content }}
              </span>
              ：{{ item.user.namee }}
            </div>
          </div>
          <div style="height: 10px"></div>
        </div>
        <div
          style="
            margin-top: 10px;
            display: flex;
            align-items: center;
            gap: 10px;
          "
        >
          <el-input v-model="msg" type="textarea" :autosize="{ minRows: 3 }" />
          <el-button type="primary" @click="sendMsg"> 发送 </el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<style scoped></style>
