import {createRouter, createWebHashHistory} from "vue-router";
// import {showLoadingToast} from "vant";
import usePeerStore from "@/store/peer";
let router = createRouter({
    history: createWebHashHistory(process.env.BASE_URL),
    routes: [
        {
            path: "/",
            component: () => import("@/views/MainEntranceView.vue"),
            children: [
                {
                    path: "/",
                    redirect: "/friends-list-view",
                    meta: {
                        requestAuth: true,
                    }
                },
                {
                    path: "friends-list-view",
                    component: () => import("@/views/FriendsListView.vue"),
                    meta: {
                        requestAuth: true,
                    }
                },
                {
                    path: "setting-view",
                    component: () => import("@/views/SettingsView.vue"),
                    meta: {
                        requestAuth: true,
                    }
                },
                {
                    path: "recordList-view",
                    component: () => import("@/views/RecordListView.vue"),
                    meta: {
                        requestAuth: true,
                    }
                }
            ]
        },
        {
            path:"/saveRecord",
            component:()=>import("@/views/SaveDoc.vue"),
        },
        {
            path: "/video-call-calling-view",
            component: () => import("@/views/VideoCallCallingView.vue"),
            meta: {
                requestAuth: true,
            }
        },
        {
            path: "/admin-view",
            component: () => import("@/views/adminView.vue"),
        },
        {
            path: "/video-call-answer-view",
            component: () => import("@/views/VideoCallAnswerView.vue"),
            meta: {
                requestAuth: true,
            }
        },
        {
            path: "/login-view",
            name: "login",
            component: () => import("@/views/LoginView.vue"),
            meta: {
                requestAuth: false,
            }
        },
        {
            path: "/register-view",
            name: "register",
            component: () => import("@/views/RegisterView.vue"),
            meta: {
                requestAuth: false,
            }
        },
    ]
});

router.beforeEach((to, from, next) => {
    const peerStore = usePeerStore();
    if (to.meta.requestAuth && !peerStore.is_login) {
        next({name: "login"});
    } else {
        next();
    }
})





export default router;