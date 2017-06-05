package com.fr.plugin.db.redis.core.visit.impl;

import com.fr.base.Parameter;
import com.fr.plugin.db.redis.core.visit.AbstractVisitor;
import com.fr.script.Calculator;
import com.fr.stable.ArrayUtils;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richie on 2017/5/3.
 */
public class MGetVisitor extends AbstractVisitor<String> {
    @Override
    public List<List<String>> getContent(Calculator calculator, Parameter[] ps, Jedis client, String query, int rowCount) throws Exception {
        String[] arr = query.trim().split(TOKEN_SPACE);
        if (arr.length < 2) {
            throw new IllegalArgumentException("Illegal query:" + query);
        }
        String[] args = (String[]) ArrayUtils.remove(arr, 0);
        List<String> data = client.mget(args);
        List<List<String>> result = new ArrayList<List<String>>();
        result.add(data);
        result.add(data);
        return result;
    }

    @Override
    public String keyWord() {
        return "mget";
    }
}
