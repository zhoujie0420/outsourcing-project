<script setup>
import request from "@/utils/request.js";

document.title = "数据统计"; // 设置页面标题

import * as echarts from "echarts";
import { onMounted } from "vue";
import { ElMessage } from "element-plus";

//初始化函数
function init() {
  // 基于准备好的dom，初始化echarts实例
  const Chart1 = echarts.init(document.getElementById("main1"));

  request({
    url: "/api/statistics/salesVolume",
    data: {},
  }).then(({ data }) => {
    const names = [];
    const list = [];

    for (let i = 0; i < data.length; i++) {
      names.push(data[i].namee);
      list.push(data[i].total);
    }
    // 绘制图表
    const options1 = {
      tooltip: {
        trigger: "item",
        formatter: "销量：{c} ({d}%)",
      },
      series: [
        {
          type: "pie", // 将图表类型改为饼图
          data: names.map((data, index) => ({
            value: list[index],
            name: data,
          })),
        },
      ],
    };

    // 渲染图表
    Chart1.setOption(options1);
  });
}

//声明周期函数，自动执行初始化
onMounted(() => {
  init();
});
</script>

<template>
  <el-row style="margin: 10px" :gutter="10">
    <el-col :span="12">
      <el-card>
        <template #header> 销量分析 </template>
        <div id="main1" style="height: 400px"></div>
      </el-card>
    </el-col>
    <el-col :span="12"> </el-col>
  </el-row>
</template>

<style scoped></style>
