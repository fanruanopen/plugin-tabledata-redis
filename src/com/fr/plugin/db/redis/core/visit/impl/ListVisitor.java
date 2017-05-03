package com.fr.plugin.db.redis.core.visit.impl;

import com.fr.base.TableData;
import com.fr.plugin.db.redis.core.visit.AbstractVisitor;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richie on 2017/5/2.
 */
public class ListVisitor extends AbstractVisitor<String> {
    @Override
    public List<List<String>> getContent(Jedis client, String query, int rowCount) throws Exception {

        String[] arr = query.trim().split(TOKEN_SPACE);
        if (arr.length < 4) {
            throw new IllegalArgumentException("Illegal query:" + query);
        }
        long start = Long.parseLong(arr[2]);
        long end = Long.parseLong(arr[3]);
        if (rowCount != TableData.RESULT_ALL) {
            end = start + rowCount;
        }
        List<String> data = client.lrange(arr[1], start, end);
        List<List<String>> result = new ArrayList<List<String>>();
        result.add(data);
        result.add(data);
        return result;
    }

    @Override
    public String keyWord() {
        return "lrange";
    }
}
