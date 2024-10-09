<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
      <router-link class="navbar-brand" :to="{name: 'home'}">课程管理系统</router-link>
      <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <!--          stu-->
            <li  v-if="$store.state.user.role === 1" class="nav-item">
            <router-link :class="route_name === 'course_schedule' ? 'nav-link active' : 'nav-link'" :to="{name: 'course_schedule'}">课程表
            </router-link>
            </li>
            <li v-if="$store.state.user.role === 1" class="nav-item">
            <router-link :class="route_name === 'course_select' ? 'nav-link active' : 'nav-link'" :to="{name: 'course_select'}">选课
            </router-link>
            </li>
            <li v-if="$store.state.user.role === 1" class="nav-item">
            <router-link :class="route_name === 'course' ? 'nav-link active' : 'nav-link'" :to="{name: 'course'}">课程查看
            </router-link>
            </li>
          <li v-if="$store.state.user.role === 1" class="nav-item">
            <router-link :class="route_name === 'message' ? 'nav-link active' : 'nav-link'" :to="{name: 'message'}">学生留言
            </router-link>
          </li>
          <!--          admin-->
          <li v-if="$store.state.user.role === 0" class="nav-item">
            <router-link :class="route_name === 'userManage' ? 'nav-link active' : 'nav-link'"
                         :to="{name: 'userManage'}">用户管理
            </router-link>
          </li>
          <li v-if="$store.state.user.role === 0" class="nav-item">
            <router-link :class="route_name === 'logList' ? 'nav-link active' : 'nav-link'"
                         :to="{name: 'logList'}">日志查看
            </router-link>
          </li>
          <li v-if="$store.state.user.role === 0" class="nav-item">
            <router-link :class="route_name === 'monitor' ? 'nav-link active' : 'nav-link'"
                         :to="{name: 'monitor'}">监控安全
            </router-link>
          </li>
          <li v-if="$store.state.user.role === 2" class="nav-item">
            <router-link :class="route_name === 'course' ? 'nav-link active' : 'nav-link'"
                         :to="{name: 'course'}">课程查看
            </router-link>
          </li>
          <li v-if="$store.state.user.role === 2" class="nav-item">
            <router-link :class="route_name === 'userManage' ? 'nav-link active' : 'nav-link'"
                         :to="{name: 'userManage'}">学生信息
            </router-link>
          </li>
          <li v-if="$store.state.user.role === 2" class="nav-item">
            <router-link :class="route_name === 'registration_score' ? 'nav-link active' : 'nav-link'"
                         :to="{name: 'registration_score'}">登记成绩
            </router-link>
          </li>
        </ul>
        <ul class="navbar-nav" v-if="$store.state.user.is_login">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              {{ $store.state.user.username }} / {{$store.state.user.role === 1 ? '学生' : ($store.state.user.role === 2 ? '老师' : ($store.state.user.role === 0 ? '管理员' : '')) }}
            </a>
            <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
              <li><a class="dropdown-item" href="#" @click="logout">退出</a></li>
            </ul>
          </li>
        </ul>
        <ul class="navbar-nav" v-else-if="!$store.state.user.pulling_info">
          <li class="nav-item">
            <router-link class="nav-link" :to="{name: 'user_account_login' }" role="button">
              登录
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" :to="{name: 'user_account_register'}" role="button">
              注册
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
  </template>

  <script>
  import { useRoute } from 'vue-router'
  import { computed } from 'vue'
  import { useStore } from 'vuex';

  export default {
      setup() {
          const store = useStore();
          const route = useRoute();
          let route_name = computed(() => route.name)
          const logout = () => {
            store.dispatch("logout");
          }

          return {
              route_name,
              logout
          }
      }
  }
  </script>

  <style scoped>

  </style>