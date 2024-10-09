<template>
  <ContentField>
    <div style="display: flex; align-items: center;">
      <el-select
          v-model="value"
          placeholder="Select"
          size="large"
          style="width: 240px; margin-right: 10px;"
          @change="getStudent(value)"
      >
        <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.name"
            :value="item.id"
        />
      </el-select>
      <div style="margin-left: auto;">
        <el-button type="primary" @click="exportExcel()" >成绩导出</el-button>
      </div>
    </div>

    <table class="table table-striped table-hover" style="text-align: center;">
      <thead>
      <tr>
        <th>学生姓名</th>
        <th>手机号</th>
        <th>课程名</th>
        <th>成绩</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="info in studentInfos" :key="info.id">
        <td>{{ info.username }}</td>
        <td>{{ info.phone }}</td>
        <td>{{ options[0].name }}</td>
        <td>{{ info.score === null ? 0 : info.score }}</td>
        <td class="custom-file">
          <div class="custom-file">
            <el-button type="primary" size="small" @click="editScore(info.id)">编辑成绩</el-button>
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
import {ElMessage, ElMessageBox, ElNotification} from "element-plus";

const studentInfos = ref([]);
const value = ref('')
const options = ref([])

const nowCourseId = ref()
getCourseList();


function editScore(studentID) {
  ElMessageBox.prompt('Please input Student Score', 'Tip', {
    confirmButtonText: 'Submit',
    cancelButtonText: 'Cancel',
    inputErrorMessage: 'Student Score',
  })
      .then(({value}) => {
        $.ajax({
          url: `${apiUrl}/api/user/course/editScore/`,
          type: "post",
          headers: {
            Authorization: "Bearer " + store.state.user.token,
          },
          data: {
            score: value,
            studentId: studentID,
            courseId: nowCourseId.value
          },
          success(resp) {
            if (resp.code === 200) {
              options.value = resp.data;
            } else {
              console.log(resp.message);
            }
          },
        });
        getStudent(nowCourseId.value)
        ElNotification({
          title: 'Success',
          message: '编辑成功',
          type: 'success',
        })
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: 'Submit Fail',
        })
      })
}

function getCourseList() {
  $.ajax({
    url: `${apiUrl}/api/user/course/getCourseList/`,
    type: "get",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        options.value = resp.data;
      } else {
        console.log(resp.message);
      }
    },
  });
}
function exportExcel(){
  $.ajax({
    url: `${apiUrl}/api/user/course/getExcel/`,
    type: "get",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    xhrFields: {
      responseType: 'blob' // 告诉浏览器响应类型为二进制流
    },
    success: function(data) {
      // 创建一个Blob对象，用来存储从后端接收到的二进制流数据
      var blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      // 创建一个URL对象，指向Blob对象的URL
      var url = window.URL.createObjectURL(blob);
      // 创建一个a标签来模拟点击下载链接
      var a = document.createElement('a');
      a.href = url;
      a.download = 'users.xlsx'; // 设置下载文件的名称
      // 将a标签添加到页面中，并触发点击事件进行下载
      document.body.appendChild(a);
      a.click();
      // 下载完成后移除a标签和URL对象
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    },
    error: function(xhr, status, error) {
      // 处理错误情况
      console.error('下载Excel失败:', error);
    }
  });
}

function getStudent(id) {
  $.ajax({
    url: `${apiUrl}/api/user/course/getStudentsByCourseId/`,
    type: "post",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    data: {
      Id: id,
    },
    success(resp) {
      if (resp.code === 200) {
        studentInfos.value = resp.data;
        nowCourseId.value = id
      } else {
        console.log(resp.message);
      }
    },
  });
}
</script>

<style scoped>
.inner-table {
  width: 100%;
  border-collapse: collapse;
}

.inner-table th, .inner-table td {
  border: 1px solid #ddd;
  padding: 8px;
}

.inner-table th {
  background-color: #f2f2f2;
}
</style>
