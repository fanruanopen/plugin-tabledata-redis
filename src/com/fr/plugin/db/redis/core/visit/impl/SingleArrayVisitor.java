package com.fr.plugin.db.redis.core.visit.impl;

import com.fr.base.Parameter;
import com.fr.general.FRLogger;
import com.fr.plugin.db.redis.conf.ShellConfigManager;
import com.fr.plugin.db.redis.core.DataWrapper;
import com.fr.plugin.db.redis.core.visit.AbstractVisitor;
import com.fr.script.Calculator;
import com.fr.stable.ArrayUtils;
import com.fr.stable.StringUtils;
import redis.clients.jedis.Jedis;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by richie on 2017/6/5.
 */
public class SingleArrayVisitor extends AbstractVisitor<String> {
    @Override
    public List<List<String>> getContent(Calculator calculator, Parameter[] ps, Jedis client, String query, int rowCount) throws Exception {
        String[] arr = query.trim().split(TOKEN_SPACE);
        if (ArrayUtils.getLength(arr) < 1) {
            throw new IllegalArgumentException("Illegal query:" + query);
        }
        String shell = ShellConfigManager.getProviderInstance().getShellText();
        if (StringUtils.isNotEmpty(shell)) {
            for (Parameter parameter : ps) {
                shell += " " + parameter.getName() + " " + parameter.getValue();
            }
            Process process = Runtime.getRuntime().exec(shell);
            String statusText = getShellResult(process);
            if (isSuccess(statusText)) {
                return calculateData(client, arr[1]);
            }
        }
        return null;
    }

    @Override
    public String keyWord() {
        return "singlemaparray";
    }


    private boolean isSuccess(String statusText) {
        return "success".equals(statusText);
    }

    // TODO:richie 只要实现这个方法就可以了,还要处理列名的时候，需要修改下面 buildData方法
    private List<List<String>> calculateData(Jedis client, String key) {
        String result = client.get(key);
        return null;
    }

    @Override
    public DataWrapper<String> buildData(Calculator calculator, Parameter[] ps, Jedis client, String query, int rowCount) throws Exception {
        return super.buildData(calculator, ps, client, query, rowCount);
    }

    private String getShellResult(Process process) {
        String result = null;
        try {
            ByteArrayOutputStream resultOutStream = new ByteArrayOutputStream();
            InputStream errorInStream = new BufferedInputStream(process.getErrorStream());
            InputStream processInStream = new BufferedInputStream(process.getInputStream());
            int num = 0;
            byte[] bs = new byte[1024];
            while ((num = errorInStream.read(bs)) != -1) {
                resultOutStream.write(bs, 0, num);
            }
            while ((num = processInStream.read(bs)) != -1) {
                resultOutStream.write(bs, 0, num);
            }
            result = new String(resultOutStream.toByteArray());
            errorInStream.close();
            processInStream.close();
            resultOutStream.close();
        } catch (IOException e) {
            FRLogger.getLogger().error(e.getMessage(), e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }
}
