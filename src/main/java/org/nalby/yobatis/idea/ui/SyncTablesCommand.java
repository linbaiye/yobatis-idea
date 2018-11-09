package org.nalby.yobatis.idea.ui;

import org.nalby.yobatis.core.mybatis.Settings;
import org.nalby.yobatis.core.mybatis.TableElement;

import java.util.List;

public class SyncTablesCommand extends Command<List<TableElement>>  {

    private Settings settings;

    public SyncTablesCommand(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }
}
