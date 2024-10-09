<script setup>
import router from "@/utils/router.js";

document.title = "详情";

// 导入所需模块和库
import moment from "moment";
import { onMounted, reactive, ref } from "vue";
import { ElForm, ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import { useThrottleFn } from "@vueuse/core";
import { Delete, Plus, RefreshRight, Star } from "@element-plus/icons-vue";
import { useMainStore } from "@/utils/store.js";

// 使用主要的store模块
const mainStore = useMainStore();
const evaluationList = ref([]);
const type = ref({
  medicine: "default",
  shop: "default",
}); //收藏状态

//使用节流函数封装更新提交操作
const addCart = useThrottleFn(() => {
  request({
    url: "/api/cart/add",
    data: {
      medicineid: mainStore.tmpData.id,
    },
  }).then(() => {
    ElMessage.success("操作成功");
  });
}, 500);

//检查收藏
function check() {
  if (mainStore.tmpData.type == "药品" || mainStore.tmpData.type == "店铺") {
    let shopid = 0;
    if (mainStore.tmpData.shop) {
      shopid = mainStore.tmpData.shop.id;
    }
    let medicineid = 0;
    if (mainStore.tmpData.id) {
      medicineid = mainStore.tmpData.id;
    }
    request({
      url: "/api/bookmark/check",
      data: {
        userid: mainStore.curuser.id,
        medicineid: medicineid,
        shopid: shopid,
      },
    }).then(({ data }) => {
      type.value = data;
    });
  }
}

function sendMsg() {
  request({
    url: "/api/message/add",
    data: {
      userid: mainStore.curuser.id,
      shopid: mainStore.tmpData.shop.id,
      content: "",
      typee: "",
    },
  }).then(({}) => {
    router.push("/message2");
  });
}

//切换收藏
function toggle(dataid, typee) {
  request({
    url: "/api/bookmark/toggle",
    data: {
      dataid: dataid,
      typee: typee,
      userid: mainStore.curuser.id,
    },
  }).then(() => {
    ElMessage.success("操作成功");
    if (typee == "药品") {
      if (type.value.medicine == "default") {
        type.value.medicine = "warning";
      } else {
        type.value.medicine = "default";
      }
    } else {
      if (type.value.shop == "default") {
        type.value.shop = "warning";
      } else {
        type.value.shop = "default";
      }
    }
  });
}

function listEvaluation() {
  request({
    url: "/api/order/evaluation",
    data: {
      medicineid: mainStore.tmpData.id,
    },
  }).then(({ data }) => {
    evaluationList.value = data;
  });
}

// 页面加载时进行查询
onMounted(() => {
  mainStore.active = router.currentRoute.value.path; // 初始化当前激活菜单项
  if (mainStore.tmpData.type == "药品" || mainStore.tmpData.type == "店铺") {
    check();
    listEvaluation();
  }
});
</script>

<template>
  <div style="height: 10px"></div>

  <el-card v-if="mainStore.tmpData.type == '药品'">
    <template #header>
      <div
        style="
          display: flex;
          justify-content: space-between;
          align-items: center;
        "
      >
        <div>药品详情</div>
        <div>
          <el-button
            :type="type.medicine"
            :icon="Star"
            circle
            @click="toggle(mainStore.tmpData.id, '药品')"
          />
        </div>
      </div>
    </template>
    <el-row>
      <el-col :span="8">
        <el-image
          :preview-teleported="true"
          style="
            width: 100%;
            aspect-ratio: 1 / 1;
            display: block;
            object-fit: cover;
          "
          :src="mainStore.tmpData.img"
          :preview-src-list="[mainStore.tmpData.img]"
          fit="cover"
        />
      </el-col>
      <el-col :span="16">
        <div
          style="
            padding-left: 20px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            gap: 10px;
          "
        >
          <div>
            药品名称：
            <span style="font-size: large; font-weight: bold">
              {{ mainStore.tmpData.namee }}
            </span>
          </div>
          <div>
            单价：
            <span style="color: red">{{ mainStore.tmpData.price }}元</span>
          </div>
          <div>
            <el-button type="danger" @click="addCart">加入购物车</el-button>
          </div>
          <div>
            药品描述:
            <pre style="font-size: 16px; white-space: pre-wrap">
             {{ mainStore.tmpData.description }}
            </pre>
          </div>
        </div>
      </el-col>
    </el-row>
  </el-card>

  <el-card
    style="margin-top: 10px"
    v-if="mainStore.tmpData.type == '药品' && evaluationList.length != 0"
  >
    <template #header>
      <div>评价</div>
    </template>
    <el-row
      v-for="item in evaluationList"
      style="border: 1px solid #f2f3f5; padding: 10px; margin-top: 5px"
    >
      <el-col :span="4">
        <div style="display: flex; flex-direction: column; align-items: center">
          <el-image
            :preview-teleported="true"
            style="
              width: 70%;
              aspect-ratio: 1 / 1;
              display: block;
              object-fit: cover;
            "
            :src="item.user.img"
            :preview-src-list="[item.user.img]"
            fit="cover"
          />
          <div style="font-size: large; font-weight: bold">
            {{ item.user.namee }}
          </div>
        </div>
      </el-col>
      <el-col :span="20">
        <div
          style="
            padding-left: 20px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            gap: 10px;
          "
        >
          <div>
            评分：
            <el-rate v-model="item.rate" disabled />
          </div>
          <div>
            评价:
            <pre style="font-size: 16px; white-space: pre-wrap">
             {{ item.content }}
            </pre>
          </div>
        </div>
      </el-col>
    </el-row>
  </el-card>

  <el-card
    v-if="
      (mainStore.tmpData.type == '药品' || mainStore.tmpData.type == '店铺') &&
      mainStore.tmpData.shop
    "
    style="margin-top: 10px"
  >
    <template #header>
      <div
        style="
          display: flex;
          justify-content: space-between;
          align-items: center;
        "
      >
        <div>店铺详情</div>
        <div>
          <el-button
            :type="type.shop"
            :icon="Star"
            circle
            @click="toggle(mainStore.tmpData.shop.id, '店铺')"
          />
        </div>
      </div>
    </template>
    <el-row>
      <el-col :span="8">
        <el-image
          :preview-teleported="true"
          style="
            width: 100%;
            aspect-ratio: 1 / 1;
            display: block;
            object-fit: cover;
          "
          :src="mainStore.tmpData.shop.img"
          :preview-src-list="[mainStore.tmpData.shop.img]"
          fit="cover"
        />
      </el-col>
      <el-col :span="16">
        <div
          style="
            padding-left: 20px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            gap: 10px;
          "
        >
          <div>
            店铺名称：
            <span style="font-size: large; font-weight: bold">
              {{ mainStore.tmpData.shop.namee }}
            </span>
          </div>
          <div>
            <el-button type="danger" @click="sendMsg">在线沟通</el-button>
          </div>
          <div>
            店铺介绍:
            <pre style="font-size: 16px; white-space: pre-wrap">
             {{ mainStore.tmpData.shop.shopcontent }}
            </pre>
          </div>
        </div>
      </el-col>
    </el-row>
  </el-card>

  <el-card v-if="mainStore.tmpData.type == '公告'">
    <template #header>
      <div style="text-align: center; font-weight: bold">
        {{ mainStore.tmpData.title }}
      </div>
    </template>
    <div
      style="
        text-align: right;
        font-size: small;
        color: gray;
        margin-right: 20px;
      "
    >
      {{ moment(mainStore.tmpData.createtime).format("YYYY-MM-DD HH:mm:ss") }}
    </div>
    <div>
      <pre style="font-size: 16px; white-space: pre-wrap">
        {{ mainStore.tmpData.content }}
      </pre>
    </div>
  </el-card>

  <el-card v-if="mainStore.tmpData.type == '知识'">
    <template #header>
      <div style="text-align: center; font-weight: bold">
        {{ mainStore.tmpData.title }}
      </div>
    </template>
    <div
      style="
        text-align: right;
        font-size: small;
        color: gray;
        margin-right: 20px;
      "
    >
      {{ moment(mainStore.tmpData.createtime).format("YYYY-MM-DD HH:mm:ss") }}
    </div>
    <div style="text-align: center">
      <el-image
        :preview-teleported="true"
        style="width: 500px"
        :src="mainStore.tmpData.img"
        :preview-src-list="[mainStore.tmpData.img]"
        fit="contain"
      />
    </div>

    <div>
      <pre style="font-size: 16px; white-space: pre-wrap">
        {{ mainStore.tmpData.content }}
      </pre>
    </div>
  </el-card>

  <div style="height: 10px"></div>
</template>

<style scoped></style>
