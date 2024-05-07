import store from "@/store";
import configureVantToast from "@/plugins/configure-vant-toast";
import loadVantDialogCss from "@/plugins/load-vant-dialog-css";
import loadVantNotifyCss from "@/plugins/load-vant-notify-css";
import {Lazyload} from "vant";
import {createApp} from "vue";
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/user.css'

import App from './App.vue'
import router from './router'

import Editor from '@/components/EditorDemo.vue'
import Source from '@/components/SourceDemo.vue'
createApp(App)
    .component('EditorDemo', Editor)
    .component('SourceDemo', Source)
    .use(ElementPlus)

    .use(router)
    .use(store)
    .use(router)
    .use(configureVantToast)
    .use(loadVantDialogCss)
    .use(loadVantNotifyCss)
    .use(Lazyload, {
        lazyComponent: true,
    })
    .mount("#app");