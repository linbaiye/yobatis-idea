package org.nalby.yobatis.idea.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.nalby.yobatis.core.log.AbstractLogger;
import org.nalby.yobatis.core.log.LoggerFactory;
import org.nalby.yobatis.core.mybatis.Settings;
import org.nalby.yobatis.core.mybatis.TableElement;
import org.nalby.yobatis.idea.logging.IdeaLogger;
import org.nalby.yobatis.idea.logging.LoggingConsoleManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class YobatisToolWindow implements ToolWindowFactory {
    private JPanel body;
    private JPanel configurationBox;
    private JTextField url;
    private JTextField username;
    private JTextField password;
    private JTextField entityPath;
    private JTextField entityPackage;
    private JTextField daoPath;
    private JTextField daoPackage;
    private JTextField xmlMapperPath;
    private JButton configButton;
    private JButton generateButton;
    private JList tableList;
    private JPanel databaseSettings;
    private JPanel yobatisSettings;
    private JScrollPane scrollPanel;
    private JPanel menu;
    private JButton refreshButton;
    private JTextField mapperPackage;

    private boolean configureEnabled;

    private LoggingAwareCommandExecutor executor;

    public YobatisToolWindow() {
        configureEnabled = true;
        bindListeners();
        initJList();
        toggleSettingsView();
    }

    private void initJList() {
        tableList.setCellRenderer(new CheckboxListRenderer());
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableList.setVisible(true);
        tableList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                JList<CheckboxListItem> list = (JList<CheckboxListItem>) event.getSource();
                if (list == null || list.getModel() == null || list.getModel().getSize() == 0) {
                    return;
                }
                int index = list.locationToIndex(event.getPoint());
                CheckboxListItem item = list.getModel().getElementAt(index);
                item.setSelected(!item.isSelected());
                list.repaint(list.getCellBounds(index, index));
            }
        });
    }

    private void toggleSettingsView() {
        configureEnabled = !configureEnabled;
        configurationBox.setVisible(configureEnabled);
    }

    private Settings collectSettingsFromView() {
        Settings current = new Settings();
        current.setDaoPackage(daoPackage.getText());
        current.setDaoPath(daoPath.getText());
        current.setEntityPackage(entityPackage.getText());
        current.setEntityPath(entityPath.getText());
        current.setUrl(url.getText());
        current.setPassword(password.getText());
        current.setUser(username.getText());
        current.setXmlPath(xmlMapperPath.getText());
        return current;
    }

    private List<TableElement> collectTablesFromView() {
        ListModel modelList = tableList.getModel();
        if (modelList == null || modelList.getSize() == 0) {
            return Collections.emptyList();
        }
        List<TableElement> tableElementList = new ArrayList<>(modelList.getSize());
        for (int i = 0; i < modelList.getSize(); i++) {
            CheckboxListItem checkboxListItem = (CheckboxListItem) modelList.getElementAt(i);
            tableElementList.add(checkboxListItem.asTableElement());
        }
        return tableElementList;
    }

    private void updateSettingsView(Settings settings) {
        if (settings != null) {
            password.setText(settings.getPassword());
            username.setText(settings.getUser());
            url.setText(settings.getUrl());
            daoPackage.setText(settings.getDaoPackage());
            daoPath.setText(settings.getDaoPath());
            entityPath.setText(settings.getEntityPath());
            entityPackage.setText(settings.getEntityPackage());
            xmlMapperPath.setText(settings.getXmlPath());
        }
    }

    private void updateTablesView(List<TableElement> tableElementList) {
        if (tableElementList != null) {
            ListModel<CheckboxListItem>  jbCheckBoxListModel = new DefaultListModel<>();
            for (TableElement tableElement : tableElementList) {
                CheckboxListItem jbCheckBox  = CheckboxListItem.fromTableElement(tableElement);
                ((DefaultListModel<CheckboxListItem>) jbCheckBoxListModel).addElement(jbCheckBox);
            }
            tableList.setModel(jbCheckBoxListModel);
        }
    }


    private LoadSettingsCommand makeLoadSettingsCommand() {
        return new LoadSettingsCommand();
    }

    private LoadTableCommand makeLoadTablesCommand() {
        return new LoadTableCommand(collectSettingsFromView());
    }

    private SyncTablesCommand makeSyncTableCommand() {
        return new SyncTablesCommand(collectSettingsFromView());
    }

    private GenerateCommand makeGenerateCommand() {
        Settings settings = collectSettingsFromView();
        List<TableElement> tableElementList = collectTablesFromView();
        return new GenerateCommand(settings, tableElementList);
    }

    private void executeLoadTables() {
        LoadTableCommand command1 = makeLoadTablesCommand();
        executor.execute(command1);
        updateTablesView(command1.getResult());
    }

    private void executeSyncTables() {
        SyncTablesCommand command = makeSyncTableCommand();
        executor.execute(command);
        updateTablesView(command.getResult());
    }

    private void executeLoadAll() {
        LoadSettingsCommand command = makeLoadSettingsCommand();
        executor.execute(command);
        updateSettingsView(command.getResult());
        executeLoadTables();
    }

    private void executeGenerateCommand() {
        GenerateCommand generateCommand = makeGenerateCommand();
        executor.execute(generateCommand);
    }

    private void bindListeners() {
        configButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSettingsView();
            }
        });
        refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                executeSyncTables();
            }
        });
        generateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                executeGenerateCommand();
            }
        });
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Must initialize logger at very first.
        IdeaLogger.defaultLevel = IdeaLogger.LogLevel.INFO;
        LoggerFactory.setLogger(IdeaLogger.class);
        LoggingConsoleManager.init(project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(body, "", false);
        toolWindow.getContentManager().addContent(content);
        executor = LoggingAwareCommandExecutor.newInstance(project);
        executeLoadAll();
    }

}
