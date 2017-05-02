package com.fr.plugin.db.redis;

import com.fr.data.impl.Connection;
import com.fr.design.beans.BasicBeanPane;
import com.fr.design.fun.impl.AbstractConnectionProvider;
import com.fr.plugin.db.redis.core.RedisDatabaseConnection;
import com.fr.plugin.db.redis.ui.RedisConnectionPane;

public class RedisConnectionImpl extends AbstractConnectionProvider {

    @Override
    public int currentAPILevel() {
        return CURRENT_LEVEL;
    }

    @Override
    public String nameForConnection() {
        return "RedisDB";
    }

    @Override
    public String iconPathForConnection() {
        return "/com/fr/plugin/db/redis/images/redis.png";
    }

    @Override
    public Class<? extends Connection> classForConnection() {
        return RedisDatabaseConnection.class;
    }

    @Override
    public Class<? extends BasicBeanPane<? extends Connection>> appearanceForConnection() {
        return RedisConnectionPane.class;
    }
}