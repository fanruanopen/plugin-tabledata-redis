package com.fr.plugin.db.redis.core.visit.impl;

import com.fr.base.Parameter;
import com.fr.plugin.db.redis.core.visit.AbstractVisitor;
import com.fr.script.Calculator;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by richie on 2017/5/2.
 */
public class SetVisitor extends AbstractVisitor<String> {

    @Override
    public List<List<String>> getContent(Calculator calculator, Parameter[] ps, Jedis client, String query, int rowCount) throws Exception {
        String[] arr = query.trim().split(TOKEN_SPACE);
        if (arr.length < 2) {
            throw new IllegalArgumentException("Illegal query:" + query);
        }
        Set<String> data = client.smembers(arr[1]);
        List<String> column = new ArrayList<String>();
        for (String element : data) {
            column.add(element);
        }
        List<List<String>> result = new ArrayList<List<String>>();
        result.add(column);
        result.add(column);
        return result;
    }

    @Override
    public String keyWord() {
        return "smembers";
    }
}
