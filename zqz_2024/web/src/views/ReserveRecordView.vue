<template>
  <ContentField>
    <div class="row align-items-center">
      <div class="col-auto">
        <label for="datePicker" class="form-label">选择日期:</label>
      </div>
      <div class="col-auto"> <!-- 修改此处，使用col-auto以适应内容宽度 -->
        <input type="date" class="form-control" id="datePicker" v-model="selectedDate" style="width: 160px;"> <!-- 添加内联样式调整宽度 -->
      </div>
    </div>

    <div v-if="store.state.user.role === 3">
      <el-table :data="doctorRecords" style="width: 100%">
        <el-table-column prop="otherName" label="患者姓名" width="280" />
        <el-table-column prop="otherNameTwo" label="医生姓名" width="280" />
        <el-table-column prop="appointmentDate" label="预约日期" width="240" />
        <el-table-column prop="timeSlot" label="预约时间"  :formatter="formatTimeSlot" width="240" />
      </el-table>
    </div>

    <div v-if="store.state.user.role === 2">
      <el-table :data="doctorRecords" style="width: 100%">
        <el-table-column prop="otherName" label="患者姓名" width="300" />
        <el-table-column prop="appointmentDate" label="预约日期" width="240" />
        <el-table-column prop="timeSlot" label="预约时间"  :formatter="formatTimeSlot" width="240" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleClick(scope.row.id)">上级医院处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-if="store.state.user.role === 1">
      <el-table :data="doctorRecords" :border="parentBorder" style="width: 100%" @expand-change="row => getAppointmentInfo(row.otherId)">
        <el-table-column type="expand">
          <table class="table table-striped table-hover" style="text-align: left;">
            <thead>
            <tr>
              <th>预约时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(record) in AppointmentRecords" :key="record.id">
              <td>{{ record.timeSlot + 8}}:00 - {{record.timeSlot + 9}}:00</td>
              <td>{{ record.status === 1 ? '已预约': record.status === 2 ? '上级医院处理' :'未预约' }}</td>
              <td>
                <el-button type="primary" size="small" v-if="record.status !== 0" >已被预约</el-button>
                <el-button type="success" size="small" @click="addAppointment(record.doctorId,record.timeSlot)" v-else> 预约</el-button>
              </td>
            </tr>
            </tbody>
          </table>
        </el-table-column>
        <el-table-column label="医生姓名" prop="otherName" />
        <el-table-column label="部门" prop="departmentName" />
        <el-table-column label="部门描述" prop="departmentDes" />
      </el-table>
    </div>
  </ContentField>
</template>

<script setup>
import ContentField from '../components/ContentField.vue';
import {ref} from "vue";
import store from "@/store";
import $ from 'jquery';
import {apiUrl} from "../../config";

const doctorRecords = ref([]); // 创建响应式的userInfos
const AppointmentRecords = ref([]); // 创建响应式的userInfos
const selectedDate = ref(new Date().toISOString().slice(0, 10)); // 初始化日期选择器的值

getAppointmentUsers()

function formatTimeSlot(row){
  return (row.timeSlot + 8) + ':00 - ' + (row.timeSlot + 9) + ':00'
}

function handleClick(id) {
  $.ajax({
    url: `${apiUrl}/api/user/appointment/updateAppointment/`,
    type: "post",
    data: {
      id: id
    },
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        console.log("处理成功")
        getAppointmentUsers()
      }
    },
  });
}
function addAppointment(doctorId,timeSlot) {
  $.ajax({
    url: `${apiUrl}/api/user/appointment/addAppointment/`,
    type: "post",
    data: {
      doctorId: doctorId,
      timeSlot: timeSlot,
      appointmentDate: selectedDate.value
    },
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        console.log("预约成功")
        getAppointmentInfo(doctorId,selectedDate)
      } else {
        console.log("预约失败")
      }
    },
  });
}
function getAppointmentUsers() {
  $.ajax({
    url: `${apiUrl}/api/user/appointment/getAppointmentUsers/`,
    type: "get",
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        console.log(resp.data)
        doctorRecords.value = resp.data; // 更新userInfos的值
      } else {
        console.log(resp.message);
      }
    },
  });
}

function getAppointmentInfo(id) {
  $.ajax({
    url: `${apiUrl}/api/user/appointment/getAppointmentsOfDoctor/`,
    type: "post",
    data: {
      id: id,
      date: selectedDate.value
    },
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        AppointmentRecords.value = resp.data; // 更新userInfos的值
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