<template>
  <div class="about">
    <h1>文本翻译</h1>
    <textarea v-model="inputText" placeholder="输入文本..."></textarea>
    <button :disabled="!inputText || isLoading" @click="translateText(inputText)">翻译</button>
    <div v-if="isLoading" class="loading">正在翻译...</div>
    <div v-if="translatedText" class="result">
      <h2>翻译结果</h2>
      <p>{{ translatedText }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from "axios";

const inputText = ref('');
const translatedText = ref('');
const isLoading = ref(false);

async function translateText(text) {
  isLoading.value = true;
  translatedText.value = '';
  try {
    // 模拟调用翻译API
    const response = await axios.get(`http://127.0.0.1:8000/translate/?text=${text}`, {withCredentials: true});
    console.log(response.data); // 在控制台中输出从 Django 视图函数返回的数据
    translatedText.value = `翻译后的文本: ${response.data.translated_text}`;
  } catch (error) {
    console.error('翻译失败', error);
    translatedText.value = '翻译失败，请重试。';
  } finally {
    isLoading.value = false;
  }
}
</script>

<style scoped>
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
  color: #007bff;
}

.result {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
}

@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    padding: 20px;
  }
}
</style>
