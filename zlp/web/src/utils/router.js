import { createRouter, createWebHistory } from "vue-router";

//路由
const routes = [
  {
    path: "/my",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/my.vue"),
      },
    ],
  },
  {
    path: "/user",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/user.vue"),
      },
    ],
  },
  {
    path: "/shop",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/shop.vue"),
      },
    ],
  },
  {
    path: "/knowledge",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/knowledge.vue"),
      },
    ],
  },
  {
    path: "/notice",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/notice.vue"),
      },
    ],
  },
  {
    path: "/order",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/order.vue"),
      },
    ],
  },
  {
    path: "/medicine",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/medicine.vue"),
      },
    ],
  },
  {
    path: "/statistics",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/statistics.vue"),
      },
    ],
  },

  {
    path: "/shop1",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/shop1.vue"),
      },
    ],
  },
  {
    path: "/medicine1",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/medicine1.vue"),
      },
    ],
  },
  {
    path: "/order1",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/order1.vue"),
      },
    ],
  },
  {
    path: "/statistics1",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/statistics1.vue"),
      },
    ],
  },
  {
    path: "/message1",
    component: () => import("@/views/index.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/message1.vue"),
      },
    ],
  },

  {
    path: "/knowledge2",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/knowledge2.vue"),
      },
    ],
  },
  {
    path: "/home",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/home.vue"),
      },
    ],
  },
  {
    path: "/announcement",
    component: () => import("@/views/announcement.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/announcement.vue"),
      },
    ],
  },
  {
    path: "/my2",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/my2.vue"),
      },
    ],
  },
  {
    path: "/medicine2",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/medicine2.vue"),
      },
    ],
  },
  {
    path: "/bookmark2",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/bookmark2.vue"),
      },
    ],
  },
  {
    path: "/cart2",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/cart2.vue"),
      },
    ],
  },
  {
    path: "/article",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/article.vue"),
      },
    ],
  },
  {
    path: "/address2",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/address2.vue"),
      },
    ],
  },
  {
    path: "/order2",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/order2.vue"),
      },
    ],
  },
  {
    path: "/message2",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/message2.vue"),
      },
    ],
  },
  {
    path: "/aiChat",
    component: () => import("@/views/index2.vue"),
    children: [
      {
        path: "",
        component: () => import("@/views/aiChat.vue"),
      },
    ],
  },

  {
    path: "/login",
    component: () => import("@/views/login.vue"),
  },
  {
    path: "/reg",
    component: () => import("@/views/reg.vue"),
  },
  {
    path: "/",
    component: () => import("@/views/index.vue"),
  },
];

/**
 * 创建并配置路由器实例。
 * 使用现代浏览器的history模式来管理应用的路由，提供了更自然的URL。
 * 可以选择使用hash模式来兼容不支持history模式的旧浏览器或服务器配置。
 */
const router = createRouter({
  history: createWebHistory(), // 使用history模式
  //   history: createWebHashHistory(), // 使用hash模式
  routes,
});

//白名单
const list = ["/login", "/reg"];

//前置守卫
router.beforeEach((to, from) => {
  const token = sessionStorage.getItem("token");
  const usertype = sessionStorage.getItem("usertype");
  // 返回 false 以取消导航
  if (list.includes(to.path)) {
    return true;
  } else if (!token) {
    return "/login";
  }
  if (to.path == "/") {
    if (usertype == "管理员") {
      return { path: "/user", replace: true };
    } else if (usertype == "药店") {
      return { path: "/shop1", replace: true };
    } else {
      return { path: "/home", replace: true };
    }
  }
  return true;
});

export default router;
