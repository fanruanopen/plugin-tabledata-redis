package com.fr.plugin.db.redis.core;

import com.fr.data.impl.AbstractDatabaseConnection;
import com.fr.data.impl.Connection;
import com.fr.general.Inter;
import com.fr.stable.CodeUtils;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;

public class RedisDatabaseConnection extends AbstractDatabaseConnection {

    private static final int DEFAULT_REDIS_PORT = 6379;


    private String host;
    private int port = DEFAULT_REDIS_PORT;
    private String password;

    public RedisDatabaseConnection() {

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public void testConnection() throws Exception {
        Jedis client = createRedisClient();
        client.dbSize();
    }

    public Jedis createRedisClient() {
        Jedis client = new Jedis(host, port);
        if (StringUtils.isNotEmpty(password)) {
            client.auth(password);
        }
        return client;
    }

    @Override
    public java.sql.Connection createConnection() throws Exception {
        return null;
    }

    @Override
    public String connectMessage(boolean status) {
        if (status) {
            return Inter.getLocText("Datasource-Connection_successfully") + "!";
        } else {
            return Inter.getLocText("Datasource-Connection_failed") + "!";
        }
    }

    @Override
    public void addConnection(List<String> list, String connectionName, Class<? extends Connection>[] acceptTypes) {
        for (Class<? extends com.fr.data.impl.Connection> accept : acceptTypes) {
            if (StableUtils.classInstanceOf(getClass(), accept)) {
                list.add(connectionName);
                break;
            }
        }
    }

    @Override
    public String getDriver() {
        return null;
    }

    @Override
    public String getOriginalCharsetName() {
        return null;
    }

    @Override
    public void setOriginalCharsetName(String s) {

    }

    @Override
    public String getNewCharsetName() {
        return null;
    }

    @Override
    public void setNewCharsetName(String s) {

    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isChildNode()) {
            String tagName = reader.getTagName();
            if ("Attr".equals(tagName)) {
                host = reader.getAttrAsString("host", StringUtils.EMPTY);
                port = reader.getAttrAsInt("port", DEFAULT_REDIS_PORT);
                String pwd = reader.getAttrAsString("password", StringUtils.EMPTY);
                if (StringUtils.isNotEmpty(pwd)) {
                    password = CodeUtils.passwordDecode(pwd);
                }
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        super.writeXML(writer);
        writer.startTAG("Attr");
        writer.attr("host", host);
        writer.attr("port", port);
        if (StringUtils.isNotEmpty(password)) {
            writer.attr("password", CodeUtils.passwordEncode(password));
        }
        writer.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        com.fr.plugin.db.redis.core.RedisDatabaseConnection cloned = (com.fr.plugin.db.redis.core.RedisDatabaseConnection) super.clone();
        cloned.host = host;
        cloned.port = port;
        cloned.password = password;
        return cloned;
    }
}