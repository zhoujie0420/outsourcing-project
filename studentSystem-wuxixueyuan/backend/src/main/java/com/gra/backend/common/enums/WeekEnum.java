package com.gra.backend.common.enums;

public enum WeekEnum {
    /**
     * 星期一
     */
    MONDAY(1, "星期一"),
    /**
     * 星期二
     */
    TUESDAY(2, "星期二"),
    /**
     * 星期三
     */
    WEDNESDAY(3, "星期三"),
    /**
     * 星期四
     */
    THURSDAY(4, "星期四"),
    /**
     * 星期五
     */
    FRIDAY(5, "星期五"),
    /**
     * 星期六
     */
    SATURDAY(6, "星期六"),
    /**
     * 星期日
     */
    SUNDAY(7, "星期日");

    private final Integer code;
    private final String name;

    WeekEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        for (WeekEnum value : WeekEnum.values()) {
            if (value.code.equals(code)) {
                return value.name;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
