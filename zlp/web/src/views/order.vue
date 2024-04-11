<script setup>
document.title = "订单管理";

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
    value: "待发货",
    label: "待发货",
  },
  {
    value: "已发货",
    label: "已发货",
  },
  {
    value: "已签收",
    label: "已签收",
  },
]);
const options2 = ref([]);
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
  orderno: [{ required: true, message: "请输入订单号", trigger: "blur" }],
  userid: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
  shopid: [{ required: true, message: "请输入店铺ID", trigger: "blur" }],
  medicineid: [{ required: true, message: "请输入药品ID", trigger: "blur" }],
  namee: [{ required: true, message: "请输入药品名称", trigger: "blur" }],
  price: [{ required: true, message: "请输入单价，元", trigger: "blur" }],
  cnt: [{ required: true, message: "请输入数量", trigger: "blur" }],
  address: [{ required: true, message: "请输入地址", trigger: "blur" }],
  statuss: [
    {
      required: true,
      message: "请输入发货状态，待发货、已发货、已签收",
      trigger: "blur",
    },
  ],
  evastatus: [
    {
      required: true,
      message: "请输入评价状态，未评论、已评价",
      trigger: "blur",
    },
  ],
  content: [{ required: true, message: "请输入评价内容", trigger: "blur" }],
  showcontent: [
    { required: true, message: "请输入是否显示评价，是、否", trigger: "blur" },
  ],
  rate: [{ required: true, message: "请输入评分", trigger: "blur" }],
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
    url: "/api/orderr/page",
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
      url: "/api/orderr/delete",
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
        url: "/api/orderr/add",
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
        url: "/api/orderr/updateRate",
        data: updateFormData,
      }).then(() => {
        ElMessage.success("操作成功");
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
    options2.value = data;
  });
}

//当当前页码发生变化时更新查询
function onCurrentPageUpdate(num) {
  queryParams.pageNum = num;
  handleQuery();
}

//删除评价
const updateComments = useThrottleFn((row) => {
  ElMessageBox.confirm("确认删除?", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(function () {
    request({
      url: "/api/orderr/updateComments",
      data: {
        id: row.id,
      },
    }).then(() => {
      ElMessage.success("操作成功");
      resetQuery();
    });
  });
}, 500);

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
      <el-form-item label="订单号" prop="orderno" style="margin-bottom: 0">
        <el-input
          v-model="queryParams.orderno"
          placeholder="订单号"
          clearable
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item label="发货状态" prop="statuss" style="margin-bottom: 0">
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
      <el-form-item label="店铺" prop="shopid" style="margin-bottom: 0">
        <el-select
          v-model="queryParams.shopid"
          placeholder="请选择"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="item in options2"
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
          @click="handleDelete"
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
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="订单号" align="center" prop="orderno" />
      <el-table-column label="订单详情" align="center">
        <template #default="scope">
          <div v-for="item in scope.row.sub" style="text-align: center">
            <span>
              {{ item.namee }} 数量：{{ item.cnt }} 单价：{{ item.price }}元
            </span>
          </div>
          <div>总价：{{ scope.row.totalPrice }}元</div>
        </template>
      </el-table-column>
      <el-table-column label="店铺" align="center" prop="shop.namee">
        <template #default="scope">
          <div style="text-align: center">
            <div>
              {{ scope.row.shop.namee }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="用户" align="center">
        <template #default="scope">
          <div style="text-align: center">
            <div>
              {{ scope.row.user.namee }}
            </div>
            <div>
              {{ moment(scope.row.createtime).format("YYYY-MM-DD HH:mm:ss") }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="地址" align="center" prop="addcontent">
        <template #default="scope">
          <div style="text-align: center">
            <div>联系人：{{ scope.row.address.name }}</div>
            <div>手机号：{{ scope.row.address.phone }}</div>
            <div>详细地址：{{ scope.row.address.address }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="发货状态" align="center" prop="statuss">
        <template #default="scope">
          <div style="text-align: center">
            <div>{{ scope.row.statuss }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="评价内容" align="center" prop="content">
        <template #default="scope">
          <div style="text-align: center">
            <div v-if="scope.row.showcontent == '是'">
              <div v-if="scope.row.evastatus == '已评价'">
                评分：{{ scope.row.rate }}
              </div>
              <div v-if="scope.row.evastatus == '已评价'">
                内容：{{ scope.row.content }}
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right">
        <template #default="scope">
          <el-button
            v-if="
              scope.row.showcontent == '是' && scope.row.evastatus == '已评价'
            "
            type="primary"
            link
            size="small"
            @click="updateComments(scope.row)"
          >
            删除评价
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
      <el-form-item label="订单号" prop="orderno">
        <el-input v-model="addFormData.orderno" placeholder="请输入订单号" />
      </el-form-item>
      <el-form-item label="用户ID" prop="userid">
        <el-input v-model="addFormData.userid" placeholder="请输入用户ID" />
      </el-form-item>
      <el-form-item label="店铺ID" prop="shopid">
        <el-input v-model="addFormData.shopid" placeholder="请输入店铺ID" />
      </el-form-item>
      <el-form-item label="药品ID" prop="medicineid">
        <el-input v-model="addFormData.medicineid" placeholder="请输入药品ID" />
      </el-form-item>
      <el-form-item label="药品名称" prop="namee">
        <el-input v-model="addFormData.namee" placeholder="请输入药品名称" />
      </el-form-item>
      <el-form-item label="单价，元" prop="price">
        <el-input v-model="addFormData.price" placeholder="请输入单价，元" />
      </el-form-item>
      <el-form-item label="数量" prop="cnt">
        <el-input v-model="addFormData.cnt" placeholder="请输入数量" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="addFormData.address" placeholder="请输入地址" />
      </el-form-item>
      <el-form-item label="发货状态，待发货、已发货、已签收" prop="statuss">
        <el-input
          v-model="addFormData.statuss"
          placeholder="请输入发货状态，待发货、已发货、已签收"
        />
      </el-form-item>
      <el-form-item label="评价状态，未评论、已评价" prop="evastatus">
        <el-input
          v-model="addFormData.evastatus"
          placeholder="请输入评价状态，未评论、已评价"
        />
      </el-form-item>
      <el-form-item label="评价内容" prop="content">
        <el-input v-model="addFormData.content" placeholder="请输入评价内容" />
      </el-form-item>
      <el-form-item label="是否显示评价，是、否" prop="showcontent">
        <el-input
          v-model="addFormData.showcontent"
          placeholder="请输入是否显示评价，是、否"
        />
      </el-form-item>
      <el-form-item label="评分" prop="rate">
        <el-input v-model="addFormData.rate" placeholder="请输入评分" />
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
    title="评价"
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
      <el-form-item label="评分" prop="rate">
        <el-rate v-model="updateFormData.rate" />
      </el-form-item>
      <el-form-item label="评价内容" prop="content">
        <el-input
          v-model="updateFormData.content"
          placeholder="请输入评价内容"
          type="textarea"
          :autosize="{ minRows: 3 }"
        />
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
