package com.fr.plugin.db.redis.core.visit;

import com.fr.plugin.db.redis.core.visit.impl.*;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collections;
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
    }

    public  static <T> List<List<T>> getKeyValueResult(Jedis client, String query, int rowCount) throws Exception {
        Visitor visitor = getMatchedVisitor(query);
        if (visitor == null) {
            return Collections.emptyList();
        } else {
            return visitor.getContent(client, query, rowCount);
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
