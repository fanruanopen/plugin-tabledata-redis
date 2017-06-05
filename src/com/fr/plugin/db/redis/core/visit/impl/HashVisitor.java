package com.fr.plugin.db.redis.core.visit.impl;

import com.fr.base.Parameter;
import com.fr.plugin.db.redis.core.visit.AbstractVisitor;
import com.fr.script.Calculator;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by richie on 2017/5/2.
 */
public class HashVisitor extends AbstractVisitor<String> {
    @Override
    public List<List<String>> getContent(Calculator calculator, Parameter[] ps, Jedis client, String query, int rowCount) throws Exception {
        String[] arr = query.trim().split(TOKEN_SPACE);
        if (arr.length < 2) {
            throw new IllegalArgumentException("Illegal query:" + query);
        }
        Map<String, String> map = client.hgetAll(arr[1]);
        List<String> column1 = new ArrayList<String>();
        List<String> column2 = new ArrayList<String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            column1.add(entry.getKey());
            column2.add(entry.getValue());
        }
        List<List<String>> result = new ArrayList<List<String>>();
        result.add(column1);
        result.add(column2);
        return result;
    }

    @Override
    public String keyWord() {
        return "hgetall";
    }
}
