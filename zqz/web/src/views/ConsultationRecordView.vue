<template>
  <ContentField>
    <el-dialog v-model="dialogFormVisible" title="添加患者诊疗信息" width="500">
      <el-form :model="form">
        <el-form-item label="用户名" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" style="width: 150px" />
        </el-form-item>
        <el-form-item label="症状表现和诊断结果:" :label-width="formLabelWidth">
          <el-input maxlength="200" v-model="form.diagnosis" autocomplete="off" :row="20" type="textarea"
            :autosize="{ minRows: 2, maxRows: 400 }" style="width: 100% ;" />
        </el-form-item>
        <el-form-item label="处方:" :label-width="formLabelWidth">
          <el-input maxlength="200" v-model="form.prescription" autocomplete="off" :row="20" type="textarea"
            :autosize="{ minRows: 2, maxRows: 400 }" style="width: 100% ;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="toggleForm">返回</el-button>
          <el-button type="primary" @click="toggleForm">
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>
    <div style="display: flex; align-items: center;">
      <el-input v-model="keyword" placeholder="输入用户名搜索" style="width: 30% ;"></el-input>
      <el-button @click="selectById">搜索</el-button>
      <el-button type="primary" class="addbutton" @click="toggleForm">
        添加诊疗记录
        <el-icon class="el-icon--right">
        </el-icon>
      </el-button>
    </div>
      <el-table :data="records" :border="parentBorder" style="width: 100%">
        <el-table-column type="expand">
          <template #default="props">
            <h5>随诊记录</h5>
              <el-table :data="props.row.followUpList" :border="childBorder">
                <el-table-column label="症状表现及诊断结果" prop="diagnosis" />
                <el-table-column label="处方" prop="prescription" />
                <el-table-column label="时间" prop="consultationDate" />
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-button size="small" @click="childEdit(scope.row.id)"
                    >编辑</el-button
                    >
                    <el-button
                        size="small"
                        type="danger"
                        @click="handleDelete(scope.$index, scope.row)"
                    >Delete</el-button
                    >
                  </template>
                </el-table-column>
              </el-table>
          </template>
        </el-table-column>
        <el-table-column :label="getColumnLabel()" prop="otherName" />
        <el-table-column :label="getPhoneTable()" prop="otherPhone" />
        <el-table-column label="症状表现及诊断结果" prop="diagnosis" />
        <el-table-column label="处方" prop="prescription" />
        <el-table-column label="时间" prop="consultationDate" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small"  @click="handleEdit(scope.row.id)"
            >新增随诊</el-button
            >
            <el-button
                size="small"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)"
            >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
      :page-sizes="[10, 20, 30, 40]" :page-size="pageSize" layout=" sizes, prev, pager, next, jumper" :total="total" />
  </ContentField>
</template>

<script setup>
import ContentField from '../components/ContentField.vue';
import { ref, reactive } from "vue";
import store from "@/store";
const records = ref([]); // 创建响应式的userInfos
import $ from 'jquery';
import { apiUrl } from "../../config";
import {ElMessage, ElMessageBox} from "element-plus";
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref('');
const handleSizeChange = (val) => {
  pageSize.value = val;
  getRecords();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  getRecords();
};

const form = reactive({
  name: '',
  diagnosis: '',
  prescription: '',
  delivery: false,
  type: [],
  resource: '',
  desc: '',
})
const dialogFormVisible = ref(false);
const toggleForm = () => {
  dialogFormVisible.value = !dialogFormVisible.value;
};
const handleEdit = (row) => {
  console.log(row);
  ElMessageBox.prompt('选择随诊时间', '新增随诊记录',
      {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    //     时间选择器，分钟选项间隔为 10 分钟
    inputPattern: /\d{4}-\d{2}-\d{2} \d{2}:\d{2}/,
  })
      .then(({ value }) => {
        $.ajax({
          url: `${apiUrl}/api/record/addRecord/`,
          type: "post",
          data: {
            fatherId: row,
            consultationDate:value,
          },
          headers: {
            Authorization: "Bearer " + store.state.user.token,
          },
          success(resp) {
            if (resp.code === 200) {
              console.log("处理成功");
            } else {
              console.log(resp.message);
            }
          },
        });
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: 'Input canceled',
        })
      })
}

getRecords()
function getColumnLabel() {
  return store.state.user.role === 1 ? '医生用户名' : '患者用户名';
}
function getPhoneTable() {
  return store.state.user.role === 1 ? '医生电话' : '患者电话';
}
function getRecords() {
  $.ajax({
    url: `${apiUrl}/api/record/getRecords/`,
    type: "post",
    data: {
      page: currentPage.value,
      pageSize: pageSize.value,
    },
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        records.value = resp.data; // 更新userInfos的值
      } else {
        console.log(resp.message);
      }
    },
  });
}
</script>

<style scoped>
.addbutton {
  margin-left: 10px;
}

.table {
  margin-top: 20px;
}
</style>