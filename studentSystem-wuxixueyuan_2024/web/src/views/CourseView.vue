<template>
  <ContentField>
    <div v-if="$store.state.user.role === 0">
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
          <td>{{ info.state === 1 ? '正常' : '禁用' }}</td>
          <td>
            <div class="custom-file">
              <el-button type="primary" size="small" @click="update(info)">禁用</el-button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
    <div v-if="$store.state.user.role === 1 ">
      <table class="table table-striped table-hover" style="text-align: center;">
        <thead>
        <tr>
          <th>课程名</th>
          <th>学分</th>
          <th>教师名</th>
          <th>上课时间</th>
          <th>作业下载</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="info in courseInfo" :key="info.id">
          <td>{{ info.name }}</td>
          <td>{{ info.score }}</td>
          <td>{{ info.teacher }}</td>
          <td>{{ info.time }}</td>
          <td class="custom-file">
            <el-button v-if="info.url !== null" type="primary" size="small" @click="goToUrl(info.url)">作业查看</el-button>
            <el-button v-if="info.url === null" :disabled = "true"  type="primary" size="small" @click="goToUrl(info.url)">作业查看</el-button>

          </td>
        </tr>
        </tbody>
      </table>
    </div>
    <div v-if="$store.state.user.role === 2 ">
      <el-button plain @click="centerDialogVisible = true">
        申请开课
      </el-button>
      <el-dialog
          v-model="centerDialogVisible"
          title="开课信息"
          width="500"
          align-center
      >
        <div style="display: flex; align-items: center; margin-bottom: 10px;">
          <label for="courseName" style="margin-right: 10px;">课程名称：</label>
          <el-input v-model="CourseName" id="courseName" style="flex: 1;" placeholder="请输入"/>
        </div>

        <div style="display: flex; align-items: center; margin-bottom: 10px;">
          <label for="score" style="margin-right: 10px;">学分：</label>
          <el-input v-model="Score" id="score" style="flex: 1;" placeholder="请输入"/>
        </div>

        <div style="display: flex; align-items: center; margin-bottom: 10px;">
          <label for="yearDate" style="margin-right: 10px;">年份日期：</label>
          <el-input v-model="YearDate" id="yearDate" style="flex: 1;" placeholder="请输入"/>
        </div>

        <div style="display: flex; align-items: center; margin-bottom: 10px;">
          <label for="weekTime" style="margin-right: 10px;">周时间：</label>
          <el-input v-model="WeekTime" id="weekTime" style="flex: 1;" placeholder="请输入"/>
        </div>

        <div style="display: flex; align-items: center; margin-bottom: 10px;">
          <label for="dayTime" style="margin-right: 10px;">日时间：</label>
          <el-input v-model="DayTime" id="dayTime" style="flex: 1;" placeholder="请输入"/>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="centerDialogVisible = false">Cancel</el-button>
            <el-button type="primary" @click="addCourse">
              Confirm
            </el-button>
          </div>
        </template>
      </el-dialog>
      <table class="table table-striped table-hover" style="text-align: center;">
        <thead>
        <tr>
          <th>课程名</th>
          <th>学分</th>
          <th>上课时间</th>
          <th>作业状态</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="info in courseInfo" :key="info.id">
          <td>{{ info.name }}</td>
          <td>{{ info.score }}</td>
          <td>{{ info.time }}</td>
          <td>{{ info.url === null ? '未上传' : '已上传' }}</td>
          <td class="custom-file">
            <label :for="'customFile_' + info.id" class="custom-file-upload" :class="{ 'disabled': info.state === 0 }">
              <input type="file" @change="upload(info.id, $event)" :id="'customFile_' + info.id"
                     :disabled="info.state === 0" style="display: none;">
              作业上传
            </label>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </ContentField>
</template>

<script setup>
import ContentField from '../components/ContentField.vue';
import {ref} from "vue";
import store from "@/store";
import $ from 'jquery';
import {apiUrl} from "../../config";
import {ElNotification} from "element-plus";

let centerDialogVisible = ref(false)
const CourseName = ref('')
const Score = ref('')
const YearDate = ref('')
const WeekTime = ref('')
const DayTime = ref('')
const courseInfo = ref([]); // 创建响应式的userInfos

getCourseList()

function goToUrl(url) {
  window.location.href = url
}
function upload(id, e) {
  let file = e.target.files[0];
  let param = new FormData();
  param.append("file", file);
  $.ajax({
    url: `${apiUrl}/api/aliyun/oss/upload`,
    type: "post",
    data: param,
    processData: false,
    contentType: false,
    headers: {
      "Authorization": "Bearer " + store.state.user.token,
    },
    success(res) {
      updateCourseHomeWork(id,res)
      ElNotification({
        title: 'Success',
        message: '上传成功',
        type: 'success',
      })
    },
    error() {
      ElNotification({
        title: 'Error',
        message: '上传失败',
        type: 'error',
      })
    }
  })
}
function updateCourseHomeWork(id, url) {
  $.ajax({
    url: `${apiUrl}/api/user/course/updateCourseHomeWork/`,
    type: "post",
    data:
        {
          "id": id,
          "url": url,
        },
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        getCourseList()
      }
    },
  });
}
function update() {
  console.log('update')
}


function addCourse() {
  $.ajax({
    url: `${apiUrl}/api/user/course/addCourse/`,
    type: "post",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    data: {
      name: CourseName.value,
      score: Score.value,
      yearDate: YearDate.value,
      weekTime: WeekTime.value,
      dayTime: DayTime.value,
    },
    success(resp) {
      if (resp.code === 200) {
        getCourseList();
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
  centerDialogVisible.value = false;
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
  background-color: #f5f5f5; /* 修改按钮背景色为浅灰色 */
  opacity: 0.6; /* 设置按钮透明度为 60% */
}

/* 禁用状态的按钮样式 */
.custom-file-upload.disabled {
  color: #fff; /* 修改按钮文字颜色为白色 */
  background-color: #a50709; /* 修改按钮背景色为红色 */
  cursor: not-allowed; /* 鼠标指针变为禁用样式 */
  opacity: 0.6; /* 设置按钮透明度为 60% */
  /* 添加其他禁用状态下的样式 */
}
</style>