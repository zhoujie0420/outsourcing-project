<script setup>
document.title = "个人中心"; // 设置页面标题

import moment from "moment"; // 导入时间处理库
import { onMounted, reactive, ref } from "vue"; // 导入 Vue 相关模块
import { ElForm, ElMessage, ElMessageBox } from "element-plus"; // 导入 Element Plus 组件
import request from "@/utils/request"; // 导入请求模块
import { useThrottleFn } from "@vueuse/core"; // 导入节流函数
import { Delete, Plus, RefreshRight } from "@element-plus/icons-vue"; // 导入 Element Plus 图标
import { useMainStore } from "@/utils/store.js"; // 导入主要的 store
import router from "@/utils/router.js";

const mainStore = useMainStore();
const updateFormRef = ref(ElForm); // 修改表单引用
const resetFormRef = ref(ElForm); // 修改密码表单引用
const updatePwdFormData = ref({}); // 密码
const options = ref([
  // 下拉选项列表
  {
    value: "男",
    label: "男",
  },
  {
    value: "女",
    label: "女",
  },
]);
const rules = reactive({
  // 表单校验规则
  namee: [{ required: true, message: "请输入昵称", trigger: "blur" }],
});

const rules2 = reactive({
  // 表单校验规则
  oldpwd: [{ required: true, message: "请输入旧密码", trigger: "blur" }],
  newpwd: [{ required: true, message: "请输入新密码", trigger: "blur" }],
  newpwd2: [{ required: true, message: "请输入确认密码", trigger: "blur" }],
});

// 处理上传头像成功的回调
function handleUpdateAvatarSuccess(response) {
  mainStore.curuser.img = response.data.url;
}

//重置
function handleRest() {
  resetFormRef.value.clearValidate();
  resetFormRef.value.resetFields();
}

// 使用节流函数包装修改提交逻辑
const handleUpdateSubmit = useThrottleFn(() => {
  updateFormRef.value.validate((valid) => {
    if (valid) {
      request({
        url: "/api/userr/update", // 修改请求的地址
        data: mainStore.curuser, // 修改后的用户数据
      }).then(() => {
        ElMessage.success("修改成功"); // 提示修改成功
      });
    }
  });
}, 500);

// 使用节流函数包装修改密码提交逻辑
const handleUpdatePwdSubmit = useThrottleFn(() => {
  resetFormRef.value.validate((valid) => {
    if (updatePwdFormData.value.newpwd != updatePwdFormData.value.newpwd2) {
      ElMessage.warning("两次密码不一致");
      return;
    }
    if (valid) {
      request({
        url: "/api/updatePwd", // 修改请求的地址
        data: updatePwdFormData.value, // 修改后的用户数据
      }).then(() => {
        ElMessage.success("修改成功"); // 提示修改成功
        updatePwdFormData.value = {};
      });
    }
  });
}, 500);

// 组件挂载完成后执行查询操作
onMounted(() => {
  mainStore.active = router.currentRoute.value.path; // 初始化当前激活菜单项
});
</script>

<template>
  <el-row>
    <el-col :span="12">
      <el-card style="margin: 10px">
        <template #header>
          <div class="card-header">
            <span>个人信息</span>
          </div>
        </template>
        <el-form
          ref="updateFormRef"
          :model="mainStore.curuser"
          label-width="80px"
          :rules="rules"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="mainStore.curuser.username"
              placeholder="请输入用户名"
              :disabled="true"
            />
          </el-form-item>
          <el-form-item label="昵称" prop="namee">
            <el-input
              v-model="mainStore.curuser.namee"
              placeholder="请输入昵称"
            />
          </el-form-item>
          <el-form-item label="头像" prop="img">
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
          <div style="text-align: center">
            <el-button type="primary" @click="handleUpdateSubmit"
              >确 定</el-button
            >
          </div>
        </template>
      </el-card>
    </el-col>

    <el-col :span="12">
      <el-card style="margin: 10px; margin-left: 0">
        <template #header>
          <div class="card-header">
            <span>修改密码</span>
          </div>
        </template>
        <el-form
          ref="resetFormRef"
          :model="updatePwdFormData"
          label-width="80px"
          :rules="rules2"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="mainStore.curuser.username"
              placeholder="请输入用户名"
              :disabled="true"
            />
          </el-form-item>
          <el-form-item label="旧密码" prop="oldpwd">
            <el-input
              v-model="updatePwdFormData.oldpwd"
              placeholder="请输入旧密码"
              type="password"
              show-password
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newpwd">
            <el-input
              v-model="updatePwdFormData.newpwd"
              placeholder="请输入新密码"
              type="password"
              show-password
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="newpwd2">
            <el-input
              v-model="updatePwdFormData.newpwd2"
              placeholder="请输入确认密码"
              type="password"
              show-password
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <div style="text-align: center">
            <el-button @click="handleRest">重置</el-button>
            <el-button type="primary" @click="handleUpdatePwdSubmit"
              >确 定</el-button
            >
          </div>
        </template>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped></style>
