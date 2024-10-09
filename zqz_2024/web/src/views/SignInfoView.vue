<template>
  <div id="app" class="esign">
    <div class="esign-box">
      <vue-esign class="esignature" ref="esign" :width="800" :height="300" :isCrop="isCrop" :lineWidth="lineWidth" :lineColor="lineColor" />
    </div>
    <div class="esigh-btns">
      <button @click="handleReset">清空画板</button>
      <button @click="handleGenerate">生成图片</button>
    </div>
    <!-- <div class="esigh-result">
      <img v-if="resultImg" :src="resultImg" alt="" />
    </div> -->
  </div>
</template>

<script>
import router from '@/router';
import $ from 'jquery';
import { apiUrl } from '../../config';
import store from '@/store';

import { PDFDocument } from 'pdf-lib';
import { ElLoading, ElMessage } from 'element-plus';

function upload(file) {
  return new Promise(success => {
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
      success
    });
  });
}

function updateDocUrl(id, doctorId, url) {
  return new Promise(success => {
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
      success
    });
  });
}

export default {
  name: 'App',
  data() {
    return {
      lineWidth: 6,
      lineColor: '#000000',
      bgColor: '#000000',
      resultImg: '',
      isCrop: false
    };
  },

  methods: {
    handleReset() {
      this.$refs.esign.reset();
      console.log('111' + this.$route.query.id); // 18
    },
    async handleGenerate() {
      const loading = ElLoading.service({ lock: true, text: 'Loading', background: 'rgba(0, 0, 0, 0.7)' });
      try {
        const res = await this.$refs.esign.generate();
        this.resultImg = res;
        const arrayBuffer = await fetch(this.$route.query.docUrl).then(res => res.arrayBuffer());
        const doc = await PDFDocument.load(arrayBuffer);
        const signImg = await doc.embedPng(res);
        const page = doc.getPage(doc.getPageCount() - 1);
        const width = page.getWidth();
        const height = page.getHeight();

        const sw = width * 0.15;
        const sh = (sw / 800) * 300;
        const sx = width * 0.6;
        if (this.$store.state.user.role === 2) {
          page.drawImage(signImg, { x: sx, y: height * 0.25, width: sw, height: sh });
        } else {
          page.drawImage(signImg, { x: sx, y: height * 0.21, width: sw, height: sh });
        }
        const newDoc = await doc.save();
        const blob = new Blob([newDoc], { type: 'application/pdf' });

        // window.open(URL.createObjectURL(blob));
        const resp = await upload(new File([blob], 'new.pdf'));
        await updateDocUrl(this.$route.query.id, this.$route.query.doctorId, resp);
        const updateSigning = await new Promise(success => {
          $.ajax({
            url: `${apiUrl}/api/signing/updateSigning/`,
            type: 'post',
            data: {
              id: this.$route.query.id,
              doctorId: this.$route.query.doctorId
            },
            headers: {
              Authorization: 'Bearer ' + store.state.user.token
            },
            success
          });
        });
        if (updateSigning.code === 200) {
          ElMessage.success('签名成功');
          router.push({ name: 'signing_index' });
        }
      } catch (err) {
        ElMessage.error(err);
      } finally {
        loading.close();
      }
    }
  }
};
</script>

<style scoped>
.esign {
  max-width: 1000px;
  margin: auto;
  padding: 10px;
}
.esigh-btns button {
  color: #fff;
  height: 40px;
  width: 100px;
  font-size: 16px;
  margin-right: 10px;
  outline: none;
  border-radius: 4px;
  background: #f60;
  border: 1px solid transparent;
}
.esigh-btns button:active {
  color: #fff;
  box-shadow: 0px 0px 50px orangered inset;
}
.esigh-result {
  margin-top: 10px;
}
.esigh-result img {
  display: block;
  max-width: 100%;
  height: auto;
  border: 1px solid #ececee;
}
.esignature {
  margin: 10px 0;
  border: 2px solid #ccc;
}
</style>
