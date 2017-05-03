package com.fr.plugin.db.redis.ui;

import com.fr.design.border.UITitledBorder;
import com.fr.design.data.datapane.connect.DatabaseConnectionPane;
import com.fr.design.gui.ibutton.UIPasswordField;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.report.freeze.UIIntNumberField;
import com.fr.general.Inter;
import com.fr.plugin.db.redis.core.RedisDatabaseConnection;

import javax.swing.*;
import java.awt.*;


public class RedisConnectionPane extends DatabaseConnectionPane<com.fr.plugin.db.redis.core.RedisDatabaseConnection> {

    private UITextField hostTextField;
    private UIIntNumberField portNumberField;
    private UIPasswordField passwordTextField;


    @Override
    protected JPanel mainPanel() {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        hostTextField = new UITextField();
        portNumberField = new UIIntNumberField();
        passwordTextField = new UIPasswordField();

        Component[][] components = new Component[][]{
                {new UILabel(Inter.getLocText("Plugin-Redis_Host") + ":"), hostTextField},
                {new UILabel(Inter.getLocText("Plugin-Redis_Port") + ":"), portNumberField},
                {new UILabel(Inter.getLocText("Plugin-Redis_Password") + ":"), passwordTextField}
        };
        double p = TableLayout.PREFERRED;

        double[] rowSize = new double[]{p, p, p, p};
        double[] columnSize = new double[]{p, 400};

        JPanel settingsUI = TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);
        settingsUI.setBorder(UITitledBorder.createBorderWithTitle("Redis"));

        JPanel centerPane = FRGUIPaneFactory.createNormalFlowInnerContainer_M_Pane();

        centerPane.add(settingsUI);

        pane.add(centerPane, BorderLayout.CENTER);
        return pane;
    }

    @Override
    protected boolean isFineBI() {
        return false;
    }

    @Override
    protected void populateSubDatabaseConnectionBean(RedisDatabaseConnection ob) {
        hostTextField.setText(ob.getHost());
        portNumberField.setValue(ob.getPort());
        passwordTextField.setText(ob.getPassword());
    }

    @Override
    protected com.fr.plugin.db.redis.core.RedisDatabaseConnection updateSubDatabaseConnectionBean() {
        RedisDatabaseConnection connection = new RedisDatabaseConnection();
        connection.setHost(hostTextField.getText());
        connection.setPort((int) portNumberField.getValue());
        connection.setPassword(passwordTextField.getText());
        return connection;
    }

    @Override
    protected String title4PopupWindow() {
        return "Redis";
    }
}