<template>
  <div class="about">
    <h1 >文本识别</h1>
    <input type="file" ref="fileInput" @change="handleFileChange"/>
    <img v-if="res" :src="res" alt="Uploaded Image"/>
    <div>{{ resView }}</div>
  </div>
</template>

<script setup>
import {ref} from "vue";
import axios from "axios";

const resView = ref('');
const fileInput = ref(null);
const imageUrl = ref('');
const res = ref('');
function handleFileChange() {
  const file = fileInput.value.files[0];
  if (file) {
    res.value = URL.createObjectURL(file);
    imageUrl.value = file.webkitRelativePath || file.name;
    console.log("Uploaded image path:", imageUrl.value);
    callDjangoView(imageUrl.value);
  }
}

function callDjangoView(url) {
  axios.get(`http://127.0.0.1:8000/index/?url=${url}`, {withCredentials: true})
      .then(response => {
        console.log(response.data); // 在控制台中输出从 Django 视图函数返回的数据
        resView.value = response.data;
      })
      .catch(error => {
        console.error('Error fetching data:', error);
      });
}
</script>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
    flex-direction: column;
  }
}
</style>
