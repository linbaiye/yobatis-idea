package org.nalby.yobatis.idea.logging;

import org.nalby.yobatis.core.log.AbstractLogger;

public class IdeaLogger extends AbstractLogger {

    public IdeaLogger(String name) {
        super(name);
    }

    @Override
    protected void wirteToConsole(String s) {

    }

    @Override
    public void info(String format, Object... args) {
        LoggingConsoleManager.getInstance().appendInfo(format("INFO ", format, args));
    }

    @Override
    public void debug(String format, Object... args) {
        LoggingConsoleManager.getInstance().appendDebug(format("DEBUG", format, args));
    }

    @Override
    public void error(String format, Object... args) {
        LoggingConsoleManager.getInstance().appendError(format("ERROR", format, args));
    }
}
