import { createRouter, createWebHistory } from 'vue-router'
import NotFound from '../views/error/NotFound'
import UserAccountLoginView from '../views/UserAccountLoginView.vue'
import UserAccountRegisterView from '../views/UserAccountRegisterView.vue'
import IndexView from '../views/IndexView.vue'
import store from '../store/index'
import ConsultationRecordView from "@/views/ConsultationRecordView.vue";
import ReserveRecordView from "@/views/ReserveRecordView.vue";
import SigningRecordView from "@/views/SigningRecordView.vue";
import signInfoView from "@/views/SignInfoView.vue";

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
    path: "/consultations/",
    name: "consultations_index",
    component: ConsultationRecordView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/reserve/",
    name: "reserve_index",
    component: ReserveRecordView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/signing/",
    name: "signing_index",
    component: SigningRecordView,
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
    path: "/view/SignInfoView/",
    name: "sign_info_view",
    component: signInfoView,
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
