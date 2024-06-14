<template>
  <div class="about">
    <h1 >小说解析</h1>
      <input type="file" ref="fileInput" @change="handleFileChange"/>
    <div v-if="loading" class="loading">正在加载...</div>
    <div v-if="error" class="error">{{ error }}</div>
    <div class="translated-text" v-if="res">
      <h2 class="result-title">解析结果：</h2>
      <pre class="result-text">{{ JSON.parse(res.character_classification) }}</pre>
      <pre class="result-text">{{ JSON.parse(res.character_info) }}</pre>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import {ref} from "vue";

const fileInput = ref(null);
const imageUrl = ref('');
let res = ref(null);
let loading = ref(false);
let error = ref(null);
let tmp = ref(null);

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
  loading.value = true;
  error.value = null;
  res.value = null;
  axios.get(`http://127.0.0.1:8000/textGeneration/?url=${url}`, {withCredentials: true})
      .then(response => {
        console.log(response.data); // 在控制台中输出从 Django 视图函数返回的数据
        res.value = response.data;
        tmp.value = JSON.parse(response.data.character_classification);
        console.log(tmp.value)
      })
      .catch(err => {
        console.error('Error fetching data:', err);
        error.value = "无法获取数据，请稍后再试";
      })
      .finally(() => {
        loading.value = false;
      });
}
</script>

<style scoped>
.translator {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
}

.title {
  margin-bottom: 20px;
  font-size: 24px;
}

.input-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 80%;
  margin: 20px auto;
}

textarea {
  width: 100%;
  height: 150px;
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  resize: none;
}

button {
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

button:hover:enabled {
  background-color: #0056b3;
}

.loading {
  margin-top: 20px;
  font-size: 16px;
}

.error {
  margin-top: 20px;
  color: red;
  font-size: 16px;
}

.translated-text {
  margin-top: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 10px;
}

.result-title {
  margin-bottom: 10px;
  font-size: 18px;
}

.result-text {
  font-size: 16px;
  line-height: 1.6;
}
</style>
