import Peer from "peerjs";
import networkConfiguration from "@/network/configuration";
import usePeerStore from "@/store/peer";
import {showToast} from "vant";
import $ from "jquery";
import {apiUrl} from "../../../config";


const user = usePeerStore();
export default function initializePeer (id) {
    let peerStore = usePeerStore();
    let {host, port, path} = {...networkConfiguration.server.peerServer};
    let localPeer = new Peer(id, {host, port, path});
    localPeer.on("connection", dataConnection => {
        console.log("localPeer on connection", dataConnection);
        if (peerStore.dataConnection) {
            dataConnection.on("open", () => {
                dataConnection.send({
                    instruction: peerStore.instruction.busy
                });
            });
        } else {
            dataConnection.on("data", data => {
                console.log("dataConnection data", data);
                //请求通话
                if (data.instruction === peerStore.instruction.request) {
                    peerStore.dataConnection = dataConnection;
                    //激活通知
                    peerStore.activateNotification = true;
                }

                //对方取消
                else if (data.instruction === peerStore.instruction.cancel) {
                    //关闭数据连接
                    peerStore.dataConnection.close();
                    peerStore.dataConnection = undefined;
                    //取消激活通知
                    peerStore.activateNotification = false;
                }

                //对方挂断
                else if (data.instruction === peerStore.instruction.ringOff) {
                    //关闭数据连接
                    peerStore.dataConnection.close();
                    peerStore.dataConnection = undefined;
                    //关闭媒体连接
                    peerStore.mediaConnection.close();
                    peerStore.mediaConnection = undefined;
                }
            });
        }
    });

    localPeer.on("call", mediaConnection => {
        console.log("localPeer call", mediaConnection);
        addRecord(mediaConnection.peer,user.localPeer.id);
        peerStore.mediaConnection = mediaConnection;
    });

    localPeer.on("disconnected", () => {
        console.warn("localPeer disconnected");
        localPeer.reconnect();
        showToast("video call component disconnected");
    });

    localPeer.on("error", error => {
        console.error("localPeer error,the error information is : ", JSON.stringify(error));
        showToast("video call component error");
    });

    localPeer.on("open", localPeerId => {
        console.log("localPeer opened,the local peer id is: ", localPeerId);
        peerStore.localPeer = localPeer;
    });
}

function addRecord(id1,id2) {
    $.ajax({
        url: `${apiUrl}/api/record/addRecord/`,
        type: "post",
        data: {
            userId1: id1,
            userId2: id2,
        },
        headers: {
            Authorization: "Bearer " + user.token,
        },
        success(resp) {
            if (resp.code === 200) {
                console.log(resp.message)
            } else {
                console.log(resp.message)
            }
        },
        error(resp) {
            console.log(resp)
        }
    })
}