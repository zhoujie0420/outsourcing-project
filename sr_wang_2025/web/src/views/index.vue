<script setup>
import {useMainStore} from "@/utils/store.js"; // 导入 main store
import {ArrowDown} from "@element-plus/icons-vue"; // 导入箭头图标组件
import router from "@/utils/router.js"; // 导入路由模块
import {onMounted, ref} from "vue";
import {zhCn} from "element-plus/es/locale/index";
import request from "@/utils/request.js"; // 导入 Vue 相关模块

const mainStore = useMainStore(); // 获取 main store 实例
const title = mainStore.setting.title; // 获取后台管理系统标题
const menu = mainStore.getMenu(); // 获取菜单列表

const locale = zhCn;
const intervalId = ref({});

// 跳转到指定路径
function go(path) {
  router.push(path);
}

// 退出登录
function logout() {
  sessionStorage.removeItem("token"); // 移除 token
  sessionStorage.removeItem("usertype"); // 移除用户类型
  mainStore.curuser = {}; // 清空当前用户信息
  mainStore.tmpData = {};
  clearInterval(intervalId.value);
  router.push("/login"); // 重置路由
}

function loadContact() {
  const token = sessionStorage.getItem("token");
  if (token) {
    intervalId.value = setInterval(function () {
      request({
        url: "/api/message/listContactUser",
        data: {},
      }).then(({ data }) => {
        mainStore.contactData = data;
      });
    }, 1000);
  }
}
// 在组件挂载完成后执行
onMounted(() => {
  if (mainStore.curuser.typee == "药店") {
    loadContact();
  }
});
</script>

<template>
  <!--左-->
  <div
      style="
      position: fixed;
      width: 200px;
      height: 60px;
      top: 0;
      left: 0;
      display: flex;
      justify-content: center;
      align-items: center;
      text-align: center;
      background-color: #2d3748;
      border-right: solid 1px var(--el-menu-border-color);
      box-sizing: border-box;
      z-index: 999;
    "
  >
    <span
        style="color: white; margin-left: 5px; font-weight: bold; font-size: 14px"
    >{{ title }}</span
    >
  </div>

  <el-menu
      :default-active="mainStore.active"
      style="
      width: 200px;
      margin-top: 60px;
      height: calc(100vh - 60px);
      overflow-y: auto;
    "
      active-text-color="#ffd04b"
      background-color="#304156"
      text-color="#fff"
      class="menu"
  >
    <el-menu-item
        v-for="item in menu"
        :index="item.path"
        @click="go(item.path)"
    >
      <span>{{ item.name }}</span>
    </el-menu-item>
  </el-menu>

  <!--顶-->
  <div
      style="
      background-color: white;
      border-bottom: 1px solid #e4e7ed;
      width: calc(100vw - 200px);
      height: 60px;
      position: fixed;
      top: 0;
      left: 200px;
      box-sizing: border-box;
      display: flex;
      justify-content: flex-end;
      align-items: center;
    "
  >
    <el-dropdown style="height: 60px">
      <span
          class="el-dropdown-link"
          style="
          color: var(--el-color-primary);
          cursor: pointer;
          display: flex;
          align-items: center;
          margin-right: 20px;
        "
      >
        {{ mainStore.curuser.namee }}
        <el-icon class="el-icon--right">
          <arrow-down/>
        </el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="go('/my')">个人中心</el-dropdown-item>
          <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
  <!--主-->
  <div
      style="
      background-color: #f2f3f5;
      width: calc(100vw - 200px);
      height: calc(100vh - 60px);
      position: fixed;
      top: 60px;
      left: 200px;
      overflow-y: auto;
    "
  >
    <el-config-provider :locale="locale">
      <RouterView></RouterView>
    </el-config-provider>
  </div>
</template>
<style scoped>
/* 隐藏滚动条但仍允许滚动 */
.menu::-webkit-scrollbar {
  display: none;
}

.el-menu-item:hover {
  background-color: black;
}

.el-dropdown-link:focus-visible {
  outline: unset;
}
</style>
