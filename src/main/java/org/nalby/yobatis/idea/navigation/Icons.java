package org.nalby.yobatis.idea.navigation;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public final class Icons {
    private Icons() {
        throw new IllegalStateException("Do not construct me.");
    }
    public static final Icon MYBATIS_ICON = IconLoader.findIcon("/icons/icon1.png");
}
