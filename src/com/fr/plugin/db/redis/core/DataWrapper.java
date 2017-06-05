package com.fr.plugin.db.redis.core;

import java.util.List;

/**
 * Created by richie on 2017/6/5.
 */
public class DataWrapper<T> {

    public static <T> DataWrapper<T> create(List<List<T>> data, String[] columnNames) {
        return new DataWrapper<T>(data, columnNames);
    }

    public static DataWrapper EMPTY = new DataWrapper(null, RedisConstants.DEFAULT_COLUMN_NAMES);

    private List<List<T>> data;
    private String[] columnNames;

    private DataWrapper(List<List<T>> data, String[] columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }

    public String[] getColumnNames() {
        if (columnNames == null) {
            return RedisConstants.DEFAULT_COLUMN_NAMES;
        }
        return columnNames;
    }

    public List<List<T>> getData() {
        return data;
    }
}
