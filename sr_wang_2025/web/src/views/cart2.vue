<script setup>
document.title = "购物车";

import moment from "moment"; // 导入时间处理库
import { onMounted, reactive, ref } from "vue"; // 导入 Vue 相关模块
import { ElForm, ElMessage, ElMessageBox } from "element-plus"; // 导入 Element Plus 组件
import request from "@/utils/request"; // 导入请求模块
import { useThrottleFn } from "@vueuse/core"; // 导入节流函数
import { Delete, Minus, Plus, RefreshRight } from "@element-plus/icons-vue"; // 导入 Element Plus 图标
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
const addressDataList = ref([]);
// 添加对话框的可见性控制
const addDialog = ref(false);
// 更新对话框的可见性控制
const updateDialog = ref(false);
const addressDialog = ref(false);
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
  userid: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
  medicineid: [{ required: true, message: "请输入药品ID", trigger: "blur" }],
  cnt: [{ required: true, message: "请输入数量", trigger: "blur" }],
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
    url: "/api/cart/page",
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
function openSelAddressDialog() {
  addressDialog.value = true;
  const address = mainStore.curuser.address;
  if (address) {
    addressDataList.value = JSON.parse(address);
  }
}
function handleChange(...args) {
  console.log(args);
  // request({
  //   url: "/api/cart/update",
  //   data: queryParams,
  // }).then(({ data }) => {
  //   pageDataList.value = data.list;
  //   total.value = data.total;
  // });
}
function closeAddressDialog() {
  addressDialog.value = false;
}

//执行删除操作
function handleDelete(row) {
  ElMessageBox.confirm("确认移除?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(function () {
    request({
      url: "/api/cart/delete",
      data: {
        ids: [row.id],
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
        url: "/api/cart/add",
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
        url: "/api/cart/update",
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
function selAddress(row) {
  const address = JSON.stringify(row);
  addressDialog.value = false;
  ElMessageBox.confirm(`确认购买?`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(function () {
    request({
      url: "/api/orderr/add",
      data: {
        ids: removeIds.value,
        addcontent: address,
      },
    }).then(() => {
      ElMessage.success("下单成功");
      resetQuery();
    });
  });
}
function minus(row) {
  if (row.cnt <= 1) {
    return;
  }
  row.cnt--;
  request({
    url: "/api/cart/update",
    data: row,
  }).then(() => {});
}
function plus(row) {
  row.cnt++;
  request({
    url: "/api/cart/update",
    data: row,
  }).then(() => {});
}
//详情
function openArticle(row) {
  mainStore.tmpData = row;
  mainStore.tmpData.type = "药品";
  router.push("/article");
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
      <el-form-item style="margin-bottom: 0">
        <el-button
          type="danger"
          :disabled="removeIds.length === 0"
          @click="openSelAddressDialog"
        >
          下单
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
      <el-table-column label="药品" align="left" prop="userid">
        <template #default="scope">
          <div style="display: flex; align-items: center; gap: 5px">
            <el-image
              :preview-teleported="true"
              style="width: 50px; height: 50px"
              :src="scope.row.medicine.img"
              :preview-src-list="[scope.row.medicine.img]"
              fit="cover"
            />
            <div>
              <el-button
                type="primary"
                link
                @click="openArticle(scope.row.medicine)"
              >
                {{ scope.row.medicine.namee }}
              </el-button>
              <div style="font-size: small; color: gray">
                {{ scope.row.shop.namee }}
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="数量" align="center" prop="cnt">
        <template #default="scope">
          <el-icon @click="minus(scope.row)">
            <Minus />
          </el-icon>
          <span style="width: 80px; display: inline-block">
            {{ scope.row.cnt }}
          </span>
          <el-icon @click="plus(scope.row)">
            <Plus />
          </el-icon>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="right" fixed="right" width="100">
        <template #default="scope">
          <el-button
            type="primary"
            link
            size="small"
            @click="handleDelete(scope.row)"
          >
            移除
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
      <el-form-item label="用户ID" prop="userid">
        <el-input v-model="addFormData.userid" placeholder="请输入用户ID" />
      </el-form-item>
      <el-form-item label="药品ID" prop="medicineid">
        <el-input v-model="addFormData.medicineid" placeholder="请输入药品ID" />
      </el-form-item>
      <el-form-item label="数量" prop="cnt">
        <el-input v-model="addFormData.cnt" placeholder="请输入数量" />
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
      <el-form-item label="用户ID" prop="userid">
        <el-input v-model="updateFormData.userid" placeholder="请输入用户ID" />
      </el-form-item>
      <el-form-item label="药品ID" prop="medicineid">
        <el-input
          v-model="updateFormData.medicineid"
          placeholder="请输入药品ID"
        />
      </el-form-item>
      <el-form-item label="数量" prop="cnt">
        <el-input v-model="updateFormData.cnt" placeholder="请输入数量" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button @click="closeUpdateDialog">取 消</el-button>
        <el-button type="primary" @click="handleUpdateSubmit">确 定</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog
    v-model="addressDialog"
    title="选择地址"
    width="600px"
    append-to-body
    @close="closeAddressDialog"
  >
    <el-table :data="addressDataList">
      <el-table-column label="联系人" align="center" prop="name" />
      <el-table-column label="手机号" align="center" prop="phone" />
      <el-table-column
        label="详细地址"
        align="center"
        prop="address"
        show-overflow-tooltip
      />
      <el-table-column label="操作" align="right" fixed="right" width="100">
        <template #default="scope">
          <el-button
            type="primary"
            link
            size="small"
            @click="selAddress(scope.row)"
          >
            选择
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<style scoped></style>
