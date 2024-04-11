package com.lhqjlb.project.config;

import com.lhqjlb.project.util.R;
import lombok.Getter;

@Getter
public class ResultException extends RuntimeException {

    private final R r;

    public ResultException(R r) {
        super(r.getMsg());
        this.r = r;
    }

}
