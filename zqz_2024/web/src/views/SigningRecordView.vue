<template>
  <ContentField>
    <table class="table table-striped table-hover" style="text-align: center">
      <thead>
        <tr>
          <th>{{ $store.state.user.role === 1 ? '医生姓名' : '患者姓名' }}</th>
          <th>更新时间</th>
          <th>签约文档</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="record in signRecords" :key="record.id">
          <td>{{ record.otherName }}</td>
          <td>{{ record.createTime }}</td>
          <td @click="redirectToUrl(record.docUrl)">
            {{ record.docUrl === null ? '待上传' : '点击查看' }}
          </td>
          <td>
            {{
              record.docUrl === null
                ? '未开启签约流程'
                : record.status === 0
                ? '待医生确认'
                : record.status === 1
                ? '待医院管理员确认'
                : record.status === 2
                ? '已完成'
                : ''
            }}
          </td>
          <td>
            <div class="custom-file">
              <div v-if="store.state.user.role === 1">
                <label :for="'customFile_' + record.id" class="custom-file-upload">
                  <input type="file" @change="upload(record.id, record.doctorId, $event)" :id="'customFile_' + record.id" style="display: none" />
                  上传文件
                </label>
              </div>
              <div v-else>
                <el-button plain @click="goToSignInfo(record.id, record.doctorId, record.docUrl)"> 签字确认 </el-button>
              </div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </ContentField>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      lineWidth: 6,
      lineColor: '#000000',
      bgColor: '',
      resultImg: '',
      isCrop: false
    };
  },
  methods: {
    handleReset() {
      this.$refs.resign.reset();
    },
    handleGenerate() {
      this.$refs.esign
        .generate()
        .then(res => {
          this.resultImg = res;
        })
        .catch(err => {
          alert(err); // 画布没有签字时会执行这里 'Not Signned'
        });
    }
  }
};
</script>
<script setup>
import ContentField from '../components/ContentField.vue';
import { ref } from 'vue';
import store from '@/store';
import $ from 'jquery';
import { apiUrl } from '../../config';
import router from '@/router';

const signRecords = ref([]); // 创建响应式的userInfos

function goToSignInfo(id, doctorId, docUrl) {
  router.push({ name: 'sign_info_view', query: { id: id, doctorId: doctorId, docUrl } });
}

function upload(id, doctorId, e) {
  let file = e.target.files[0];
  let param = new FormData();
  param.append('file', file);
  $.ajax({
    url: `${apiUrl}/api/aliyun/oss/upload`,
    type: 'post',
    data: param,
    processData: false,
    contentType: false,
    headers: {
      Authorization: 'Bearer ' + store.state.user.token
    },
    success(resp) {
      updateDocUrl(id, doctorId, resp);
    }
  });
}

getSigns();

function updateDocUrl(id, doctorId, url) {
  $.ajax({
    url: `${apiUrl}/api/signing/updateSigning/`,
    type: 'post',
    data: {
      id: id,
      docUrl: url,
      doctorId: doctorId
    },
    headers: {
      Authorization: 'Bearer ' + store.state.user.token
    },
    success(resp) {
      if (resp.code === 200) {
        getSigns();
      }
    }
  });
}

function redirectToUrl(url) {
  if (url !== null) {
    window.open(url, '_blank');
  }
}

function getSigns() {
  $.ajax({
    url: `${apiUrl}/api/signing/getSigning/`,
    type: 'post',
    headers: {
      Authorization: 'Bearer ' + store.state.user.token
    },
    success(resp) {
      if (resp.code === 200) {
        signRecords.value = resp.data; // 更新userInfos的值
      } else {
        console.log(resp.message);
      }
    }
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
