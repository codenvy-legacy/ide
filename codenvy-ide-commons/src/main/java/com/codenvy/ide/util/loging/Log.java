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

import com.codenvy.ide.util.loging.LogConfig.LogLevel;
import com.google.gwt.core.client.GWT;


/**
 * Simple Logging class that logs to the browser's console and to the DevMode
 * console (if you are in DevMode).
 * <p/>
 * So long as generating the parameters to pass to the logging methods is free
 * of side effects, all Logging code should compile out of your application if
 * logging is disabled.
 */
public class Log {

    private static final Logger delegate;

    static {
        LogConfig.setLogLevel(LogLevel.INFO);
        delegate = GWT.isClient() ? new DevModeLogger() : new DummyLogger();
    }

    public static void debug(Class<?> clazz, Object... args) {
        delegate.debug(clazz, args);
    }

    public static void error(Class<?> clazz, Object... args) {
        delegate.error(clazz, args);
    }

    public static void info(Class<?> clazz, Object... args) {
        delegate.info(clazz, args);
    }

    public static void warn(Class<?> clazz, Object... args) {
        delegate.warn(clazz, args);
    }

    public static boolean isLoggingEnabled() {
        return delegate.isLoggingEnabled();
    }

}
