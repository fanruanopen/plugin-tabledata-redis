package com.fr.plugin.db.redis.conf;

import com.fr.stable.FCloneable;
import com.fr.stable.file.RemoteXMLFileManagerProvider;

/**
 * Created by richie on 2017/6/5.
 */
public interface ShellConfigManagerProvider extends RemoteXMLFileManagerProvider, java.io.Serializable, FCloneable {

    String getShellText();
}
