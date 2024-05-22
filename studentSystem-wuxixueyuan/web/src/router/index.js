import { createRouter, createWebHistory } from 'vue-router'
import NotFound from '../views/error/NotFound'
import UserAccountLoginView from '../views/UserAccountLoginView.vue'
import UserAccountRegisterView from '../views/UserAccountRegisterView.vue'
import IndexView from '../views/IndexView.vue'
import store from '../store/index'
import userManageView from "@/views/UserManageView.vue";
import LogListView from "@/views/LogListView.vue";
import MonitorView from "@/views/MonitorView.vue";
import CourseScheduleView from "@/views/CourseScheduleView.vue";
import CourseView from "@/views/CourseView.vue";
import RegisterScoreView from "@/views/RegisterScoreView.vue";
import CourseSelectionView from "@/views/CourseSelectionView.vue";
import MessageView from "@/views/MessageView.vue";
import CourseInfoView from "@/views/CourseInfoView.vue";

const routes = [
  {
    path: "/",
    name: "home",
    component: IndexView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/userManage/",
    name: "userManage",
    component: userManageView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/LogList/",
    name: "logList",
    component: LogListView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/Monitor/",
    name: "monitor",
    component: MonitorView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/Course/:courseId/",
    name: "course_info",
    component: CourseInfoView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/CourseSchedule/",
    name: "course_schedule",
    component: CourseScheduleView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/Course/",
    name: "course",
    component: CourseView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/Message/",
    name: "message",
    component: MessageView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/CourseSelection/",
    name: "course_select",
    component: CourseSelectionView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/RegistrationScore/",
    name: "registration_score",
    component: RegisterScoreView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/404/",
    name: "404",
    component: NotFound,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.requestAuth && !store.state.user.is_login) {
    next({name: "user_account_login"});
  } else {
    next();
  }
})

export default router
