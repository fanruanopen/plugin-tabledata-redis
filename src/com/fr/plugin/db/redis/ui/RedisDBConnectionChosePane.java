package com.fr.plugin.db.redis.ui;

import com.fr.base.FRContext;
import com.fr.data.impl.Connection;
import com.fr.design.data.datapane.connect.ConnectionComboBoxPanel;
import com.fr.design.dialog.BasicPane;
import com.fr.file.DatasourceManager;
import com.fr.plugin.db.redis.ui.event.DataLoadedListener;
import com.fr.plugin.db.redis.core.RedisDatabaseConnection;
import com.fr.stable.ArrayUtils;
import com.fr.stable.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class RedisDBConnectionChosePane extends BasicPane {

    private ConnectionComboBoxPanel connectionComboBoxPanel;
    private DefaultListModel listModel = new DefaultListModel();
    private List<DataLoadedListener> listeners = new ArrayList<DataLoadedListener>();

    public RedisDBConnectionChosePane() {
        setLayout(new BorderLayout(4, 4));
        connectionComboBoxPanel = new ConnectionComboBoxPanel(Connection.class) {

            protected void filterConnection(Connection connection, String conName, List<String> nameList) {
                connection.addConnection(nameList, conName, new Class[]{RedisDatabaseConnection.class});
            }
        };

        add(connectionComboBoxPanel, BorderLayout.NORTH);
        connectionComboBoxPanel.addComboBoxActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = getSelectRedisConnectionName();
                if (StringUtils.isEmpty(name)) {
                    clearList();
                    fireDataLoaded(ArrayUtils.EMPTY_STRING_ARRAY);
                    return;
                }
                RedisDatabaseConnection connection = DatasourceManager.getProviderInstance().getConnection(name, RedisDatabaseConnection.class);
                if (connection != null) {
                    listAllDBNames(connection);
                }
            }
        });
        JList list = new JList(listModel);
        add(list, BorderLayout.CENTER);
    }

    private void clearList() {
        listModel.clear();
    }

    private void listAllDBNames(final RedisDatabaseConnection connection) {
        clearList();
        new SwingWorker<String[], Void>() {

            @Override
            protected String[] doInBackground() throws Exception {
                if (StringUtils.isNotEmpty(connection.getDefaultDatabaseName())) {
                    return new String[]{connection.getDefaultDatabaseName()};
                }

                List<String> names = new ArrayList<String>();
                return names.toArray(new String[names.size()]);
            }

            @Override
            protected void done() {
                try {
                    String[] names = get();
                    for (String name : names) {
                        listModel.addElement(name);
                    }
                    fireDataLoaded(names);
                } catch (InterruptedException e) {
                    FRContext.getLogger().error(e.getMessage(), e);
                } catch (ExecutionException e) {
                    FRContext.getLogger().error(e.getMessage(), e);
                }
            }
        }.execute();
    }

    public String getSelectRedisConnectionName() {
        return connectionComboBoxPanel.getSelectedItem();
    }

    public void populateConnection(Connection connection) {
        connectionComboBoxPanel.populate(connection);
    }

    public void addDataLoadedListener(DataLoadedListener listener) {
        listeners.add(listener);
    }

    private void fireDataLoaded(String[] data) {
        for (DataLoadedListener listener : listeners) {
            listener.fireEvent(data);
        }
    }

    @Override
    protected String title4PopupWindow() {
        return "Choose";
    }
}