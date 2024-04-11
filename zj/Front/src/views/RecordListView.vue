<template>
  <section>
    <van-nav-bar
        title="标题"
        left-text="返回"
        left-arrow
        @click-left="goBack"
    />
    <van-search v-model="searchKeyword" placeholder="请输入搜索关键词" />
  </section>
  <section v-if="records.length">
    <section v-for="info of records" :key="info">
      <record-card @click="showDetail(info.id)">
        <div class="record-info">
          <p class="date">{{ info.consultationDate }}</p>
          <p class="status">{{ info.status === 1 ? '已完成' : '未完成' }}</p>
        </div>
      </record-card>
      <section v-if="show[info.id] === 1">
        <van-field v-if="peerStore.role === 1" v-model="info.otherName" label="医生姓名" placeholder="请输入医生姓名" readonly />
        <van-field v-if="peerStore.role === 2" v-model="info.otherName" label="患者姓名" placeholder="请输入患者姓名" readonly />
        <van-field v-model="info.diagnosis" label="诊断结果" placeholder="请输入诊断结果" />
        <van-field v-model="info.prescription" label="处方" placeholder="请输入处方" />
<!--        <van-field v-model="info.videoUrl" label="问诊回放" placeholder="请输入问诊回放链接" readonly/>-->

        <el-button @click="goInfo()">详情</el-button>
        <el-button type="primary" @click="updateRecord(info)" >提交</el-button>
      </section>
    </section>
  </section>
</template>


<script setup>
import $ from "jquery";
import {apiUrl} from "../../config";
import {showToast} from "vant";
import usePeerStore from "@/store/peer";
import {ref} from "vue";
import router from "@/router";

let peerStore = usePeerStore();
const records = ref([]); // 创建响应式的userInfos
getRecords()
const show = ref({});

function goBack(){
  router.go(-1);
}
function goInfo(){
  router.push({path: '/saveRecord'});
}
function showDetail(id) {
  show.value[id] = show.value[id] === 1 ? 0 : 1;
}

function updateRecord(info) {
  $.ajax({
    url: `${apiUrl}/api/record/updateRecord/`,
    type: "post",
    data: {
      id: info.id,
      diagnosis: info.diagnosis,
      prescription: info.prescription,
      videoUrl: info.videoUrl,
    },
    headers: {
      Authorization: "Bearer " + peerStore.token,
    },
    success(resp) {
      if (resp.code === 200) {
        showToast("更新成功");
        getRecords();
      } else {
        showToast(resp.message);
      }
    },
  });
}
function getRecords() {
  let peerStore = usePeerStore();
  $.ajax({
    url: `${apiUrl}/api/record/getRecords/`,
    type: "post",
    headers: {
      Authorization: "Bearer " + peerStore.token,
    },
    success(resp) {
      if (resp.code === 200) {
        records.value = resp.data; // 更新userInfos的值
      } else {
        showToast(resp.message);
      }
    },
  });
}
</script>

<style scoped lang="scss">
.wide-button {
  width: 200px; /* 根据需要调整按钮宽度 */
  position: fixed;
  left: 50%;
  bottom: 80px; /* 调整底部距离根据需要 */
  transform: translateX(-50%);
}

.center-bottom {
  display: flex;
  justify-content: center;
}

.record-info {
  display: flex;
  justify-content: space-between;
}

.record-info .date {
  text-align: left;
  padding-right: 10px; /* 可根据实际情况调整间距 */
}

.record-info .status {
  text-align: right;
}

/* 样式可以根据自己的需求进行调整 */
.record-info {
  display: flex;
  justify-content: space-between;
}
.date {
  font-weight: bold;
}
.status {
  color: #999;
}
.right-aligned {
  width: 120px;
  display: block;
  margin: 0 auto;
}

</style>