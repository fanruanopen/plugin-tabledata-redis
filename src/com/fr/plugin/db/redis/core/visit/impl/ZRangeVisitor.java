package com.fr.plugin.db.redis.core.visit.impl;

import com.fr.base.TableData;
import com.fr.plugin.db.redis.core.visit.AbstractVisitor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by richie on 2017/5/2.
 */
public class ZRangeVisitor extends AbstractVisitor<Object> {
    @Override
    public List<List<Object>> getContent(Jedis client, String query, int rowCount) throws Exception {
        String[] arr = query.trim().split(TOKEN_SPACE);
        if (arr.length < 4) {
            throw new IllegalArgumentException("Illegal query:" + query);
        }
        long start = Long.parseLong(arr[2]);
        long end = Long.parseLong(arr[3]);
        if (rowCount != TableData.RESULT_ALL) {
            end = start + rowCount;
        }
        Set<Tuple> data = client.zrangeWithScores(arr[1], start, end);
        List<Object> column = new ArrayList<Object>();
        List<Object> value = new ArrayList<Object>();
        for (Tuple element : data) {
            column.add(element.getElement());
            value.add(element.getScore());
        }
        List<List<Object>> result = new ArrayList<List<Object>>();
        result.add(column);
        result.add(value);
        return result;
    }

    @Override
    public String keyWord() {
        return "zrange";
    }
}
