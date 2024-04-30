
<template>
  <ContentField>
    <div class="log-viewer">
      <h2>日志查看器</h2>

      <el-table :data="filteredLogs" border stripe>
        <el-table-column prop="timestamp" label="时间"></el-table-column>
        <el-table-column prop="level" label="级别"></el-table-column>
        <el-table-column prop="message" label="消息"></el-table-column>
      </el-table>
    </div>
  </ContentField>
</template>

<script>
import {  ElTable, ElTableColumn } from 'element-plus';
import ContentField from "@/components/ContentField.vue";

export default {
  name: 'LogViewer',
  components: {
    ContentField,
    ElTable,
    ElTableColumn
  },
  data() {
    return {
      logs: [
        { timestamp: '2024-04-30 10:00:00', level: 'INFO', message: 'Example log message 1' },
        { timestamp: '2024-04-30 10:10:00', level: 'WARNING', message: 'Example log message 2' },
        { timestamp: '2024-04-30 10:20:00', level: 'ERROR', message: 'Example log message 3' },
        { timestamp: '2024-04-30 10:00:00', level: 'INFO', message: 'Example log message 1' },
        { timestamp: '2024-04-30 10:10:00', level: 'WARNING', message: 'Example log message 2' },
        { timestamp: '2024-04-30 10:20:00', level: 'ERROR', message: 'Example log message 3' },
        { timestamp: '2024-04-30 10:00:00', level: 'INFO', message: 'Example log message 1' },
        { timestamp: '2024-04-30 10:10:00', level: 'WARNING', message: 'Example log message 2' },
        { timestamp: '2024-04-30 10:20:00', level: 'ERROR', message: 'Example log message 3' },
        { timestamp: '2024-04-30 10:00:00', level: 'INFO', message: 'Example log message 1' },
        { timestamp: '2024-04-30 10:10:00', level: 'WARNING', message: 'Example log message 2' },
        { timestamp: '2024-04-30 10:20:00', level: 'ERROR', message: 'Example log message 3' },
        { timestamp: '2024-04-30 10:00:00', level: 'INFO', message: 'Example log message 1' },
        { timestamp: '2024-04-30 10:10:00', level: 'WARNING', message: 'Example log message 2' },
        { timestamp: '2024-04-30 10:20:00', level: 'ERROR', message: 'Example log message 3' },
        { timestamp: '2024-04-30 10:00:00', level: 'INFO', message: 'Example log message 1' },
        { timestamp: '2024-04-30 10:10:00', level: 'WARNING', message: 'Example log message 2' },
        { timestamp: '2024-04-30 10:20:00', level: 'ERROR', message: 'Example log message 3' },

        // Add more log entries as needed
      ],
      searchText: ''
    };
  },
  computed: {
    filteredLogs() {
      return this.logs.filter(log => {
        return log.message.toLowerCase().includes(this.searchText.toLowerCase());
      });
    }
  }
};
</script>

<style scoped>
.log-viewer {
  padding: 20px;
}
</style>
