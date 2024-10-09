package com.gra.backend.service.utils;

import com.github.houbb.heaven.util.lang.CharUtil;
import com.github.houbb.sensitive.word.api.ISensitiveWordReplace;
import com.github.houbb.sensitive.word.api.ISensitiveWordReplaceContext;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.github.houbb.sensitive.word.support.result.WordResultHandlers;
import org.junit.Test;

public class SensitiveWordReplace implements ISensitiveWordReplace {
    @Override
    public String replace(ISensitiveWordReplaceContext context) {
        // 其他默认使用 * 代替
        int wordLength = context.wordLength();
        return CharUtil.repeat('*', wordLength);
    }
}

