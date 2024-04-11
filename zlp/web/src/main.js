import { createApp } from "vue";
import { createPinia } from "pinia";
import router from "@/utils/router";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import App from "./App.vue";
import "./style.css";
import piniaPersist from 'pinia-plugin-persist'

const app = createApp(App);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}
const pinia = createPinia();
pinia.use(piniaPersist)

app.use(ElementPlus);
app.use(router);
app.use(pinia);
app.mount("#app");
