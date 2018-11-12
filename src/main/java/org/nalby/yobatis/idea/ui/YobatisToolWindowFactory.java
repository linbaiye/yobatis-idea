package org.nalby.yobatis.idea.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.nalby.yobatis.core.log.LoggerFactory;
import org.nalby.yobatis.idea.logging.IdeaLogger;
import org.nalby.yobatis.idea.logging.LoggingConsoleManager;


public class YobatisToolWindowFactory implements ToolWindowFactory {


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Must initialize logger at very first.
        LoggerFactory.setLogger(IdeaLogger.class);
        IdeaLogger.defaultLevel = IdeaLogger.LogLevel.INFO;

        LoggingAwareCommandExecutor executor = LoggingAwareCommandExecutor.newInstance(project);
        YobatisToolWindow yobatisToolWindow = new YobatisToolWindow(toolWindow, executor);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(yobatisToolWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);

        yobatisToolWindow.executeLoadAll();
    }
}
