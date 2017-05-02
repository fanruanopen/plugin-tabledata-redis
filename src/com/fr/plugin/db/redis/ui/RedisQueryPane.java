package com.fr.plugin.db.redis.ui;

import com.fr.design.border.UIRoundedBorder;
import com.fr.design.constants.UIConstants;
import com.fr.design.data.datapane.sqlpane.SQLEditPane;
import com.fr.design.dialog.BasicPane;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.ActionLabel;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.gui.syntax.ui.rsyntaxtextarea.SyntaxConstants;
import com.fr.design.gui.syntax.ui.rtextarea.RTextScrollPane;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.utils.gui.GUICoreUtils;
import com.fr.general.FRLogger;
import com.fr.general.GeneralUtils;
import com.fr.general.Inter;
import com.fr.general.SiteCenter;
import com.fr.stable.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;


public class RedisQueryPane extends BasicPane {

    private UIComboBox dbNameCombobox;
    private UITextField tableNameTextField;

    private SQLEditPane sqlTextPane;
    private SQLEditPane filterTextPane;
    private SQLEditPane sortTextPane;


    private String dbName;

    private boolean isFirstTime = true;

    public RedisQueryPane() {
        setLayout(new BorderLayout());

        dbNameCombobox = new UIComboBox();
        tableNameTextField = new UITextField();
        sqlTextPane = new SQLEditPane();
        filterTextPane = new SQLEditPane();
        sortTextPane = new SQLEditPane();

        ActionLabel helpLabel = new ActionLabel(Inter.getLocText("Plugin-Redis_Help"));
        helpLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(URI.create(SiteCenter.getInstance().acquireUrlByKind("help.mongodb")));
                } catch (IOException e1) {
                    FRLogger.getLogger().error(e1.getMessage(), e1);
                }
            }
        });

        Component[][] coms = new Component[][]{
                {new UILabel(Inter.getLocText("Plugin-Redis_Database_Name") + ":"), GUICoreUtils.createBorderLayoutPane(
                        dbNameCombobox, BorderLayout.CENTER,
                        helpLabel, BorderLayout.EAST
                )},
                {new UILabel(Inter.getLocText("Plugin-Redis_Table_Name") + ":"), tableNameTextField},
                {new UILabel(Inter.getLocText("Plugin-Redis_Query_Condition") + ":"),  createConditionTextPane(sqlTextPane)},
                {new UILabel(Inter.getLocText("Plugin-Redis_Filter_Condition") + ":"), createConditionTextPane(filterTextPane)},
                {new UILabel(Inter.getLocText("Plugin-Redis_Sort_Condition") + ":"), createConditionTextPane(sortTextPane)}
        };

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;

        double[] rowSize = {p, p, p, p, p};
        double[] columnSize = {p, f};

        JPanel panel = TableLayoutHelper.createTableLayoutPane(coms, rowSize, columnSize);

        add(panel, BorderLayout.CENTER);
    }

    private RTextScrollPane createConditionTextPane(SQLEditPane sqlTextPane) {
        sqlTextPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        RTextScrollPane sqlTextScrollPane = new RTextScrollPane(sqlTextPane);
        sqlTextScrollPane.setBorder(new UIRoundedBorder(UIConstants.LINE_COLOR, 1, UIConstants.ARC));
        sqlTextScrollPane.setPreferredSize(new Dimension(680, 60));
        return sqlTextScrollPane;
    }

    @Override
    protected String title4PopupWindow() {
        return "Query";
    }

    public void loadDBNames(String[] names) {
        if (!isFirstTime) {
            setQuery(StringUtils.EMPTY);
            setDBName(StringUtils.EMPTY);
            setTableName(StringUtils.EMPTY);
        }
        DefaultComboBoxModel model = (DefaultComboBoxModel) dbNameCombobox.getModel();
        model.removeAllElements();
        for (String name : names) {
            model.addElement(name);
        }
        model.setSelectedItem(dbName);
        isFirstTime = false;
    }

    public String getQuery() {
        return sqlTextPane.getText();
    }

    public void setQuery(String query) {
        sqlTextPane.setText(query);
    }

    public String getFilter() {
        return filterTextPane.getText();
    }

    public void setFilter(String filter) {
        filterTextPane.setText(filter);
    }

    public String getSort() {
        return sortTextPane.getText();
    }

    public void setSort(String sort) {
        sortTextPane.setText(sort);
    }

    public String getDBName() {
        return GeneralUtils.objectToString(dbNameCombobox.getSelectedItem());
    }

    public void setDBName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableNameTextField.getText();
    }

    public void setTableName(String tableName) {
        tableNameTextField.setText(tableName);
    }
}