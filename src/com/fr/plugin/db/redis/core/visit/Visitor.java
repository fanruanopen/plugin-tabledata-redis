package com.fr.plugin.db.redis.core.visit;

import com.fr.stable.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Created by richie on 2017/5/2.
 */
public interface Visitor<T> {

    String TOKEN_SPACE = StringUtils.BLANK;

    List<List<T>> getContent(Jedis client, String query, int rowCount) throws Exception;

    boolean match(String query);

    String keyWord();
}
