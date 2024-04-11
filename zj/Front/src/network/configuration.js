import {wsUrl} from "../../config";

export default {
    server: {
        webSocketServer: {
            host: `${wsUrl}`,
            port: 8081,
            path: "/peerServerEndpoint"
        },
        peerServer: {
            host: `${wsUrl}`,
            port: 9000,
            path: "/"
        }
    }
};