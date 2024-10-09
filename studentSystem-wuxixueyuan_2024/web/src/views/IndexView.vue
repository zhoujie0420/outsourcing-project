<template>
  <ContentField class="body-base">
    <div class="row">
      <div
          class="alert alert-info alert-dismissible fade show col-11"
          role="alert"
          style="margin-left: 3.5vw"
      >
        课程管理系统提醒您：请不要随意泄露个人信息，以免造成不必要的损失！
        <button
            type="button"
            class="btn-close small"
            data-bs-dismiss="alert"
            aria-label="Close"
        ></button>
      </div>
    </div>
    <div class="row">
      <div class="col-11 desc">
        <div class="desc-title">公告</div>
        <div v-if="$store.state.user.role === 0" class="desc-simple-content">
          &nbsp;&nbsp;&nbsp;&nbsp;<p>尊敬的各位同事，
          为了保障学校选课系统的正常运行和管理，提高教学管理效率，特向各位管理员发布以下通知：
        </p>
          <p>1.
            为了保障选课系统的稳定运行，计划于本周末进行系统维护，届时选课系统将暂时关闭。请各位管理员提前通知相关教职工和学生，做好课程安排和调整。</p>
          <p>2.
            针对上学期选课中出现的问题和反馈意见，我们将对选课流程进行优化，提升用户体验。请各位管理员密切关注选课系统的更新和改进，及时向教职工和学生传达相关信息。</p>
          <p>3.
            为了提高管理员对选课系统的使用熟练度，我们将组织相关的技术培训和支持，帮助各位管理员更好地掌握选课系统的操作技巧和功能特点。请各位管理员积极参与培训，提升自身能力。</p>
          <p>4.
            选课系统涉及大量的教学和学生信息，各位管理员务必严格遵守信息安全相关规定，加强系统权限管理和数据保护工作，确保信息安全和教学秩序的稳定，不泄露任何学生与老师的信息。</p>
          <p>在新学期的开始，让我们共同努力，充分发挥选课系统的作用，为教学管理工作提供更加高效便捷的支持！</p>
        </div>
        <div v-if="$store.state.user.role === 1" class="desc-simple-content">
          &nbsp;&nbsp;&nbsp;&nbsp;

          <p>亲爱的同学们，
            为了确保大家顺利完成本学期的选课和学习任务，学校提供了便捷的选课系统，希望大家能够充分利用这一工具，做好选课和学习计划。
          </p>
          <p>1.请尽快完成本学期的选课。合理选择课程是你学习生活的重要一环，务必根据个人兴趣、专业要求和学业规划，认真选择适合自己的课程。
          </p>
          <p>2. 随着新学期的开始，课程作业也将陆续布置。请同学们在规定的时间内，按时完成并提交作业。及时完成作业不仅有利于巩固学习内容，也是提高成绩的重要保障。
          </p>
          <p>3. 请留意选课系统公布的选课截止日期，务必在规定时间内完成选课。逾期选课可能会影响到你本学期的学习计划和课程安排。
          </p>
          <p>4. 如果在选课或学习过程中遇到任何问题，欢迎及时联系学校的选课管理员或相关部门寻求帮助。我们将竭诚为你提供支持和服务。
          </p>
          <p>5.评论区功能是各位同学合理交流课程的优缺点，希望友善交流，将屏蔽不友好的信息。
          </p>
          <p>让我们携手努力，充分利用选课系统和其他学习资源，共同度过充实而愉快的学习时光！
          </p> 祝好
        </div>
        <div v-if="$store.state.user.role === 2" class="desc-simple-content">
          &nbsp;&nbsp;&nbsp;&nbsp; <p>尊敬的教职工们，
          为了顺利开展本学期的教学工作，学校提供了便捷的选课系统，希望各位老师能够充分利用这一工具，做好课程安排和教学准备。
        </p>
          <p>1. 请各位老师尽快登录选课系统，录入本学期所负责的课程信息。准确完整的课程信息对学生的选课和学习至关重要，希望各位能够认真负责地完成课程信息的录入工作。
          </p>
          <p>2. 请密切关注选课系统中学生的选课进度和人数，及时调整课程安排和教学计划。确保课程安排合理，满足学生的学习需求。
          </p>
          <p>3. 学生选课是学习的第一步，也是教师教学工作的基础。希望各位老师能够配合学生顺利完成选课，提供必要的帮助和指导。
          </p>
          <p> 4. 建立良好的教学沟通机制，与学生和选课管理员保持及时的沟通和交流，解决教学中的问题和困难，共同促进教学质量的提升。
          </p>
          <p>让我们携手努力，充分发挥选课系统的作用，共同为学生的学习成长努力奋斗！
          </p>
        </div>


      </div>
    </div>
  </ContentField>

</template>

<script>
import ContentField from '../components/ContentField.vue'
import router from "@/router";
import {useStore} from "vuex";

export default {
  components: {
    ContentField,
  },
  setup() {
    const store = useStore();

    const jwt_token = localStorage.getItem("jwt_token");
    if (jwt_token) {
      store.commit("updateToken", jwt_token);
      store.dispatch("getInfo", {
        success() {
          router.push({name: "home"});
          store.commit("updatePullingInfo", false);
        },
        error() {
          store.commit("updatePullingInfo", false);
        }
      })
    } else {
      store.commit("updatePullingInfo", false);
    }

  }
}

</script>

<style scoped>
a {
  font-weight: bold;
  text-decoration: none;
  color: rgb(132, 183, 200);
}

.desc {
  height: 64vh;
  margin-top: 1vh;
  /* margin-left: 4vh; */
  margin-left: 3.4vw;
  border-radius: 2vh;
  border: 1px solid rgb(211, 217, 224);
  box-shadow: 2px 2px 10px lightgray;
}

.desc-title {
  text-align: center;
  margin-top: 1vh;
  font-size: 24px;
  font-weight: bold;
}

.desc-simple-content {
  font-size: 16px;
  margin-top: 2vh;
}

.desc-how {
  text-align: center;
  margin-top: 1vh;
  font-size: 24px;
  font-weight: bold;
}

.desc-how-ans {
  font-size: 16px;
  margin-top: 2vh;
}

.desc-how-ans > div {
  padding: 0.3vh;
}

span {
  font-weight: bold;
}

.how-fix-bug {
  margin-top: 2vh;
  font-size: 16px;
}

.how-fix-bug > div {
  padding-top: 1vh;
}


.information-title {
  color: #19d3ea;
  font-size: 18px;
  cursor: pointer; /*鼠标悬停变小手*/
  width: 100%;
}
</style>
  