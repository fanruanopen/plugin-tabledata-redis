package com.fr.plugin.db.redis.core.visit;

import com.fr.plugin.db.redis.core.DataWrapper;
import com.fr.plugin.db.redis.core.RedisConstants;
import redis.clients.jedis.Jedis;

/**
 * Created by richie on 2017/5/2.
 */
public abstract class AbstractVisitor<T> implements Visitor<T> {

    @Override
    public DataWrapper<T> buildData(Jedis client, String query, int rowCount) throws Exception {
        return DataWrapper.create(getContent(client, query, rowCount), RedisConstants.DEFAULT_COLUMN_NAMES);
    }

    @Override
    public boolean match(String query) {
        return query.trim().toLowerCase().startsWith(keyWord());
    }
}
