<script setup>
document.title = "注册";

import { ElForm, ElMessage } from "element-plus";
import { reactive, ref } from "vue";
import { useMainStore } from "@/utils/store.js"; // 导入 main store
import { useThrottleFn } from "@vueuse/core";
import request from "@/utils/request.js"; // 导入请求模块
import router from "@/utils/router.js";
import { Plus } from "@element-plus/icons-vue"; // 导入路由模块

const mainStore = useMainStore(); // 获取 main store 实例
// 表单规则
const rules = reactive({
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  pwd: [{ required: true, message: "请输入密码", trigger: "blur" }],
  namee: [{ required: true, message: "请输入名称", trigger: "blur" }],
  phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
  email: [{ required: true, message: "请输入邮箱", trigger: "blur" }],
  img: [{ required: true, message: "请上传图片", trigger: "blur" }],
});
const addFormRef = ref(ElForm); // 添加表单引用
//表单数据
const addFormData = reactive({
  typee: "用户",
}); // 添加表单数据
const options = ref([
  {
    value: "药店",
    label: "药店",
  },
  {
    value: "用户",
    label: "用户",
  },
]);

//图片列表
const fileList = ref([]);

// 处理上传头像成功的回调
function handleAddAvatarSuccess(response) {
  addFormData.img = response.data.url;
}

// 处理上传头像成功的回调
function handleUpdateAvatarSuccess(response) {
  updateFormData.img = response.data.url;
}

function resetForm() {
  addFormRef.value.resetFields();
  addFormRef.value.clearValidate();
}

function toLogin() {
  router.push("/login");
}

// 使用节流函数包装添加提交逻辑
const handleAddSubmit = useThrottleFn(() => {
  addFormRef.value.validate((valid) => {
    if (valid) {
      if (addFormData.typee == "药店") {
        if (fileList.value.length == 0) {
          ElMessage.warning("请上传资质");
          return;
        }
        const list = [];
        fileList.value.forEach(function (item) {
          list.push(item.response.data.url);
        });
        addFormData.shopimgs = list.join();
      }
      request({
        url: "/reg", // 添加请求的地址
        data: addFormData, // 添加表单数据
      }).then(() => {
        ElMessage.success("提交成功"); // 提示添加成功
        router.push("/login");
      });
    }
  });
}, 500);

function onSelectChange(option) {
  if (option == "用户") {
    fileList.value = [];
  }
}
</script>

<template>
  <div class="background">
    <div class="container">
      <div class="centered-div">
        <el-card>
          <template #header>
            <div>注册</div>
          </template>
          <el-form
            ref="addFormRef"
            :model="addFormData"
            label-width="80px"
            :rules="rules"
          >
            <el-form-item label="角色" prop="typee">
              <el-select
                v-model="addFormData.typee"
                placeholder="请选择"
                @change="onSelectChange"
              >
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="addFormData.username"
                placeholder="请输入用户名"
              />
            </el-form-item>
            <el-form-item label="密码" prop="pwd">
              <el-input
                v-model="addFormData.pwd"
                placeholder="请输入密码"
                type="password"
                show-password
              />
            </el-form-item>
            <el-form-item
              label="昵称"
              prop="namee"
              v-if="addFormData.typee == '用户'"
            >
              <el-input v-model="addFormData.namee" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item
              label="店铺名称"
              prop="namee"
              v-if="addFormData.typee == '药店'"
            >
              <el-input
                v-model="addFormData.namee"
                placeholder="请输入店铺名称"
              />
            </el-form-item>
            <el-form-item
              label="头像"
              prop="img"
              v-if="addFormData.typee == '用户'"
            >
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
            <el-form-item
              label="店铺图片"
              prop="img"
              v-if="addFormData.typee == '药店'"
            >
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
            <el-form-item
              label="店铺资质"
              prop=""
              v-if="addFormData.typee == '药店'"
            >
              <el-upload
                v-model:file-list="fileList"
                multiple
                :action="mainStore.setting.uploadURL"
                list-type="picture"
                style="width: 100%"
              >
                <el-button type="primary">点击上传</el-button>
              </el-upload>
            </el-form-item>
          </el-form>
          <template #footer>
            <div style="text-align: center">
              <el-button @click="toLogin">返回登录</el-button>
              <el-button type="primary" @click="handleAddSubmit"
                >确定
              </el-button>
            </div>
          </template>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.background {
  background-image: url("/bg2.jpg"); /* 设置背景图片路径 */
  background-size: cover; /* 让背景图片覆盖整个屏幕 */
  background-position: center; /* 将背景图片居中 */
  height: 100vh; /* 使容器占据整个视口的高度 */
}

.container {
  display: flex;
  justify-content: center; /* 在主轴上居中 */
  align-items: center; /* 在交叉轴上居中 */
  height: 100vh; /* 使容器占据整个视口的高度 */
}

.centered-div {
  width: 600px; /* 宽度为600像素 */
}
</style>
