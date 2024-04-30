<template>
  <ContentField>
    <table class="table table-striped table-hover" style="text-align: center;">
      <thead>
      <tr>
        <th>课程名</th>
        <th>学分</th>
        <th>教师名</th>
        <th>上课时间</th>
        <th>状态</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="info in courseInfo" :key="info.id">
        <td>{{ info.name }}</td>
        <td>{{ info.score }}</td>
        <td>{{ info.teacher }}</td>
        <td>{{ info.time }}</td>
        <td>{{
            info.state === 0 ? "禁用" : info.state === 1 ? "已选" : info.state === 2 ? "时间冲突" : info.state === 3 ? "可选" : "未知状态"
          }}
        </td>
        <td>
          <div class="custom-file">
            <el-button v-if="info.state === 0 " disabled type="success" size="small">选课
            </el-button>
            <el-button v-if="info.state === 1 " type="danger" size="small" @click="update(info.id,0)">退课
            </el-button>
            <el-button v-if="info.state === 2 " disabled type="success" size="small">选课
            </el-button>
            <el-button v-if="info.state === 3 " type="danger" size="small" @click="update(info.id, 1)">选课
            </el-button>
          </div>
        </td>
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

const courseInfo = ref([]); // 创建响应式的userInfos

getCourseAllList()

function update(id, state) {
  $.ajax({
    url: `${apiUrl}/api/user/course/updateStudentCourse/`,
    type: "post",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    data: {
      id: id,
      studentCourseState: state,
    },
    success(resp) {
      if (resp.code === 200) {
        getCourseAllList();
        // eslint-disable-next-line no-undef
        ElNotification({
          title: 'Success',
          message: '操作成功',
          type: 'success',
        })
      } else {
        ElNotification({
          title: 'Error',
          message: '操作失败',
          type: 'error',
        })      }
    },
  });
}

function getCourseAllList() {
  $.ajax({
    url: `${apiUrl}/api/user/course/getCourseAllList/`,
    type: "get",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        courseInfo.value = resp.data; // 更新userInfos的值
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