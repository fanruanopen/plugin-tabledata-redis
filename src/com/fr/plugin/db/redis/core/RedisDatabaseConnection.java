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
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.exceptions.JedisConnectionException;



public class RedisDatabaseConnection extends AbstractDatabaseConnection {

    private static final int DEFAULT_REDIS_PORT = 6379 ;


    private String host;
    private int port = DEFAULT_REDIS_PORT;
    private String username;
    private String password;
    private String defaultDatabaseName;
    private String options;
    private RedisMechanism mechanism = RedisMechanism.NONE;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RedisMechanism getMechanism() {
        return mechanism;
    }

    public void setMechanism(RedisMechanism mechanism) {
        this.mechanism = mechanism;
    }

    public String getDefaultDatabaseName() {
        return defaultDatabaseName;
    }

    public void setDefaultDatabaseName(String defaultDatabaseName) {
        this.defaultDatabaseName = defaultDatabaseName;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }


    @Override
    public void testConnection() throws Exception {
        Jedis client = createRedisClient();
        if(client == null) {
            throw new JedisConnectionException("Error to connect redisDB!");
        }
        else {
            return;
        }
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


    public Jedis createRedisClient() {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        System.out.println("clear all " + jedis.flushDB());
        jedis.sadd("myset","redis");
        jedis.sadd("myset","mongodb");
        jedis.lpush("mylist", "mysql");
        jedis.lpush("mylist", "sqlite");
        jedis.hset("myhash", "filed1", "value1");
        jedis.hset("myhash", "filed2", "value2");
        jedis.zadd("myzset", 1940, "Kay");
        jedis.zadd("myzset", 1912, "Turing");
        jedis.zadd("myzset", 1916, "Shannon");
        return jedis;
    }



    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isChildNode()) {
            String tagName = reader.getTagName();
            if ("Attr".equals(tagName)) {
                host = reader.getAttrAsString("host", StringUtils.EMPTY);
                port = reader.getAttrAsInt("port", DEFAULT_REDIS_PORT);
                username = reader.getAttrAsString("username", StringUtils.EMPTY);
                String pwd = reader.getAttrAsString("password", StringUtils.EMPTY);
                if (StringUtils.isNotEmpty(pwd)) {
                    password = CodeUtils.passwordDecode(pwd);
                }
                mechanism = RedisMechanism.parse(reader.getAttrAsInt("mechanism", 0));
                defaultDatabaseName = reader.getAttrAsString("defaultDB", StringUtils.EMPTY);
                options = reader.getAttrAsString("options", StringUtils.EMPTY);
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        super.writeXML(writer);
        writer.startTAG("Attr");
        writer.attr("host", host);
        writer.attr("port", port);
        writer.attr("username", username);
        if (StringUtils.isNotEmpty(password)) {
            writer.attr("password", CodeUtils.passwordEncode(password));
        }
        writer.attr("mechanism", mechanism.toInt());
        writer.attr("defaultDB", defaultDatabaseName);
        if (StringUtils.isNotEmpty(options)) {
            writer.attr("options", options);
        }
        writer.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        com.fr.plugin.db.redis.core.RedisDatabaseConnection cloned = (com.fr.plugin.db.redis.core.RedisDatabaseConnection) super.clone();
        cloned.host = host;
        cloned.port = port;
        cloned.username = username;
        cloned.password = password;
        cloned.mechanism = mechanism;
        cloned.defaultDatabaseName = defaultDatabaseName;
        cloned.options = options;
        return cloned;
    }
}