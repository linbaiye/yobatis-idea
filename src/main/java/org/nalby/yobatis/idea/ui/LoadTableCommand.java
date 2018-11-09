package org.nalby.yobatis.idea.ui;

import org.nalby.yobatis.core.mybatis.Settings;
import org.nalby.yobatis.core.mybatis.TableElement;

import java.util.List;

public class LoadTableCommand extends Command<List<TableElement>> {

    private Settings settings;

    public LoadTableCommand(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }
}
