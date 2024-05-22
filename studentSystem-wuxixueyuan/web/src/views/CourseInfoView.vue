<template>
  <ContentField v-if="courseInfo && TeaInfo">

    <el-descriptions
        title="课程信息"
        :column="4"
        :size="'large'"
        border
    >
      <el-descriptions-item  label="名称" :span="1" >{{ courseInfo.name }}</el-descriptions-item>
      <el-descriptions-item label="地点" :span="1">{{courseInfo.address}}</el-descriptions-item>
      <el-descriptions-item label="上课周期" :span="2">{{ courseInfo.classCycle }}</el-descriptions-item>
      <el-descriptions-item label="简介">
        {{ courseInfo.info}}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider />

    <el-descriptions
        title="老师信息"
        :column="4"
        :size="'large'"
        border
    >
      <el-descriptions-item  label="姓名" :span="1" >{{ TeaInfo.username }}</el-descriptions-item>
      <el-descriptions-item label="年龄" :span="2">{{TeaInfo.age}}</el-descriptions-item>
      <el-descriptions-item label="手机号" :span="2">{{ TeaInfo.phone }}</el-descriptions-item>
      <el-descriptions-item label="办公室" :span="2">{{TeaInfo.office}}</el-descriptions-item>
      <el-descriptions-item label="所属学院" :span="2">{{ TeaInfo.college }}</el-descriptions-item>
      <el-descriptions-item label="简介">
        {{ TeaInfo.introduction}}
      </el-descriptions-item>
    </el-descriptions>

  </ContentField>

</template>
<script setup>
import ContentField from '../components/ContentField.vue';
import $ from "jquery";
import {apiUrl} from "../../config";
import store from "@/store";
import {useRoute} from "vue-router";
import {ref} from "vue";
const route = useRoute();
const courseInfo = ref(); // 创建响应式的 courseInfo
const TeaInfo = ref(); // 创建响应式的 courseInfo

getInfo(route.params.courseId); // 使用 route.params.courseId 获取路由参数
function getInfo(id){
  $.ajax({
    url: `${apiUrl}/api/user/course/getCourseById/`,
    type: "post",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    data: {
      id: id,
    },
    success(resp) {
      if (resp.code === 200) {
        courseInfo.value = resp.data; // 更新userInfos的值
      }
    },
  });
}


getTeaInfo(route.params.courseId); // 使用 route.params.courseId 获取路由参数
function getTeaInfo(id){
  $.ajax({
    url: `${apiUrl}/api/user/account/getTeaByCourseId/`,
    type: "post",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    data: {
      id: id,
    },
    success(resp) {
      if (resp.code === 200) {
        console.log(resp.data);
        TeaInfo.value = resp.data; // 更新userInfos的值
      }
    },
  });
}
</script>