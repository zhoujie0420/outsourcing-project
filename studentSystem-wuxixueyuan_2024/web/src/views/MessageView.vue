<template>
  <ContentField>

    <el-input
        v-model="textarea"
        maxlength="30"
        style="width: 1024px; margin-top: 20px;"
        placeholder="Please input"
        show-word-limit
        type="textarea"
        size="large"
        rows="10"
    />
    <el-button type="success" style="margin-left: 10px" @click="addMessage">发帖</el-button>
    <el-divider/>

    <el-card v-for="info in messageList" :key="info.id" style="width: 1200px ;margin-top: 3px" shadow="hover">
      <div class="clearfix">
        <h4 style="display: inline-block; margin-right: 800px;">{{ info.username }}</h4>
        <div style="display: inline-block;">{{ info.createTime }}</div>
      </div>

      <div style="margin-left: 20px">{{ info.message }}</div>
    </el-card>


  </ContentField>
</template>

<script setup>
import ContentField from '../components/ContentField.vue';
import $ from "jquery";
import {apiUrl} from "../../config";
import store from "@/store";
import {ref} from "vue";
import {ElNotification} from "element-plus";

const textarea = ref('');
const messageList = ref([]);
getMessage()

function getMessage() {
  $.ajax({
    url: `${apiUrl}/api/user/message/getMessageList/`,
    type: "get",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        messageList.value = resp.data;
      } else {
        console.log(resp.message);
      }
    },
  });
}

function addMessage() {
  $.ajax({
    url: `${apiUrl}/api/user/message/addMessage/`,
    type: "post",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    data: {
      message: textarea.value
    },
    success(resp) {
      if (resp.code === 200) {
        getMessage()
        textarea.value = ''
        ElNotification({
          title: 'Success',
          message: '提交成功',
          type: 'success',
        })
      } else {
        ElNotification({
          title: 'Error',
          message: '提交失败',
          type: 'error',
        })      }
    },
  });
}


</script>
<style>
.custom-table .el-table__body tr {
  height: 100px; /* 调整行高为50像素 */
}
</style>
