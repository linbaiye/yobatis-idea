package org.nalby.yobatis.idea.ui;

import org.nalby.yobatis.core.mybatis.Settings;
import org.nalby.yobatis.core.mybatis.TableElement;

import java.util.List;

public class GenerateCommand extends Command<List<TableElement>> {

    private Settings settings;

    private List<TableElement> tableElementList;

    public GenerateCommand(Settings settings, List<TableElement> tableElementList) {
        this.settings = settings;
        this.tableElementList = tableElementList;
    }

    public Settings getSettings() {
        return settings;
    }

    public List<TableElement> getTableElementList() {
        return tableElementList;
    }
}
