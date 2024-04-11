<script setup>
document.title = "首页";

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
    value: "用药知识",
    label: "用药知识",
  },
  {
    value: "健康知识",
    label: "健康知识",
  },
]);
//用药知识 健康知识
const activeName = ref("用药知识");
// 添加表单的数据
const addFormData = reactive({});
// 更新表单的数据
const updateFormData = reactive({});
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
});
//第一个公告
const first = ref({});
const showFirst = ref(false);
// 表单规则
const rules = reactive({
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  typee: [
    {
      required: true,
      message: "请输入类型，用药知识、健康知识、公告",
      trigger: "blur",
    },
  ],
  img: [{ required: true, message: "请输入图片", trigger: "blur" }],
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
    url: "/api/knowledge/pageNotice",
    data: queryParams,
  })
      .then(({ data }) => {
        if (data.list && data.list.length > 0) {
          first.value = data.list[0];
          showFirst.value = true;
          pageDataList.value = data.list.slice(1);
          total.value = data.total - 1;
        }
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
      url: "/api/knowledge/delete",
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
        url: "/api/knowledge/add",
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
        url: "/api/knowledge/update",
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

//详情
function openArticle(row) {
  mainStore.tmpData = row;
  mainStore.tmpData.type = "公告";
  router.push("/article");
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
  queryParams.typee = activeName.value;
  resetQuery();
}

//组件挂载时执行初始化查询
onMounted(() => {
  mainStore.active = router.currentRoute.value.path; // 初始化当前激活菜单项
  handleQuery();
});
</script>

<template>
  <el-card style="margin-top: 10px" v-if="showFirst">
    <template #header> 最新公告</template>
    <div>
      <div style="text-align: center">
        {{ first.title }}
      </div>
      <div
          style="
          text-align: right;
          font-size: small;
          color: gray;
          margin-right: 20px;
        "
      >
        {{ moment(first.createtime).format("YYYY-MM-DD HH:mm:ss") }}
      </div>
      <pre style="font-size: 16px; white-space: pre-wrap">
        {{ first.content }}
      </pre>
    </div>
  </el-card>
  <el-card style="margin-top: 10px">
    <template #header> 历史公告</template>
    <el-table v-loading="loading" :data="pageDataList">
      <el-table-column label="" align="left" prop="title">
        <template #default="scope">
          <el-button
              type="primary"
              link
              size="small"
              @click="openArticle(scope.row)"
          >
            {{ scope.row.title }}
          </el-button>
        </template>
      </el-table-column>

      <el-table-column label="" align="right" prop="createtime">
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

  <div style="height: 10px"></div>
</template>

<style scoped></style>
