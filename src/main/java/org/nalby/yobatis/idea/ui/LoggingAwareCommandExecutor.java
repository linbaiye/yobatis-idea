package org.nalby.yobatis.idea.ui;

import com.intellij.openapi.project.Project;
import org.nalby.yobatis.core.YobatisShell;
import org.nalby.yobatis.core.log.Logger;
import org.nalby.yobatis.core.log.LoggerFactory;
import org.nalby.yobatis.core.mybatis.Settings;
import org.nalby.yobatis.idea.IdeaProject;
import org.nalby.yobatis.idea.logging.IdeaLogger;
import org.nalby.yobatis.idea.logging.LoggingConsoleManager;

import java.util.Collections;

/**
 * Execute commands from ui manager, also take care of logging.
 */
public class LoggingAwareCommandExecutor {

    private YobatisShell yobatisShell;

    private final static Logger LOGGER = LoggerFactory.getLogger(LoggingAwareCommandExecutor.class);

    private Project project;

    private LoggingAwareCommandExecutor(YobatisShell shell, Project project) {
        this.yobatisShell = shell;
        this.project = project;
    }

    @SuppressWarnings("unchecked")
    public void execute(Command command) {
        try {
            LoggingConsoleManager.getInstance().activateLoggingConsole(project);
            if (command instanceof GenerateCommand) {
                LOGGER.info("Generating files..");
                yobatisShell.save(((GenerateCommand) command).getSettings());
                yobatisShell.generate(((GenerateCommand) command).getTableElementList());
            } else if (command instanceof LoadSettingsCommand) {
                command.setResult(yobatisShell.loadSettings());
            } else if (command instanceof LoadTableCommand) {
                command.setResult(yobatisShell.loadTables());
            } else if (command instanceof SyncTablesCommand) {
                Settings settings = ((SyncTablesCommand) command).getSettings();
                if (!settings.isDatabaseConfigured()) {
                    LOGGER.info("Database is not configured yet.");
                    command.setResult(Collections.emptyList());
                    return;
                }
                yobatisShell.save(settings);
                yobatisShell.syncWithDatabase();
                command.setResult(yobatisShell.disableAll());
            } else {
                LOGGER.error("Unknown command.");
            }
        } catch (Exception e) {
            LOGGER.error("Caught exception: {}.", e);
        }
    }

    public synchronized static LoggingAwareCommandExecutor newInstance(com.intellij.openapi.project.Project project) {
        // Must initialize logger at very first.
        IdeaLogger.defaultLevel = IdeaLogger.LogLevel.INFO;
        LoggerFactory.setLogger(IdeaLogger.class);
        LoggingConsoleManager.newInstance(project);

        IdeaProject ideaProject = IdeaProject.wrap(project);
        YobatisShell shell = YobatisShell.newInstance(ideaProject);
        return new LoggingAwareCommandExecutor(shell, project);
    }
}
