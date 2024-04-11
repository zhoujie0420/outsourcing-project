package com.lhqjlb.project.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class R implements Serializable {

    private int code;
    private String msg;
    private Object data;

    public enum ResultEnum {

        OK(200, "ok"),
        UNAUTHORIZED(401, "未登陆"),
        FORBIDDEN(403, "没有权限"),
        INTERNAL_SERVER_ERROR(500, "服务器繁忙，请稍后再试"),
        ERROR(1000, "Error"),
        ;

        private final int code;
        private final String msg;

        ResultEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String msg() {
            return this.msg;
        }

        public int code() {
            return this.code;
        }

    }

    public static R ok() {
        return new R()
                .setCode(ResultEnum.OK.code())
                .setMsg(ResultEnum.OK.msg());
    }

    public static R ok(Object data) {
        return new R()
                .setCode(ResultEnum.OK.code())
                .setMsg(ResultEnum.OK.msg())
                .setData(data);
    }

    public static R error() {
        return new R()
                .setCode(ResultEnum.ERROR.code())
                .setMsg(ResultEnum.ERROR.msg());
    }

    public static R error(String msg) {
        return new R()
                .setCode(ResultEnum.ERROR.code())
                .setMsg(msg);
    }

    public static R error(ResultEnum result) {
        return new R()
                .setCode(result.code())
                .setMsg(result.msg());
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == ResultEnum.OK.code();
    }

}
