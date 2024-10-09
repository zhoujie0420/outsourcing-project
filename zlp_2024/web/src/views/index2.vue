<script setup>
import {useMainStore} from "@/utils/store.js"; // 导入 main store
import {ArrowDown} from "@element-plus/icons-vue"; // 导入箭头图标组件
import router from "@/utils/router.js"; // 导入路由模块
import {onMounted, ref} from "vue";
import {zhCn} from "element-plus/es/locale/index";
import request from "@/utils/request.js";
import Home from "@/views/home.vue";
import Announcement from "@/views/announcement.vue"; // 导入 Vue 相关模块

const mainStore = useMainStore(); // 获取 main store 实例
const title = mainStore.setting.title; // 获取后台管理系统标题
const menu = mainStore.getMenu(); // 获取菜单列表
const locale = zhCn;
const intervalId = ref({});
const medicineList = ref([]);

// 跳转到指定路径
function go(path) {
  router.push(path);
}

// 退出登录
function logout() {
  sessionStorage.removeItem("token"); // 移除 token
  sessionStorage.removeItem("usertype"); // 移除用户类型
  mainStore.curuser = {}; // 清空当前用户信息
  clearInterval(intervalId.value);
  router.push("/login"); // 重置路由
}
getMedicineList();

function getMedicineList(){
      request({
        url: "/api/medicine/list",
        data: {},
      }).then(({data}) => {
        medicineList.value = data;
      });
}

function openArticle(row) {
  mainStore.tmpData = row;
  mainStore.tmpData.type = "药品";
  router.push("/article");
}


function loadContact() {
  const token = sessionStorage.getItem("token");
  if (token) {
    intervalId.value = setInterval(function () {
      request({
        url: "/api/message/listContactShop",
        data: {},
      }).then(({ data }) => {
        mainStore.contactData = data;
      });
    }, 1000);
  }
}
// 在组件挂载完成后执行
onMounted(() => {
  mainStore.active = router.currentRoute.value.path; // 初始化当前激活菜单项
  loadContact();

});
</script>


<template>
  <!--顶-->
  <div style=" background-color: #76cc8d;      border-bottom: 1px solid #e4e7ed;
      width: 100vw;
      height: 60px;
      position: fixed;
      top: 0;
      left: 0;
      box-sizing: border-box;
      display: flex;
      justify-content: space-between;
      align-items: center;
    ">
    <div
        style="
        width: 200px;
        display: flex;
        align-items: center;
        margin-left: 20px;
      "
    >
      <img src="/vite.svg" style="width: 25px; height: 25px"/>
      <span
          style="
          color: white;
          margin-left: 5px;
          font-weight: bold;
          font-size: 14px;
        "
      >{{ title }}</span
      >
    </div>
    <el-menu
        :default-active="mainStore.active"
        active-text-color="#ffd04b"
        background-color="#76cc8d"
        text-color="#fff"
        class="menu"
        mode="horizontal"
        :ellipsis="false"
    >
      <el-select
          v-model="value"
          multiple
          filterable
          remote
          reserve-keyword
          placeholder="查找"
          :remote-method="remoteMethod"
          :loading="loading"
          style="width: 240px; margin-top: 15px;"
      >
        <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
        />
      </el-select>
    </el-menu>
    <div style="width: 200px; text-align: right">
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
            <el-dropdown-item @click="go('/my2')">个人中心</el-dropdown-item>
            <el-dropdown-item @click="go('/address2')">
              地址管理
            </el-dropdown-item>
            <el-dropdown-item @click="go('/order2')">订单管理</el-dropdown-item>
            <el-dropdown-item @click="go('/cart2')">购物车</el-dropdown-item>
            <el-dropdown-item @click="go('/bookmark2')">
              我的收藏
            </el-dropdown-item>
            <el-dropdown-item divided @click="logout">
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>

  <!--主-->
  <div style="
      background-color: #f2f3f5;
      width: 100vw;
      height: calc(100vh - 60px);
      position: fixed;
      top: 60px;
      left: 0;
      overflow-y: auto;
      display: flex;
      justify-content: center;
    ">

      <el-col :span="4"><div class="grid-content ep-bg-purple" />
        <el-card style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span></span>
            </div>
          </template>
          <div v-for="item in medicineList" class="text item">
            {{ item.medicineCategoryName }}
            <div v-for="info in item.list" class="text item" style="font-size: 12px; " @click="openArticle(info)">
              {{ info.namee }}
            </div>
            <el-divider />
          </div>
        </el-card>
      </el-col>
      <el-col :span="14"><div class="grid-content ep-bg-purple" />
        <el-menu
            :default-active="mainStore.active"
            active-text-color="#ffd04b"
            background-color="#76cc8d"
            text-color="#fff"
            class="menu"
            mode="horizontal"
            :ellipsis="false"
        >
          <el-menu-item
              v-for="item in menu"
              :index="item.path"
              @click="go(item.path)"
          >
            <span>{{ item.name }}</span>
          </el-menu-item>
        </el-menu>
        <el-config-provider :locale="locale">
          <RouterView></RouterView>
        </el-config-provider>
      </el-col>
      <el-col :span="6"><div class="grid-content ep-bg-purple" />
        <announcement></announcement>
      </el-col>
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
