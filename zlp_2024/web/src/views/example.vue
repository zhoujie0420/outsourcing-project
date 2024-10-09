<script setup>
import { Plus } from "@element-plus/icons-vue";
</script>

<template>
  <el-form-item label="入职时间" prop="intime">
    <el-date-picker v-model="dateval" type="date" placeholder="请选择日期" />
  </el-form-item>

  <el-table-column
    label="内容"
    align="center"
    prop="content"
    show-overflow-tooltip
  />

  <el-table-column label="图片" align="center" prop="img">
    <template #default="scope">
      <el-image
        :preview-teleported="true"
        style="width: 50px; height: 50px"
        :src="scope.row.img"
        :preview-src-list="[scope.row.img]"
        fit="cover"
      />
    </template>
  </el-table-column>

  <el-form-item label="图片" prop="img">
    <el-upload
      style="
        width: 178px;
        height: 178px;
        display: block;
        border: 1px dashed var(--el-border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
      "
      :action="mainStore.setting.uploadURL"
      :show-file-list="false"
      :on-success="handleAddAvatarSuccess"
    >
      <img
        v-if="addFormData.img"
        :src="addFormData.img"
        style="width: 178px; height: 178px; display: block; object-fit: cover"
      />
      <el-icon
        v-else
        style="
          font-size: 28px;
          color: #8c939d;
          width: 178px;
          height: 178px;
          text-align: center;
        "
      >
        <Plus />
      </el-icon>
    </el-upload>
  </el-form-item>

  <el-form-item label="性别" prop="sex">
    <el-select
      v-model="addFormData.sex"
      placeholder="请选择"
      style="width: 200px"
    >
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
  </el-form-item>

  <el-upload
    v-model:file-list="fileList"
    multiple
    :action="mainStore.setting.uploadURL"
    list-type="picture"
    style="width: 100%"
  >
    <el-button type="primary">点击上传</el-button>
  </el-upload>

  <el-form-item label="店铺介绍" prop="namee">
    <el-input
      v-model="mainStore.curuser.shopcontent"
      placeholder="请输入店铺介绍"
      type="textarea"
      :autosize="{ minRows: 3 }"
    />
  </el-form-item>
</template>

<style scoped></style>
