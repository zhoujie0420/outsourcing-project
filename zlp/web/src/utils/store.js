import {defineStore} from "pinia";
import {reactive, ref} from "vue";
import router from "@/utils/router.js";

// 定义名为 "main" 的 Pinia store
export const useMainStore = defineStore(
    "main",
    () => {
        // 使用 ref() 和 reactive() 创建响应式数据
        const curuser = ref({}); // 当前用户信息
        const active = ref(""); // 当前激活菜单
        const tmpData = ref({}); // 临时数据，传值用
        const contactData = ref([]);
        const setting = {
            // 设置信息
            title: "药房商城系统",
            uploadURL: "http://localhost:8080/upload",
        };
        const adminMenu = ref([
            // 管理员菜单列表
            {name: "用户管理", path: "/user"},
            {name: "知识管理", path: "/knowledge"},
            {name: "公告管理", path: "/notice"},
            {name: "药店管理", path: "/shop"},
            {name: "药品管理", path: "/medicine"},
            {name: "销量分析", path: "/statistics"},
            {name: "评价分析", path: "/order"},
        ]);
        const shopMenu = ref([
            // 管理员菜单列表
            {name: "店铺管理", path: "/shop1"},
            {name: "药品管理", path: "/medicine1"},
            {name: "订单管理", path: "/order1"},
            {name: "报表分析", path: "/statistics1"},
            {name: "在线消息", path: "/message1"},
        ]);
        const userMenu = ref([
            // 管理员菜单列表
            {name: "首页", path: "/home"},
            {name: "知识普及", path: "/knowledge2"},
            {name: "药品商城", path: "/medicine2"},
            {name: "在线消息", path: "/message2"},
            {name: "Ai问诊", path: "/aiChat"},
        ]);

        // 获取菜单列表
        function getMenu() {
            if (curuser.value.typee == "管理员") {
                // 如果当前用户是管理员
                return adminMenu;
            } else if (curuser.value.typee == "药店") {
                return shopMenu;
            } else if (curuser.value.typee == "用户") {
                return userMenu;
            }
            return []; // 默认返回空数组
        }

        //检查登录
        function checkLogin() {
            const token = sessionStorage.getItem("token");
            if (!token) {
                sessionStorage.removeItem("token"); // 移除 token
                sessionStorage.removeItem("usertype"); // 移除用户类型
                curuser.value = {}; // 清空当前用户信息
                tmpData.value = {};
                router.push("/login");
                return false;
            }
            return true;
        }

        // 返回需要暴露给外部的数据和方法
        return {
            curuser,
            setting,
            active,
            tmpData,
            contactData,
            getMenu,
            checkLogin,
        };
    },
    {
        persist: {
            enabled: true, // 启用持久化
        },
    },
);
