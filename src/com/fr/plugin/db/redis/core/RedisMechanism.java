package com.fr.plugin.db.redis.core;

public enum RedisMechanism {

    NONE(0), SHA1(1),CR(2);


    private int type;
    RedisMechanism(int type) {
        this.type = type;
    }

    public int toInt() {
        return type;

    }

    public static RedisMechanism parse(int type) {
        for (RedisMechanism m : RedisMechanism.values()) {
            if (m.type == type) {
                return m;
            }
        }
        return RedisMechanism.NONE;
    }
}
