import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import vueEsign from 'vue-esign'

import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(ElementPlus).use(vueEsign).use(store).use(router).mount('#app')


