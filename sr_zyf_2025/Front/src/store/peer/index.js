import {defineStore} from "pinia";

export default defineStore("peerStore", {
    state() {
        return {
            /**
             * 本地对等体对象
             */
            localPeer: undefined,

            userid: undefined,
            /**
             * username
             */
            username: undefined,
            /**
             * role
             */
            role: undefined,
            /**
             * token
             */
            token: undefined,
            /**
             * phone
             */
            phone: undefined,
            /**
             * 是否登陆
             */
            is_login: false,
            /**
             * 正在通话的数据连接对象
             */
            dataConnection: undefined,
            /**
             * 正在通话的媒体连接对象
             */
            mediaConnection: undefined,
            /**
             * 是否激活通知
             */
            activateNotification: false,
            /**
             * 视频通话指令枚举值
             */
            instruction: {
                /**
                 * 请求视频通话
                 */
                request: 1,
                /**
                 * 拒绝视频通话
                 */
                reject: 2,
                /**
                 * 接受视频通话
                 */
                accept: 3,
                /**
                 * 取消视频通话
                 */
                cancel: 4,
                /**
                 * 正常挂断
                 */
                ringOff: 5,
                /**
                 * 忙碌挂断
                 */
                busy: 6
            },
        };
    }
});