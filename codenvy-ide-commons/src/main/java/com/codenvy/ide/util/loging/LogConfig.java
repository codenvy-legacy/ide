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

import com.google.gwt.user.client.Window;

/**
 * Deferred bound class to determing statically whether or not logging is
 * enabled.
 * <p/>
 * This is package protected all the way and only used internally by
 * {@link Log}.
 */
class LogConfig {
    public static enum LogLevel {
        DEBUG, INFO, WARNING, ERROR
    }

    private static final String LOG_LEVEL_PARAM = "logLevel";

    private static final LogConfig INSTANCE = new LogConfig();

    static LogLevel getLogLevel() {
        return INSTANCE.getLogLevelImpl();
    }

    static void setLogLevel(LogLevel level) {
        INSTANCE.setLogLevelImpl(level);
    }

    private LogLevel currentLevel = null;

    private void ensureLogLevel() {
        if (currentLevel == null) {
            // First inspect the URL to see if it has one set.
            setLogLevel(maybeGetLevelFromUrl());

            // If it is still not set, make the default be INFO.
            setLogLevel((currentLevel == null) ? LogLevel.INFO : currentLevel);
        }
    }

    private LogLevel getLogLevelImpl() {
        ensureLogLevel();
        return currentLevel;
    }

    private LogLevel maybeGetLevelFromUrl() {
        String levelStr = Window.Location.getParameter(LOG_LEVEL_PARAM);

        // The common case.
        if (levelStr == null) {
            return null;
        }

        levelStr = levelStr.toUpperCase();

        try {
            // Extract the correct Enum value;
            return LogLevel.valueOf(levelStr);
        } catch (IllegalArgumentException e) {
            // We had a String but it was malformed.
            return null;
        }
    }

    private void setLogLevelImpl(LogLevel level) {
        currentLevel = level;
    }
}
