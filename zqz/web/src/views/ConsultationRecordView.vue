<template>
  <ContentField>
    <el-dialog v-model="smallDialogFormVisible" title="添加患者诊疗信息" width="500">
      <el-form :model="form">
        <el-form-item label="症状表现和诊断结果:" :label-width="formLabelWidth">
          <el-input maxlength="200" v-model="form.diagnosis" autocomplete="off" :row="20" type="textarea"
                    :autosize="{ minRows: 2, maxRows: 400 }" style="width: 100% ;"/>
        </el-form-item>
        <el-form-item label="处方:" :label-width="formLabelWidth">
          <el-input maxlength="200" v-model="form.prescription" autocomplete="off" :row="20" type="textarea"
                    :autosize="{ minRows: 2, maxRows: 400 }" style="width: 100% ;"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="smallToggleForm">返回</el-button>
          <el-button type="primary" @click="smallEdit">
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="dialogFormVisible" title="添加患者诊疗信息" width="500">
      <el-form :model="form">
        <el-form-item label="用户名" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" style="width: 150px"/>
        </el-form-item>
        <el-form-item label="症状表现和诊断结果:" :label-width="formLabelWidth">
          <el-input maxlength="200" v-model="form.diagnosis" autocomplete="off" :row="20" type="textarea"
                    :autosize="{ minRows: 2, maxRows: 400 }" style="width: 100% ;"/>
        </el-form-item>
        <el-form-item label="处方:" :label-width="formLabelWidth">
          <el-input maxlength="200" v-model="form.prescription" autocomplete="off" :row="20" type="textarea"
                    :autosize="{ minRows: 2, maxRows: 400 }" style="width: 100% ;"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="toggleForm">返回</el-button>
          <el-button type="primary" @click="addRecordFather">
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>
    <div style="display: flex; align-items: center;">
      <div v-if="$store.state.user.role !== 1">
        <el-button type="primary" class="addbutton" @click="toggleForm">
          添加诊疗记录
          <el-icon class="el-icon--right">
          </el-icon>
        </el-button>
      </div>
    </div>
    <el-table :data="records" :border="parentBorder" style="width: 100%">
      <el-table-column type="expand">
        <template #default="props">
          <h5>随诊记录</h5>
          <el-table :data="props.row.followUpList" :border="childBorder">
            <el-table-column label="症状表现及诊断结果" prop="diagnosis"/>
            <el-table-column label="处方" prop="prescription"/>
            <el-table-column label="时间" prop="consultationDate"/>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" @click="smallToggleForm(scope.row.id)"
                >Edit
                </el-button
                >
                <el-button
                    size="small"
                    type="danger"
                    @click="handleDelete(scope.row.id)"
                >Delete
                </el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column :label="getColumnLabel()" prop="otherName"/>
      <el-table-column :label="getPhoneTable()" prop="otherPhone"/>
      <el-table-column label="症状表现及诊断结果" prop="diagnosis"/>
      <el-table-column label="处方" prop="prescription"/>
      <el-table-column label="时间" prop="consultationDate"/>
      <el-table-column label="操作">
        <template v-if="$store.state.user.role !== 1" #default="scope">
          <el-button size="small" @click="handleEdit(scope.row.id)"
          >新增随诊
          </el-button
          >
          <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row.id)"
          >删除
          </el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
                   :page-sizes="[10, 20, 30, 40]" :page-size="pageSize" layout=" sizes, prev, pager, next, jumper"
                   :total="total"/>
  </ContentField>
</template>

<script setup>
import ContentField from '../components/ContentField.vue';
import {ref, reactive} from "vue";
import store from "@/store";

const records = ref([]); // 创建响应式的userInfos
import $ from 'jquery';
import {apiUrl} from "../../config";
import {ElMessage, ElMessageBox} from "element-plus";

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const tempId = ref()
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
const smallDialogFormVisible = ref(false)

function smallEdit(){
  $.ajax({
    url: `${apiUrl}/api/record/updateRecord/`,
    type: "post",
    data: {
      id: tempId.value,
      diagnosis: form.diagnosis,
      prescription: form.prescription,
    },
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        console.log("处理成功");
        getRecords()
      } else {
        console.log(resp.message);
      }
    },
  });
  smallToggleForm()
}
function addRecordFather() {
  $.ajax({
    url: `${apiUrl}/api/record/addRecord/`,
    type: "post",
    data: {
      fatherId: 0,
      patientId: form.name,
      diagnosis: form.diagnosis,
      prescription: form.prescription,
    },
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        console.log("处理成功");
        getRecords()

      } else {
        console.log(resp.message);
      }
    },
  });
  toggleForm()
}

const toggleForm = () => {
  dialogFormVisible.value = !dialogFormVisible.value;
};

const smallToggleForm = (id) => {
  smallDialogFormVisible.value = !smallDialogFormVisible.value;
  tempId.value = id
};
const handleDelete = (row) => {
  $.ajax({
    url: `${apiUrl}/api/record/deleteRecord/`,
    type: "post",
    data: {
      id: row,
    },
    headers: {
      Authorization: "Bearer " + store.state.user.token,
    },
    success(resp) {
      if (resp.code === 200) {
        getRecords()
      } else {
        console.log(resp.message);
      }
    },
  });
}
const handleEdit = (row) => {
  console.log(row);
  ElMessageBox.prompt('选择随诊时间', '新增随诊记录',
      {
        confirmButtonText: '提交',
        cancelButtonText: '取消',
        //     时间选择器，分钟选项间隔为 10 分钟
        inputPattern: /\d{4}-\d{2}-\d{2} \d{2}:\d{2}/,
      })
      .then(({value}) => {
        $.ajax({
          url: `${apiUrl}/api/record/addRecord/`,
          type: "post",
          data: {
            fatherId: row,
            consultationDate: value,
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