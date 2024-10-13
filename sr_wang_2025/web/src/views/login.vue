<script setup>
document.title = "登录"; // 设置页面标题

import { ElForm, ElMessage } from "element-plus";
import { reactive, ref } from "vue";
import { useMainStore } from "@/utils/store.js"; // 导入 main store
import { useThrottleFn } from "@vueuse/core";
import request from "@/utils/request.js"; // 导入请求模块
import router from "@/utils/router.js"; // 导入路由模块

const mainStore = useMainStore(); // 获取 main store 实例
const formDataRef = ref(ElForm); // 表单数据引用
const formData = reactive({
  // 响应式表单数据，初始化为默认用户名和密码
  username: "",
  pwd: "",
  typee: "用户",
});
const rules = reactive({
  // 表单校验规则
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  pwd: [{ required: true, message: "请输入密码", trigger: "blur" }],
  typee: [{ required: true, message: "请选择角色", trigger: "blur" }],
});

const options = ref([
  {
    value: "管理员",
    label: "管理员",
  },
  {
    value: "工作人员",
    label: "工作人员",
  },
  {
    value: "用户",
    label: "用户",
  },
]);

// 处理登录请求，使用节流函数限制请求频率
const handleLogin = useThrottleFn(() => {
  formDataRef.value.validate((valid) => {
    // 校验表单
    if (valid) {
      request({
        // 发送登录请求
        url: "/login", // 请求地址
        data: formData, // 请求数据
      }).then(({ data }) => {
        // 登录成功后处理返回的用户数据
        mainStore.curuser = data; // 更新当前用户信息到 main store
        sessionStorage.setItem("token", data.token); // 将 token 存储到 sessionStorage
        sessionStorage.setItem("usertype", data.typee); // 将用户类型存储到 sessionStorage
        router.push("/"); // 跳转到首页
      });
    }
  });
}, 500);
</script>

<template>
  <div class="background">
    <div class="container">
      <div class="centered-div">
        <el-card>
          <template #header>
            <div style="text-align: center">
              <span style="font-weight: bold; font-size: 20px">
                {{ mainStore.setting.title }}
              </span>
            </div>
          </template>
          <el-form
            ref="formDataRef"
            :model="formData"
            label-width="80px"
            :rules="rules"
          >
            <el-form-item label="角色" prop="typee">
              <el-select
                v-model="formData.typee"
                placeholder="请选择"
                style="width: 250px"
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
                v-model="formData.username"
                placeholder="请输入用户名"
                style="width: 250px"
              />
            </el-form-item>
            <el-form-item label="密码" prop="pwd">
              <el-input
                v-model="formData.pwd"
                type="password"
                placeholder="请输入密码"
                :show-password="true"
                style="width: 250px"
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <div
              style="
                text-align: center;
                display: flex;
                margin-right: 30px;
                margin-left: 30px;
              "
            >
              <!-- 登录按钮 -->
              <el-button
                type="primary"
                size="large"
                @click.prevent="handleLogin"
                style="flex-grow: 1"
              >
                登录
              </el-button>
              <!-- 登录按钮 -->
              <el-button
                size="large"
                @click.prevent="router.push('/reg')"
                style="flex-grow: 1"
              >
                注册
              </el-button>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.background {
  background-image: url("/bg4.png"); /* 设置背景图片路径 */
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
  width: 400px;
  height: 300px;
}
</style>
