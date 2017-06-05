package com.fr.plugin.db.redis.core;

import com.fr.base.Parameter;
import com.fr.base.TableData;
import com.fr.base.TemplateUtils;
import com.fr.data.AbstractParameterTableData;
import com.fr.data.core.DataCoreXmlUtils;
import com.fr.data.impl.Connection;
import com.fr.data.impl.NameDatabaseConnection;
import com.fr.file.DatasourceManager;
import com.fr.general.GeneralUtils;
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

    private Connection database;
    private int dbIndex;
    private String query;


    public void setDatabase(Connection c) {
        this.database = c;
    }

    public Connection getDatabase() {
        return database;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(int dbIndex) {
        this.dbIndex = dbIndex;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
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
                return new RedisTableDataModel(calculator, ps, rc,
                        dbIndex,
                        calculateQuery(query, ps),
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

            if ("RedisAttr".equals(tmpName)) {
                this.dbIndex = reader.getAttrAsInt("dbIndex", RedisConstants.DEFAULT_DB_INDEX);
            } else if (com.fr.data.impl.Connection.XML_TAG.equals(tmpName)) {
                if (reader.getAttrAsString("class", null) != null) {
                    com.fr.data.impl.Connection con = DataCoreXmlUtils.readXMLConnection(reader);
                    this.setDatabase(con);
                }
            } else if ("Query".equals(tmpName)) {
                tmpVal = reader.getElementValue();
                if (isNotNullValue(tmpVal)) {
                    this.setQuery(tmpVal);
                }
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        super.writeXML(writer);
        writer.startTAG("RedisAttr");
        writer.attr("dbIndex", dbIndex);
        writer.end();
        if (this.database != null) {
            DataCoreXmlUtils.writeXMLConnection(writer, this.database);
        }
        writer.startTAG("Query").textNode(getQuery()).end();
    }

    private boolean isNotNullValue(String value) {
        return value != null && !"null".equals(value);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        RedisTableData cloned = (RedisTableData) super.clone();
        cloned.database = (Connection) database.clone();
        cloned.query = query;
        cloned.dbIndex = dbIndex;
        return cloned;
    }
}