package com.fr.plugin.db.redis.ui;

import com.fr.base.FRContext;
import com.fr.data.impl.Connection;
import com.fr.design.data.datapane.connect.ConnectionComboBoxPanel;
import com.fr.design.dialog.BasicPane;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.gui.itextfield.PlaceholderTextField;
import com.fr.design.utils.gui.GUICoreUtils;
import com.fr.file.DatasourceManager;
import com.fr.general.Inter;
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
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class RedisDBConnectionChosePane extends BasicPane {

    private ConnectionComboBoxPanel connectionComboBoxPanel;
    private DefaultListModel listModel = new DefaultListModel();
    private List<DataLoadedListener> listeners = new ArrayList<DataLoadedListener>();
    private PlaceholderTextField keysPatternTextField;

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
                loadKeys();
            }
        });
        JList list = new JList(listModel);

        keysPatternTextField = new PlaceholderTextField(10);
        keysPatternTextField.setPlaceholder(Inter.getLocText("Plugin-Redis_Keys_Pattern"));

        JPanel centerPane = new JPanel(new BorderLayout());

        UIButton searchButton = new UIButton(Inter.getLocText("Plugin-Redis_Keys_Pattern_Search"));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadKeys();
            }
        });

        centerPane.add(GUICoreUtils.createBorderLayoutPane(
                keysPatternTextField, BorderLayout.CENTER,
                searchButton, BorderLayout.EAST), BorderLayout.NORTH);

        centerPane.add(list, BorderLayout.CENTER);

        add(centerPane, BorderLayout.CENTER);
    }

    private void loadKeys() {
        String name = getSelectRedisConnectionName();
        if (StringUtils.isEmpty(name)) {
            clearList();
            fireDataLoaded(ArrayUtils.EMPTY_STRING_ARRAY);
            return;
        }
        RedisDatabaseConnection connection = DatasourceManager.getProviderInstance().getConnection(name, RedisDatabaseConnection.class);
        if (connection != null) {
            listMatchedPatternKeys(connection);
        }
    }

    private void clearList() {
        listModel.clear();
    }

    private void listMatchedPatternKeys(final RedisDatabaseConnection connection) {
        clearList();
        new SwingWorker<String[], Void>() {

            @Override
            protected String[] doInBackground() throws Exception {
                String keysPattern = keysPatternTextField.getText();
                if (StringUtils.isEmpty(keysPattern)) {
                    return ArrayUtils.EMPTY_STRING_ARRAY;
                } else {
                    Set<String> keys = connection.createRedisClient().keys(keysPattern);
                    return keys.toArray(new String[keys.size()]);
                }
            }

            @Override
            protected void done() {
                try {
                    String[] keys = get();
                    for (String name : keys) {
                        listModel.addElement(name);
                    }
                    fireDataLoaded(keys);
                } catch (Exception e) {
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