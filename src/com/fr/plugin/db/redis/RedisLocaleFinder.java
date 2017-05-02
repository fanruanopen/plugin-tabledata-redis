package com.fr.plugin.db.redis;

import com.fr.plugin.db.redis.core.RedisConstants;
import com.fr.stable.fun.Authorize;
import com.fr.stable.fun.impl.AbstractLocaleFinder;


@Authorize(callSignKey = RedisConstants.PLUGIN_ID)
public class RedisLocaleFinder extends AbstractLocaleFinder {
    @Override
    public String find() {
        return "com/fr/plugin/db/redis/locale/redis";
    }
}