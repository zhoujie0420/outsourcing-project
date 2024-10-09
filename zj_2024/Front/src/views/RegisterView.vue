<template>
  <div class="register-container">
    <h2>注册</h2>
    <form @submit.prevent="register">
      <input type="text" v-model="username" placeholder="用户名"/>
      <input type="password" v-model="password" placeholder="密码"/>
      <input type="password" v-model="password" placeholder="年龄"/>
      <input type="password" v-model="password" placeholder="邮箱"/>
      <div class="login-error-msg">{{ message }}</div>

      <button type="submit">注册</button>
    </form>
    <p>已有账号？
      <router-link to="/login-view">立即登录</router-link>
    </p>
  </div>
</template>

<script>
import {ref} from "vue";
import router from "@/router";
import {apiUrl} from "../../config";
import $ from "jquery";

export default {
  setup() {
    const username = ref('');
    const password = ref('');
    let message = ref('');

    const register = () => {
      $.ajax({
        url: `${apiUrl}/api/user/account/register/`,
        type: "post",
        data: {
          username: username.value,
          password: password.value,
        },
        success(resp) {
          if (resp.code === 200) {
            console.log("Register successful:", resp)
            router.push("/login-view");
          } else {
            message.value = resp.message
          }
        },
      });
    }


    return {
      username,
      password,
      register,
      message
    };
  }
};
</script>

<!-- 样式略 -->
<style scoped>
.register-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.register-container h2 {
  text-align: center;
}

.register-container form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}

.register-container input[type="text"],
.register-container input[type="password"] {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.register-container button {
  padding: 8px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>