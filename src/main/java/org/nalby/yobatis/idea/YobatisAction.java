package org.nalby.yobatis.idea;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class YobatisAction extends AnAction {
    public YobatisAction() {
        super("Yobatis Dao");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        System.out.println("HELLO");
    }
}
