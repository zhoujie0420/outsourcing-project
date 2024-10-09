<template>
  <ContentField>
  <div class="security-monitor">
    <h2>安全监控页面</h2>

    <!-- 实时报警信息 -->
    <div class="alert-section">
      <h3>实时报警信息</h3>
      <el-alert
          v-for="(alert, index) in alerts"
          :key="index"
          :title="alert.title"
          :type="alert.type"
          :closable="false"
          :description="alert.description"
          style="margin-bottom: 10px;"
      />
    </div>

    <!-- 设备状态 -->
    <div class="device-status-section">
      <h3>设备状态</h3>
      <el-row :gutter="20">
        <el-col :span="8" v-for="(device, index) in devices" :key="index">
          <el-card :body-style="{ padding: '20px' }">
            <p>设备名称: {{ device.name }}</p>
            <p>状态: <span :class="device.status === '正常' ? 'status-normal' : 'status-abnormal'">{{ device.status }}</span></p>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
  </ContentField>
</template>

<script>
import { ElAlert, ElRow, ElCol, ElCard } from 'element-plus';
import ContentField from "@/components/ContentField.vue";

export default {
  name: 'SecurityMonitor',
  components: {
    ContentField,
    ElAlert,
    ElRow,
    ElCol,
    ElCard
  },
  data() {
    return {
      alerts: [
        { title: '入侵检测', type: 'error', description: '发现入侵行为，IP地址：192.168.1.100' },
        { title: '异常登录', type: 'warning', description: '多次失败登录尝试，账号：admin' }
        // 添加更多报警信息
      ],
      devices: [
        { name: '服务器A', status: '正常' },
        { name: '路由器B', status: '异常' },
        { name: '摄像头C', status: '正常' }
        // 添加更多设备信息
      ]
    };
  }
};
</script>

<style scoped>
.security-monitor {
  padding: 20px;
}

.alert-section {
  margin-bottom: 20px;
}

.device-status-section {
  margin-top: 20px;
}

.status-normal {
  color: green;
}

.status-abnormal {
  color: red;
}
</style>
