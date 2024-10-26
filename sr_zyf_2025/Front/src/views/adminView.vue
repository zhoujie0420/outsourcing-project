<template>
  <el-row :gutter="16">
    <el-col :span="8">
      <div class="statistic-card">
        <el-statistic :value="8">
          <template #title>
            <div style="display: inline-flex; align-items: center">
              日活跃用户
              <el-tooltip
                  effect="dark"
                  content="Number of users who logged into the product in one day"
                  placement="top"
              >
                <el-icon style="margin-left: 4px" :size="12">
                  <Warning />
                </el-icon>
              </el-tooltip>
            </div>
          </template>
        </el-statistic>
        <div class="statistic-footer">
          <div class="footer-item">
            <span>昨日</span>
            <span class="green">
              10%
              <el-icon>
                <CaretTop />
              </el-icon>
            </span>
          </div>
        </div>
      </div>
    </el-col>
    <el-col :span="8">
      <div class="statistic-card">
        <el-statistic :value="28">
          <template #title>
            <div style="display: inline-flex; align-items: center">
              月活跃用户
              <el-tooltip
                  effect="dark"
                  content="Number of users who logged into the product in one month"
                  placement="top"
              >
                <el-icon style="margin-left: 4px" :size="12">
                  <Warning />
                </el-icon>
              </el-tooltip>
            </div>
          </template>
        </el-statistic>
        <div class="statistic-footer">
          <div class="footer-item">
            <span>上月</span>
            <span class="red">
              12%
              <el-icon>
                <CaretBottom />
              </el-icon>
            </span>
          </div>
        </div>
      </div>
    </el-col>
    <el-col :span="8">
      <div class="statistic-card">
        <el-statistic :value="160" title="New transactions today">
          <template #title>
            <div style="display: inline-flex; align-items: center">
              问诊记录总数
            </div>
          </template>
        </el-statistic>
        <div class="statistic-footer">
          <div class="footer-item">
            <span>小时增长</span>
            <span class="green">
              16%
              <el-icon>
                <CaretTop />
              </el-icon>
            </span>
          </div>

        </div>
      </div>
    </el-col>
  </el-row>


  <el-divider />


  <div>
    <el-timeline style="max-width: 600px">
      <el-timeline-item timestamp="2024/4/1" placement="top">
        <el-card>
          <h4>Jin Zhang - 患者</h4>
          <p>Join at 2024/4/11 14:24</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2024/4/1" placement="top">
        <el-card>
          <h4>Jie Zhou - 医生</h4>
          <p>Join at 2024/4/1 13:54</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2024/4/1" placement="top">
        <el-card>
          <h4>Chen Fan - 医生</h4>
          <p>Join at 2024/4/1 12:34</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2024/4/1" placement="top">
        <el-card>
          <h4>Yue Zhou - 患者</h4>
          <p>Join at 2024/4/1 11:34</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2024/4/1" placement="top">
        <el-card>
          <h4>QI Zhou - 患者</h4>
          <p>Join at 2024/4/1 11:30</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>


<script  setup>
import {
  CaretBottom,
  CaretTop,
  Warning,
} from '@element-plus/icons-vue'
import $ from "jquery";
import {apiUrl} from "../../config";
import {ref} from "vue";
import {showToast} from "vant";
let PInfos = ref([]); // 创建响应式的userInfos
let DInfos = ref([]); // 创建响应式的userInfos

getP()
getD()
function getP() {
  $.ajax({
    url: `${apiUrl}/api/user/account/getRole/`,
    type: "post",
    data: {
      userList: [],
      userRole: 2,
    },
    success(resp) {
      if (resp.code === 200) {
        PInfos.value = resp.data; // 更新userInfos的值
        console.log(PInfos.value)
      } else {
        showToast(resp.message);
      }
    },
  });
}
function getD() {
  $.ajax({
    url: `${apiUrl}/api/user/account/getRole/`,
    type: "post",
    data: {
      userList: [],
      userRole: 1,
    },
    success(resp) {
      if (resp.code === 200) {
        DInfos.value = resp.data; // 更新userInfos的值
        console.log(DInfos.value)
      } else {
        showToast(resp.message);
      }
    },
  });
}

</script>

<style scoped>
:global(h2#card-usage ~ .example .example-showcase) {
  background-color: var(--el-fill-color) !important;
}

.el-statistic {
  --el-statistic-content-font-size: 28px;
}

.statistic-card {
  height: 100%;
  padding: 20px;
  border-radius: 4px;
  background-color: var(--el-bg-color-overlay);
}

.statistic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  font-size: 12px;
  color: var(--el-text-color-regular);
  margin-top: 16px;
}

.statistic-footer .footer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistic-footer .footer-item span:last-child {
  display: inline-flex;
  align-items: center;
  margin-left: 4px;
}

.green {
  color: var(--el-color-success);
}
.red {
  color: var(--el-color-error);
}
</style>
