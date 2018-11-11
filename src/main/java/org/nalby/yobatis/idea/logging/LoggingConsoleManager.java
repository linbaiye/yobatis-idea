package org.nalby.yobatis.idea.logging;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

public class LoggingConsoleManager {

    private static LoggingConsoleManager instance;

    private ConsoleView consoleView;

    private ToolWindow toolWindow;

    private Content content;

    private final static Key<ConsoleView> CONSOLE_KEY = new Key<>("consoleView");
    private final static Key<ToolWindow> WINDOW_KEY = new Key<>("toolWindow");
    private final static Key<Content> CONTENT_KEY = new Key<>("content");

    public LoggingConsoleManager(ConsoleView consoleView, ToolWindow toolWindow, Content content) {
        this.consoleView = consoleView;
        this.toolWindow = toolWindow;
        this.content = content;
    }

    public void activateLoggingConsole(Project project) {
        toolWindow = project.getUserData(WINDOW_KEY);
        content = project.getUserData(CONTENT_KEY);
        consoleView = project.getUserData(CONSOLE_KEY);
        if (toolWindow != null && content != null) {
            toolWindow.activate(() -> { }, true, true);
            toolWindow.getContentManager().setSelectedContent(content);
        }
    }

    public void appendError(String msg) {
        if (consoleView != null) {
            consoleView.print(msg, ConsoleViewContentType.ERROR_OUTPUT);
        }
    }

    public void appendDebug(String msg) {
        if (consoleView != null) {
            consoleView.print(msg, ConsoleViewContentType.NORMAL_OUTPUT);
        }
    }

    public void appendInfo(String msg) {
        if (consoleView != null) {
            consoleView.print(msg, ConsoleViewContentType.NORMAL_OUTPUT);
        }
    }


    public synchronized static LoggingConsoleManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized.");
        }
        return instance;
    }


    public synchronized static LoggingConsoleManager newInstance(Project project) {

        ToolWindow outputWindow = ToolWindowManager.getInstance(project).registerToolWindow("Yobatis Output",
                true, ToolWindowAnchor.BOTTOM);

        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();

        Content content = outputWindow.getContentManager().getFactory().createContent(consoleView.getComponent(),
                "", false);
        outputWindow.getContentManager().addContent(content);
        project.putUserData(WINDOW_KEY, outputWindow);
        project.putUserData(CONSOLE_KEY, consoleView);
        project.putUserData(CONTENT_KEY, content);
        instance = new LoggingConsoleManager(consoleView, outputWindow, content);
        return instance;
    }
}
