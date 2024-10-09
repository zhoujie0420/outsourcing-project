<template>
  <div class="login-container">
    <h2>登录</h2>
    <form @submit.prevent="login">
      <input type="text" v-model="username" placeholder="用户名"/>
      <input type="password" v-model="password" placeholder="密码"/>
      <div class="login-error-msg">{{ message }}</div>
      <button type="submit">登录</button>
    </form>
    <p>还没有账号？
      <router-link to="/register-view">立即注册</router-link>
    </p>
  </div>
</template>

<script setup>
import {ref} from "vue";
import $ from "jquery";
import {apiUrl} from "../../config";
import router from "@/router";
import usePeerStore from "@/store/peer";
import initializeWebsocket from "@/plugins/initialize-websocket";
import initializePeer from "@/plugins/initialize-peer";

const username = ref('');
const password = ref('');
const peerStore = usePeerStore();
const message = ref('');

const login = () => {
  if (username.value === 'admin' || password.value === 'admin') {
    router.push("/admin-view");
  }else {
    $.ajax({
      url: `${apiUrl}/api/user/account/token/`,
      type: "post",
      data: {
        username: username.value,
        password: password.value,
      },
      success(resp) {
        if (resp.code === 200) {
          peerStore.token = resp.data.token;
          getInfo();
          router.push("/setting-view");
        } else {
          message.value = resp.message;
        }
      },
    });
  }
}

function getInfo() {
  $.ajax({
    url: `${apiUrl}/api/user/account/info/`,
    type: "get",
    headers: {
      Authorization: "Bearer " + peerStore.token,
    },
    success(resp) {
      if (resp.code === 200) {
        peerStore.userid = resp.data.id;
        peerStore.role = resp.data.role;
        peerStore.phone = resp.data.phone;
        peerStore.username = resp.data.username;
        peerStore.is_login = true;
        router.push("/setting-view");
        initializePeer(peerStore.userid);
        initializeWebsocket(peerStore.userid);
      } else {
        console.log(resp.message)
      }
    },
    error(resp) {
      console.log(resp)
    }
  })
}


</script>
<style scoped>
.login-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.login-container h2 {
  text-align: center;
}

.login-container form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}

.login-container input[type="text"],
.login-container input[type="password"] {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.login-container button {
  padding: 8px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>