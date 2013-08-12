/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.util.loging;

import com.codenvy.ide.util.ExceptionUtils;
import com.codenvy.ide.util.loging.LogConfig.LogLevel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
class DevModeLogger implements Logger {

    /** @see com.codenvy.ide.util.loging.Logger#debug(java.lang.Class, java.lang.Object[]) */
    @Override
    public void debug(Class<?> clazz, Object... args) {
        // DEBUG is the lowest log level, but we use <= for consistency, and in
        // case we ever decide to introduce a SPAM level.
        if (LogConfig.getLogLevel().ordinal() <= LogLevel.DEBUG.ordinal()) {
            log(clazz, LogLevel.DEBUG, args);
        }

    }

    /** @see com.codenvy.ide.util.loging.Logger#error(java.lang.Class, java.lang.Object[]) */
    @Override
    public void error(Class<?> clazz, Object... args) {
        log(clazz, LogLevel.ERROR, args);
    }

    /** @see com.codenvy.ide.util.loging.Logger#info(java.lang.Class, java.lang.Object[]) */
    @Override
    public void info(Class<?> clazz, Object... args) {
        if (LogConfig.getLogLevel().ordinal() <= LogLevel.INFO.ordinal()) {
            log(clazz, LogLevel.INFO, args);
        }
    }

    /** @see com.codenvy.ide.util.loging.Logger#isLoggingEnabled() */
    @Override
    public boolean isLoggingEnabled() {
        return true;
    }

    /** @see com.codenvy.ide.util.loging.Logger#warn(java.lang.Class, java.lang.Object[]) */
    @Override
    public void warn(Class<?> clazz, Object... args) {
        if (LogConfig.getLogLevel().ordinal() <= LogLevel.WARNING.ordinal()) {
            log(clazz, LogLevel.WARNING, args);
        }
    }

    private static native void invokeBrowserLogger(String logFuncName, Object o) /*-{
        if ($wnd.console && $wnd.console[logFuncName]) {
            $wnd.console[logFuncName](o);
        }
        return;
    }-*/;

    private static void log(Class<?> clazz, LogLevel logLevel, Object... args) {
        String prefix =
                new StringBuilder(logLevel.toString()).append(" (").append(clazz.getName()).append("): ").toString();

        for (Object o : args) {
            if (o instanceof String) {
                logToDevMode(prefix + (String)o);
                logToBrowser(logLevel, prefix + (String)o);
            } else if (o instanceof Throwable) {
                Throwable t = (Throwable)o;
                logToDevMode(prefix + "(click for stack)", t);
                logToBrowser(logLevel, prefix + ExceptionUtils.getStackTraceAsString(t));
            } else if (o instanceof JavaScriptObject) {
                logToDevMode(prefix + "(JSO, see browser's console log for details)");
                logToBrowser(logLevel, prefix + "(JSO below)");
                logToBrowser(logLevel, o);
            } else {
                logToDevMode(prefix + (o != null ? o.toString() : "(null)"));
                logToBrowser(logLevel, prefix + (o != null ? o.toString() : "(null)"));
            }
        }
    }

    private static void logToBrowser(LogLevel logLevel, Object o) {
        switch (logLevel) {
            case DEBUG:
                invokeBrowserLogger("debug", o);
                break;
            case INFO:
                invokeBrowserLogger("info", o);
                break;
            case WARNING:
                invokeBrowserLogger("warn", o);
                break;
            case ERROR:
                invokeBrowserLogger("error", o);
                break;
            default:
                invokeBrowserLogger("log", o);
        }
    }

    private static void logToDevMode(String msg) {
        if (!GWT.isScript()) {
            GWT.log(msg);
        }
    }

    private static void logToDevMode(String msg, Throwable t) {
        if (!GWT.isScript()) {
            GWT.log(msg, t);
        }
    }

}
