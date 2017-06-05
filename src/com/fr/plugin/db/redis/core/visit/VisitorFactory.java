package com.fr.plugin.db.redis.core.visit;

import com.fr.base.Parameter;
import com.fr.plugin.db.redis.core.DataWrapper;
import com.fr.plugin.db.redis.core.visit.impl.*;
import com.fr.script.Calculator;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richie on 2017/5/2.
 */
public class VisitorFactory {

    private static List<Visitor> visitors = new ArrayList<Visitor>();

    static {
        visitors.add(new ListVisitor());
        visitors.add(new HashVisitor());
        visitors.add(new SetVisitor());
        visitors.add(new ZRangeVisitor());
        visitors.add(new MGetVisitor());
        visitors.add(new SingleArrayVisitor());
    }

    public  static <T> DataWrapper<T> getKeyValueResult(Calculator calculator, Parameter[] ps, Jedis client, String query, int rowCount) throws Exception {
        Visitor visitor = getMatchedVisitor(query);
        if (visitor == null) {
            return DataWrapper.EMPTY;
        } else {
            return visitor.buildData(calculator, ps, client, query, rowCount);
        }
    }

    private static Visitor getMatchedVisitor(String query) {
        for (Visitor visitor : visitors) {
            if (visitor.match(query)) {
                return visitor;
            }
        }
        return null;
    }
}
