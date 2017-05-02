package com.fr.plugin.db.redis.core;

import com.fr.base.Parameter;
import com.fr.base.TableData;
import com.fr.base.TemplateUtils;
import com.fr.data.AbstractParameterTableData;
import com.fr.data.core.DataCoreXmlUtils;
import com.fr.data.impl.Connection;
import com.fr.data.impl.NameDatabaseConnection;
import com.fr.file.DatasourceManager;
import com.fr.general.Inter;
import com.fr.general.data.DataModel;
import com.fr.plugin.ExtraClassManager;
import com.fr.script.Calculator;
import com.fr.stable.ArrayUtils;
import com.fr.stable.ParameterProvider;
import com.fr.stable.StringUtils;
import com.fr.stable.fun.FunctionHelper;
import com.fr.stable.fun.FunctionProcessor;
import com.fr.stable.fun.impl.AbstractFunctionProcessor;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.HashMap;
import java.util.Map;


public class RedisTableData extends AbstractParameterTableData {

    private static final FunctionProcessor REDIS = new AbstractFunctionProcessor() {

        @Override
        public int getId() {
            return FunctionHelper.generateFunctionID(RedisConstants.PLUGIN_ID);
        }

        @Override
        public String getLocaleKey() {
            return "Plugin-Redis_DB";
        }

        @Override
        public String toString() {
            return Inter.getLocText("Plugin-Redis_DB");
        }
    };

    private static final String ATTR_TAG = "RedisTableDataAttr";

    private Connection database;


    private String dbName;

    private String tableName;

    private String query;

    private String filter;

    private String sort;

    private boolean showUniqueID;

    public void setDatabase(Connection c) {
        this.database = c;
    }

    public Connection getDatabase() {
        return database;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }

    public boolean isShowUniqueID() {
        return showUniqueID;
    }

    public void setShowUniqueID(boolean showUniqueID) {
        this.showUniqueID = showUniqueID;
    }

    public void setParameters(ParameterProvider[] providers) {
        super.setDefaultParameters(providers);
    }

    @Override
    public DataModel createDataModel(Calculator calculator) {
        return createDataModel(calculator, TableData.RESULT_ALL);
    }

    @Override
    public DataModel createDataModel(Calculator calculator, int rowCount) {
        FunctionProcessor processor = ExtraClassManager.getInstance().getFunctionProcessor();
        if (processor != null) {
            processor.recordFunction(REDIS);
        }
        Parameter[] ps = Parameter.providers2Parameter(Calculator.processParameters(calculator, parameters));
        if (database instanceof NameDatabaseConnection) {
            String name = ((NameDatabaseConnection) database).getName();
            RedisDatabaseConnection rc = DatasourceManager.getProviderInstance().getConnection(name, RedisDatabaseConnection.class);
            if (rc != null) {
                return new RedisTableDataModel(rc,
                        dbName,
                        tableName,
                        calculateQuery(query, ps),
                        calculateQuery(filter, ps),
                        calculateQuery(sort, ps),
                        rowCount);
            }
        }
        return null;
    }

    private String calculateQuery(String query, Parameter[] ps) {
        if (ArrayUtils.isEmpty(ps)) {
            return query;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        for (Parameter p : ps) {
            map.put(p.getName(), p.getValue());
        }
        try {
            return TemplateUtils.renderParameter4Tpl(query, map);
        } catch (Exception e) {
            return query;
        }
    }

    public void readXML(XMLableReader reader) {
        super.readXML(reader);

        if (reader.isChildNode()) {
            String tmpName = reader.getTagName();
            String tmpVal;

            if (com.fr.data.impl.Connection.XML_TAG.equals(tmpName)) {
                if (reader.getAttrAsString("class", null) != null) {
                    com.fr.data.impl.Connection con = DataCoreXmlUtils.readXMLConnection(reader);
                    this.setDatabase(con);
                }
            } else if (ATTR_TAG.equals(tmpName)) {
                dbName = reader.getAttrAsString("dbName", StringUtils.EMPTY);
                tableName = reader.getAttrAsString("tableName", StringUtils.EMPTY);
                showUniqueID = reader.getAttrAsBoolean("showUniqueID", false);
            } else if ("Query".equals(tmpName)) {
                if ((tmpVal = reader.getElementValue()) != null) {
                    this.setQuery(tmpVal);
                }
            } else if ("Filter".equals(tmpName)) {
                if ((tmpVal = reader.getElementValue()) != null) {
                    this.setFilter(tmpVal);
                }
            } else if ("Sort".equals(tmpName)) {
                if ((tmpVal = reader.getElementValue()) != null) {
                    this.setSort(tmpVal);
                }
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        super.writeXML(writer);
        if (this.database != null) {
            DataCoreXmlUtils.writeXMLConnection(writer, this.database);
        }
        writer.startTAG(ATTR_TAG);
        writer.attr("dbName", dbName);
        writer.attr("tableName", tableName);
        if (showUniqueID) {
            writer.attr("showUniqueID", true);
        }
        writer.end();
        writer.startTAG("Query").textNode(getQuery()).end();
        writer.startTAG("Filter").textNode(getFilter()).end();
        writer.startTAG("Sort").textNode(getSort()).end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        RedisTableData cloned = (RedisTableData) super.clone();
        cloned.database = database;
        cloned.dbName = dbName;
        cloned.tableName = tableName;
        cloned.showUniqueID = showUniqueID;
        cloned.query = query;
        cloned.filter = filter;
        cloned.sort = sort;
        return cloned;
    }
}