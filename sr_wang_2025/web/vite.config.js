import path from "path";
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"), // 配置别名：将 @ 指向'src'目录
    },
  },
  server: {
    host: "0.0.0.0", // 解决vite use--host to expose
    port: 3000, // 设置端口号
    open: true, // 服务启动后自动打开浏览器
  },
});
