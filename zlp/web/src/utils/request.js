import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
import router from "@/utils/router.js";

// 创建 axios 实例
const service = axios.create({
  baseURL: "http://localhost:8080", // 设置基础 URL
  timeout: 50000, // 设置请求超时时间
  headers: { "Content-Type": "application/json;charset=utf-8" }, // 设置请求头
  method: "post", // 设置请求方法
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem("token"); // 从 sessionStorage 中获取 token
    config.headers.set("token", token); // 设置请求头中的 token
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const { code, msg } = response.data; // 解构响应数据中的 code 和 msg
    if (code === 200) {
      return response.data; // 如果响应状态码为 200，返回响应数据
    } else if (code === 401) {
      sessionStorage.removeItem("token"); // 移除 token
      sessionStorage.removeItem("usertype"); // 移除用户类型
      router.push("/login");
      return;
    }
    // 响应数据为二进制流处理(Excel导出)
    if (response.data instanceof ArrayBuffer) {
      return response; // 如果响应数据为二进制流，直接返回响应
    }

    ElMessage.error(msg || "系统出错"); // 显示错误消息
    return Promise.reject(new Error(msg || "Error")); // 返回 Promise.reject()
  },
  (error) => {
    if (error.response.data) {
      const { msg } = error.response.data; // 获取错误消息
      ElMessage.error(msg || "系统出错"); // 显示错误消息
    }
    return Promise.reject(error.message); // 返回 Promise.reject()
  },
);

// 导出 axios 实例
export default service;
