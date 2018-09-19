package org.nalby.yobatis.idea;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import org.nalby.yobatis.core.log.AbstractLogger;

public class IdeaLogger extends AbstractLogger {

    public IdeaLogger(String name) {
        super(name);
    }

    @Override
    protected void wirteToConsole(String s) {
        Notification notification = new Notification("Yobatis", "Yobatis", s, NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);
        notification.hideBalloon();
    }

    @Override
    public void info(String format, Object... args) {
        wirteToConsole("INFO - " + formatArgs(format, args));
    }

    @Override
    public void debug(String format, Object... args) {
        wirteToConsole("DEBUG - " + formatArgs(format, args));
    }

    @Override
    public void error(String format, Object... args) {
        wirteToConsole("ERROR - " + formatArgs(format, args));
    }
}
