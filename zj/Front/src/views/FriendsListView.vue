<template>
  <section>
    <van-nav-bar title="视频问诊" fixed placeholder safe-area-inset-top/>
  </section>
  <section>
    <van-search placeholder="请输入搜索关键词"/>
  </section>
  <section v-if="userInfos.length">
    <van-divider>轻触即可视频问诊</van-divider>
    <section v-for="info of userInfos" :key="info">
      <friend-card @click="jumpToVideoCallCallingView(info.id)">
        {{ info.username }} / {{ info.departmentName || "患者" }}
      </friend-card>
    </section>
  </section>
</template>

<script setup>
import useFriendStore from "@/store/friend";
import usePeerStore from "@/store/peer";
import FriendCard from "@/components/FriendCard.vue";
import {useRouter} from "vue-router";
import {showToast} from "vant";
import {apiUrl} from "../../config";
import $ from "jquery";
import {ref} from "vue";

let friendStore = useFriendStore();
const peerStore = usePeerStore();
let router = useRouter();
getRole();
let userInfos = ref([]); // 创建响应式的userInfos
function jumpToVideoCallCallingView(calleePeerId) {
  console.log("peerStore.localPeer" + peerStore.localPeer);
  if (peerStore.localPeer && peerStore.localPeer.open) {
    if (peerStore.dataConnection) {
      showToast("currently busy");
    } else {
      router.push({
        path: "/video-call-calling-view",
        query: {
          calleePeerId
        }
      });
    }
  } else {
    console.log("calleePeerId" + calleePeerId);
    showToast("local peer not opened");
  }
}

function getRole() {
  $.ajax({
    url: `${apiUrl}/api/user/account/getRole/`,
    type: "post",
    data: {
      userList: friendStore.onlineList,
      userRole: peerStore.role,
    },
    headers: {
      Authorization: "Bearer " + peerStore.token,
    },
    success(resp) {
      if (resp.code === 200) {
        userInfos.value = resp.data; // 更新userInfos的值
      } else {
        showToast(resp.message);
      }
    },
  });
}


</script>