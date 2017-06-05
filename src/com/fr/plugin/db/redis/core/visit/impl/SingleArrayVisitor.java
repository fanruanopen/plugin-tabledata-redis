package com.fr.plugin.db.redis.core.visit.impl;

import com.fr.plugin.db.redis.core.visit.AbstractVisitor;
import com.fr.stable.ArrayUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Created by richie on 2017/6/5.
 */
public class SingleArrayVisitor extends AbstractVisitor<String> {
    @Override
    public List<List<String>> getContent(Jedis client, String query, int rowCount) throws Exception {
        String[] arr = query.trim().split(TOKEN_SPACE);
        if (ArrayUtils.getLength(arr) < 1) {
            throw new IllegalArgumentException("Illegal query:" + query);
        }
        String key = arr[1];
        String result = client.get(key);
        return null;
    }

    @Override
    public String keyWord() {
        return "singlemaparray";
    }
}
