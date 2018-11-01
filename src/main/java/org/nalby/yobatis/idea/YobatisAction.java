package org.nalby.yobatis.idea;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import org.nalby.yobatis.core.Yobatis;
import org.nalby.yobatis.core.log.AbstractLogger;
import org.nalby.yobatis.core.log.LogFactory;
import org.nalby.yobatis.core.mybatis.ConfigGenerator;

import java.io.File;
import java.net.URL;

public class YobatisAction extends AnAction {

    static {
        LogFactory.setLogger(IdeaLogger.class);
        AbstractLogger.defaultLevel = AbstractLogger.LogLevel.DEBUG;
    }

    public YobatisAction(){
        super("Yobatis");
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        IdeaProject ideaProject = IdeaProject.wrap(project);
        System.out.println(ideaProject.getAbsPathOfSqlConnector());
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file != null && ConfigGenerator.CONFIG_FILENAME.equals(file.getName())) {
            Yobatis.onClickFile(ideaProject);
        } else if (file != null) {
            Yobatis.onClickProject(ideaProject);
        }
    }

    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if ((file != null && ConfigGenerator.CONFIG_FILENAME.equals(file.getName())) ||
            (file != null && file.getName().equals(project.getName()))) {
            event.getPresentation().setVisible(true);
        } else {
            event.getPresentation().setVisible(false);
        }
    }
}
