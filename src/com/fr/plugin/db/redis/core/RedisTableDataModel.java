package com.fr.plugin.db.redis.core;

import com.fr.data.AbstractDataModel;
import com.fr.general.FRLogger;
import com.fr.general.data.TableDataException;
import com.fr.plugin.PluginLicense;
import com.fr.plugin.PluginLicenseManager;
import com.fr.plugin.db.redis.core.visit.VisitorFactory;
import com.fr.stable.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.*;

public class RedisTableDataModel extends AbstractDataModel {

    private Jedis redisClient;
    private String[] columnNames;
    private List<List<Object>> data;


    public RedisTableDataModel(RedisDatabaseConnection mc, int dbIndex, String query, int rowCount) {
        PluginLicense pluginLicense = PluginLicenseManager.getInstance().getPluginLicenseByID(RedisConstants.PLUGIN_ID);
        if (pluginLicense.isAvailable()) {
            initRedisData(mc, dbIndex, query, rowCount);
        } else {
            throw new RuntimeException("Redis Plugin License Expired!");
        }
    }

    private synchronized void initRedisData(RedisDatabaseConnection mc, int dbIndex, String query, int rowCount) {
        if (StringUtils.isEmpty(query)) {
            return;
        }
        if (redisClient == null) {
            redisClient = mc.createRedisClient();
            if (dbIndex != RedisConstants.DEFAULT_DB_INDEX) {
                redisClient.select(dbIndex);
            }
        }
        try {
            DataWrapper wrapper = VisitorFactory.getKeyValueResult(redisClient, query, rowCount);
            data = wrapper.getData();
            columnNames = wrapper.getColumnNames();
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public int getColumnCount() throws TableDataException {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) throws TableDataException {
        return columnNames[columnIndex];
    }

    @Override
    public boolean hasRow(int rowIndex) throws TableDataException {
        return data != null && data.get(0).size() > rowIndex;
    }

    @Override
    public int getRowCount() throws TableDataException {
        return data == null ? 0 : data.get(0).size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) throws TableDataException {
        if (data != null && data.size() > columnIndex) {
            List<Object> columnData = data.get(columnIndex);
            if (columnData != null && columnData.size() > rowIndex) {
                return columnData.get(rowIndex);
            }
        }
        return null;
    }

    @Override
    public void release() throws Exception {
        if (redisClient != null) {
            redisClient.quit();
            redisClient = null;
        }
    }
}