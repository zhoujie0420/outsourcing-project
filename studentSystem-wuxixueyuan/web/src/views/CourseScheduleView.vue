<template>
  <ContentField>
    <div>
      <el-table :data="timetableData" border class="custom-table">
        <el-table-column label="Time\Day" width="100">
          <template #default="{ row }">
            {{ row[0] }}
          </template>
        </el-table-column>
        <el-table-column v-for="(column, index) in weekdays.slice(1)" :key="index" :label="column">
          <template #default="{ row }">
            <div>{{ row[index + 1] }}</div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </ContentField>
</template>

<script setup>
import ContentField from '../components/ContentField.vue';
import {ElTable, ElTableColumn} from 'element-plus';
import {ref} from 'vue';
import {apiUrl} from "../../config";
import store from "@/store";
import $ from 'jquery';

const weekdays = ref(['Time/Day', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']);

const timetableData = ref([]);
getCourseSchedule();

function getCourseSchedule() {
  $.ajax({
    url: `${apiUrl}/api/user/course/getCourseSchedule/`,
    type: 'get',
    headers: {
      Authorization: 'Bearer ' + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        timetableData.value = resp.data;
      } else {
        console.log(resp.message);
      }
    },
  });
}
</script>
<style>
.custom-table .el-table__body tr {
  height: 100px; /* 调整行高为50像素 */
}
</style>
<script setup lang="ts">
</script>