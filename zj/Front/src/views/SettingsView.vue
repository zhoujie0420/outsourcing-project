<template>
  <section>
    <van-nav-bar title="设置" fixed placeholder safe-area-inset-top/>
  </section>
  <section>
    <van-cell-group :title="getTitle()" inset>
      <van-cell title="用户名" :value=" peerStore.username "/>
      <van-cell title="手机号" :value=" peerStore.phone"/>
      <van-cell title="身份" :value="getRole()"/>
      <van-cell title="问诊结果编辑" to="/recordList-view" is-link/>
    </van-cell-group>
    <van-button class="wide-button center-bottom" type="danger" @click="logout">退出</van-button>
  </section>
</template>
<script setup>
import usePeerStore from "@/store/peer";
import router from "@/router";


const getTitle = () => {
  if (peerStore.role === 1) {
    return "祝您早日康复";
  } else if (peerStore.role === 2) {
    return "悬壶济世，妙手回春";
  }
};

const getRole = () => {
  if (peerStore.role === 1) {
    return "患者";
  } else if (peerStore.role === 2) {
    return "医生";
  }
}


const logout = () => {
  localStorage.removeItem("jwt_token");
  peerStore.localPeer = "";
  peerStore.userid = "";
  peerStore.token = "";
  peerStore.is_login = false;
  router.push("/login-view");
}


const peerStore = usePeerStore();
</script>

<style scoped lang="scss">
.wide-button {
  width: 200px; /* 根据需要调整按钮宽度 */
  position: fixed;
  left: 50%;
  bottom: 80px; /* 调整底部距离根据需要 */
  transform: translateX(-50%);
}

.center-bottom {
  display: flex;
  justify-content: center;
}


</style>