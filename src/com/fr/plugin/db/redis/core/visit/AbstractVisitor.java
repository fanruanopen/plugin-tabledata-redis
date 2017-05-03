package com.fr.plugin.db.redis.core.visit;

/**
 * Created by richie on 2017/5/2.
 */
public abstract class AbstractVisitor<T> implements Visitor<T> {

    @Override
    public boolean match(String query) {
        return query.trim().toLowerCase().startsWith(keyWord());
    }
}
