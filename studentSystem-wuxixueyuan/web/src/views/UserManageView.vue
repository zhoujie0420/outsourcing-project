<template>
  <ContentField v-if="$store.state.user.role === 0">
    <el-button plain @click="centerDialogVisible = true">
      新增用户
    </el-button>
    <el-dialog
        v-model="centerDialogVisible"
        title="添加用户"
        width="500"
        align-center
    >
      <div style="display: flex; align-items: center; margin-bottom: 10px;">
        <label for="courseName" style="margin-right: 10px;">用户名称：</label>
        <el-input v-model="username" id="courseName" style="flex: 1;" placeholder="请输入"/>
      </div>

      <div style="display: flex; align-items: center; margin-bottom: 10px;">
        <label for="score" style="margin-right: 10px;">手机号码：</label>
        <el-input v-model="phone" id="score" style="flex: 1;" placeholder="请输入"/>
      </div>

      <div style="display: flex; align-items: center; margin-bottom: 10px;">
        <label for="yearDate" style="margin-right: 10px;">身份：</label>
        <el-select
            v-model="value"
            placeholder="Select"
            size="large"
            style="width: 240px"
        >
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="centerDialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="addUser()">
            Confirm
          </el-button>
        </div>
      </template>
    </el-dialog>
    <table class="table table-striped table-hover" style="text-align: center;">
      <thead>
      <tr>
        <th>用户姓名</th>
        <th>角色</th>
        <th>手机号</th>
        <th>状态</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="info in userInfos" :key="info.id">
        <td>{{ info.username }}</td>
        <td>{{ info.role === 1 ? '学生' : '教师' }}</td>
        <td>{{ info.phone }}</td>
        <td>{{ info.status === 1 ? '正常' : '禁用' }}</td>
        <td>
          <div class="custom-file">
            <el-button v-if="info.status === 1" type="primary" size="small" @click="update(info.id)">禁用</el-button>
            <el-button v-if="info.status === 0" type="primary" size="small" @click="update(info.id)">启用</el-button>

          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </ContentField>

  <ContentField v-if="$store.state.user.role === 2">
    <table class="table table-striped table-hover" style="text-align: center;">
      <thead>
      <tr>
        <th>用户姓名</th>
        <th>角色</th>
        <th>手机号</th>
        <th>状态</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="info in userInfos" :key="info.id">
        <td>{{ info.username }}</td>
        <td>{{ info.role === 1 ? '学生' : '教师' }}</td>
        <td>{{ info.phone }}</td>
        <td>{{ info.status === 1 ? '正常' : '禁用' }}</td>
      </tr>
      </tbody>
    </table>
  </ContentField>
</template>

<script setup>
import ContentField from '../components/ContentField.vue';
import {ref} from "vue";
import store from "@/store";
import $ from 'jquery';
import {apiUrl} from "../../config";
import {ElNotification} from "element-plus";

const userInfos = ref([]); // 创建响应式的userInfos
let centerDialogVisible = ref(false)
const username = ref()
const phone = ref()
const value = ref()

getUserList()

const options = [
  {
    value: '1',
    label: '学生',
  },
  {
    value: '2',
    label: '老师',
  },
]

function addUser(){
  console.log(value)
  $.ajax({
    url: `${apiUrl}/api/user/account/addUser/`,
    type: "post",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    data:{
      username:username.value,
      phone:phone.value,
      role:parseInt(value.value)
    },
    success(resp) {
      if (resp.code === 200) {
        getUserList()
        ElNotification({
          title: 'Success',
          message: '添加成功',
          type: 'success',
        })
      } else {
        console.log(resp.message);
      }
    },
  });
}
function update(id){
  $.ajax({
    url: `${apiUrl}/api/user/account/updateUserStatus/`,
    type: "post",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    data:{
      id:id
    },
    success(resp) {
      if (resp.code === 200) {
        getUserList()
        ElNotification({
          title: 'Success',
          message: '更新成功',
          type: 'success',
        })
      } else {
        console.log(resp.message);
      }
    },
  });
}
function getUserList() {
  $.ajax({
    url: `${apiUrl}/api/user/account/getUserList/`,
    type: "post",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        userInfos.value = resp.data; // 更新userInfos的值
      } else {
        console.log(resp.message);
      }
    },
  });
}
</script>

<style scoped>
.custom-file-upload {
  display: inline-block;
  padding: 2px 15px;
  border: 2px solid #ccc;
  border-radius: 5px;
  cursor: pointer;
}
</style>