package org.nalby.yobatis.idea;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.nalby.yobatis.core.log.AbstractLogger;
import org.nalby.yobatis.core.log.LoggerFactory;
import org.nalby.yobatis.idea.logging.IdeaLogger;

public class YobatisAction extends AnAction {

    static {
        LoggerFactory.setLogger(IdeaLogger.class);
        AbstractLogger.defaultLevel = AbstractLogger.LogLevel.DEBUG;
    }

    public YobatisAction(){
        super("Yobatis");
    }

    public void actionPerformed(AnActionEvent event) {
//        System.out.println(ideaProject.getAbsPathOfSqlConnector());
//        Project project = event.getProject();
//        if (project == null) {
//            return;
//        }
//        IdeaProject ideaProject = IdeaProject.wrap(project);
//        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
//        if (file != null && ConfigGenerator.CONFIG_FILENAME.equals(file.getName())) {
//            Yobatis.onClickFile(ideaProject);
//        } else if (file != null) {
//            Yobatis.onClickProject(ideaProject);
//        }
    }

    @Override
    public void update(AnActionEvent event) {
//        Project project = event.getProject();
//        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
//        if ((file != null && ConfigGenerator.CONFIG_FILENAME.equals(file.getName())) ||
//            (file != null && file.getName().equals(project.getName()))) {
//            event.getPresentation().setVisible(true);
//        } else {
//            event.getPresentation().setVisible(false);
//        }
    }
}
