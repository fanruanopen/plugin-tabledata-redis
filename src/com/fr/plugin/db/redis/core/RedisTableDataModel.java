package com.fr.plugin.db.redis.core;

import com.fr.base.TableData;
import com.fr.data.AbstractDataModel;

import com.fr.general.ModuleContext;
import com.fr.general.data.DataModel;
import com.fr.general.data.TableDataException;
import com.fr.plugin.PluginLicense;
import com.fr.plugin.PluginLicenseManager;
import com.fr.stable.StringUtils;


import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RedisTableDataModel extends AbstractDataModel {

    private Jedis redisClient;
    private List<String> columnNames;
    private List<List<Object>> data;


    public RedisTableDataModel(RedisDatabaseConnection mc, String dbName, String tableName, String query, String filter, String sort, int rowCount) {
        PluginLicense pluginLicense = PluginLicenseManager.getInstance().getPluginLicenseByID(RedisConstants.PLUGIN_ID);
        if (pluginLicense.isAvailable() || isDesign()) {
            initRedisData(mc, query, filter, sort, rowCount);
        } else {
            throw new RuntimeException("RedisDB Plugin License Expired!");
        }
    }

    private boolean isDesign() {
        return ModuleContext.isModuleStarted("com.fr.design.module.DesignerModule");
    }

    private synchronized void initRedisData(RedisDatabaseConnection mc, String query, String filter, String sort, int rowCount) {
        Set keys;
        if (redisClient == null) {
            redisClient = mc.createRedisClient();
            if (StringUtils.isEmpty(query)) {
                keys = redisClient.keys("*");
            }
            else {
                keys = redisClient.keys(query);
            }

            Iterator it = keys.iterator();
            columnNames = new ArrayList<String>();
            data = new ArrayList<List<Object>>();
            List<Object> rowData = new ArrayList<Object>();

            while (it.hasNext()) {
                String key = (String)it.next();
                System.out.println(redisClient.type(key));
                String value = "";
                switch (redisClient.type(key)) {
                    case "set": {
                        for (String temp : redisClient.smembers(key)) {
                            value = value + temp + " ";
                        }
                        break;
                    }
                    case "zset": {
                        for (String temp : redisClient.zrange(key, 0, -1)) {
                            value = value + temp + " ";
                        }
                        break;

                    }
                    case "hash": {
                        // 用farraylist
                        for (String temp : redisClient.hkeys(key)) {
                            String tempValueStr = redisClient.hmget(key,temp).toString();
                            value = value + temp + ": " + tempValueStr.substring(1, tempValueStr.length() - 1) + " ";
                        }
                        break;
                    }
                    case "list": {
                        for (String temp : redisClient.lrange(key, 0, -1)) {
                            value = value + temp + " ";
                        }
                        break;
                    }
                }
                columnNames.add(key);
                rowData.add(value);
            }
            data.add(rowData);

        }
    }

    @Override
    public int getColumnCount() throws TableDataException {
        return columnNames == null ? 0 :columnNames.size();
    }

    @Override
    public String getColumnName(int columnIndex) throws TableDataException {
        return columnNames == null ? null : columnNames.get(columnIndex);
    }

    @Override
    public boolean hasRow(int rowIndex) throws TableDataException {
        return data != null && data.size() > rowIndex;
    }

    @Override
    public int getRowCount() throws TableDataException {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) throws TableDataException {
        if (data != null && data.size() > rowIndex) {
            List<Object> rowData = data.get(rowIndex);
            if (rowData != null && rowData.size() > columnIndex) {
                return rowData.get(columnIndex);
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