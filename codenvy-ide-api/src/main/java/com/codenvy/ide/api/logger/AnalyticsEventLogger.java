/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.api.logger;

import java.util.Map;

/**
 * @author Anatoliy Bazko
 */
public interface AnalyticsEventLogger {

    int MAX_PARAMS_NUMBER      = 3;
    int MAX_PARAM_NAME_LENGTH  = 10;
    int MAX_PARAM_VALUE_LENGTH = 50;

    /**
     * Logs a client-side analytics event.
     *
     * @param extensionClass
     *         it used to tie event with the extension,{@link com.codenvy.ide.api.extension.Extension#title()} will
     *         be logged as a extension marker
     * @param event
     *         the event, is limited to {@link #MAX_PARAM_VALUE_LENGTH} characters
     * @param additionalParams
     *         any additional parameters to log, not more than {@link #MAX_PARAMS_NUMBER}, every parameter name and its
     *         value are limited to {@link #MAX_PARAM_NAME_LENGTH} and {@link #MAX_PARAM_VALUE_LENGTH} characters
     *         correspondingly
     */
    void log(Class<?> extensionClass, String event, Map<String, String> additionalParams);


    /**
     * Logs a client-side event without reference to an extension.
     *
     * @see #log(Class, String, java.util.Map)
     */
    void log(String event);
}
