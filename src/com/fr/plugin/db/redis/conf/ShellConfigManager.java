package com.fr.plugin.db.redis.conf;

import com.fr.cluster.rpc.RPC;
import com.fr.file.BaseClusterHelper;
import com.fr.file.XMLFileManager;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLTools;
import com.fr.stable.xml.XMLableReader;

import java.io.InputStream;

/**
 * Created by richie on 2017/6/5.
 */
public class ShellConfigManager extends XMLFileManager implements ShellConfigManagerProvider {

    private static final String XML_TAG = "ShellConfigManager";

    private static ShellConfigManagerProvider shellManager = null;


    public synchronized static ShellConfigManager getInstance() {
        return (ShellConfigManager) getProviderInstance();
    }

    public synchronized static ShellConfigManagerProvider getProviderInstance() {
        if (shellManager == null) {
            if (isClusterMember()) {
                return shellManager;
            }
            shellManager.readXMLFile();
        }
        return shellManager;
    }

    private synchronized static boolean isClusterMember() {
        switch (BaseClusterHelper.getClusterState()) {
            case LEADER:
                shellManager = new ShellConfigManager();
                RPC.registerSkeleton(shellManager);
                return false;
            case MEMBER:
                String ip = BaseClusterHelper.getMainServiceIP();
                shellManager = (ShellConfigManagerProvider) RPC.getProxy(ShellConfigManager.class, ip);
                return true;
            default:
                shellManager = new ShellConfigManager();
                break;
        }

        return false;
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            public void envChanged() {
                ShellConfigManager.envChanged();
            }
        });
    }

    private synchronized static void envChanged() {
        shellManager = null;
    }

    private String shellText;

    @Override
    public String getShellText() {
        return shellText;
    }

    public void setShellText(String shellText) {
        this.shellText = shellText;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            shellText = reader.getAttrAsString("shell", StringUtils.EMPTY);
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("shell", shellText);
        writer.end();
    }

    @Override
    public String fileName() {
        return "redis.xml";
    }

    @Override
    public void readFromInputStream(InputStream input) throws Exception {
        // 服务器端新建一个对象
        ShellConfigManager manager = new ShellConfigManager();
        // 从客户端传过来的inputstream中读取对象属性
        XMLTools.readInputStreamXML(manager, input);
        // 赋值给当前服务器端对象
        shellManager = manager;
        // 服务器端保存到本地xml中
        GeneralContext.getEnvProvider().writeResource(shellManager);
    }

    @Override
    public boolean writeResource() throws Exception {
        return GeneralContext.getEnvProvider().writeResource(ShellConfigManager.getProviderInstance());
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
