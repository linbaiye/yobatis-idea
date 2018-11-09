package org.nalby.yobatis.idea.logging;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

public class LoggingConsoleManager {

    private static LoggingConsoleManager instance;

    private ConsoleView consoleView;

    private Content content;

    private ToolWindow toolWindow;

    public LoggingConsoleManager(ConsoleView consoleView, Content content, ToolWindow toolWindow) {
        this.consoleView = consoleView;
        this.content = content;
        this.toolWindow = toolWindow;
    }

    public void activateLoggingConsole() {
        toolWindow.activate(() -> {}, true, true);
        toolWindow.getContentManager().setSelectedContent(content);
    }

    public void appendError(String msg) {
        consoleView.print(msg, ConsoleViewContentType.ERROR_OUTPUT);
    }

    public void appendDebug(String msg) {
        consoleView.print(msg, ConsoleViewContentType.NORMAL_OUTPUT);
    }

    public void appendInfo(String msg) {
        consoleView.print(msg, ConsoleViewContentType.NORMAL_OUTPUT);
    }

    public synchronized static LoggingConsoleManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized.");
        }
        return instance;
    }

    public synchronized static LoggingConsoleManager init(Project project) {
        ToolWindow outputWindow = ToolWindowManager.getInstance(project).registerToolWindow("Yobatis Output",
                true, ToolWindowAnchor.BOTTOM);

        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();

        Content content = outputWindow.getContentManager().getFactory().createContent(consoleView.getComponent(),
                "", false);
        outputWindow.getContentManager().addContent(content);
        instance = new LoggingConsoleManager(consoleView, content, outputWindow);
        return instance;
    }
}
