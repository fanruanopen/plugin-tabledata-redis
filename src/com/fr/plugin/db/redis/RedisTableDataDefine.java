package com.fr.plugin.db.redis;

import com.fr.base.TableData;
import com.fr.design.data.tabledata.tabledatapane.AbstractTableDataPane;
import com.fr.design.fun.ServerTableDataDefineProvider;
import com.fr.design.fun.impl.AbstractTableDataDefineProvider;
import com.fr.general.Inter;
import com.fr.plugin.db.redis.core.RedisTableData;
import com.fr.plugin.db.redis.ui.RedisTableDataPane;


public class RedisTableDataDefine extends AbstractTableDataDefineProvider implements ServerTableDataDefineProvider {

    @Override
    public int currentAPILevel() {
        return CURRENT_LEVEL;
    }

    @Override
    public Class<? extends TableData> classForTableData() {
        return RedisTableData.class;
    }

    @Override
    public Class<? extends TableData> classForInitTableData() {
        return RedisTableData.class;
    }

    @Override
    public Class<? extends AbstractTableDataPane> appearanceForTableData() {
        return RedisTableDataPane.class;
    }

    @Override
    public String nameForTableData() {
        return Inter.getLocText("Plugin-Redis_Table_Data");
    }

    @Override
    public String prefixForTableData() {
        return "redis";
    }

    @Override
    public String iconPathForTableData() {
        return "/com/fr/plugin/db/redis/images/redis.png";
    }
}