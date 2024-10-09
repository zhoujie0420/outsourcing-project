<template>
  <el-card style="margin-top: 10px; height: 600px">
    <template #header> 在线消息</template>
    <el-tabs tab-position="left" v-model="activeName" @tab-change="onTabChange">
      <el-tab-pane label="文心一言">
        <div style="height: 400px; border: 1px solid #f2f3f5; overflow-y: auto">
          <div v-for="(item, index) in chatHistory" :key="index" :class="{'sent-message': item.type === 'sent', 'received-message': item.type === 'received'}">
            {{ item.message }}
          </div>
        </div>
        <div style="margin-top: 10px; display: flex; align-items: center; gap: 10px;">
          <el-input v-model="msg" type="textarea" :autosize="{ minRows: 3 }" />
          <el-button type="primary" @click="handleSendMsg">发送</el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref } from 'vue';
import request from "@/utils/request"; // 导入请求模块

const chatHistory = ref([]);
const msg = ref('');

const handleSendMsg = async () => {
  chatHistory.value.push({ message: msg.value, type: 'sent' }); // 添加发送的消息到对话记录
  const response = await sendMsg();
  chatHistory.value.push({ message: response, type: 'received' }); // 添加接收的消息到对话记录
};

async function sendMsg() {
  try {
    const { data } = await request({
      url: "/api/message/aiChat",
      method: "post",
      data: {
        content: msg.value
      },
    });

    // 清空输入框
    msg.value = "";

    // 返回响应的数据
    return data;
  } catch (error) {
    console.error("发送消息出错：", error);
    return "发送消息失败";
  }
}
</script>

<style scoped>
.sent-message {
  text-align: right;
  margin-right: 20px;
}

.received-message {
  text-align: left;
  margin-left: 20px;
}
</style>
