package com.admin.server.enums;

public enum CountType {
    UserNum("A1"),

    AllSum("2"),

    AvgCount("3"),

    AvgSumByUser("4"),

    MiddleCount("5"),

    MaxCount("6"),

    MinCount("7"),

    DistincCount("8"),

    AllCount("9"),

    ClickUserNum("10"),

    AvgCountByUser("11"),

    MiddleCount5("12");

    private String type;

    private CountType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
