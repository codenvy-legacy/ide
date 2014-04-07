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

    /**
     * Logs a client-side analytics event.
     *
     * @param action
     * @param extensionClass
     * @param additionalParams
     *         (params limitation) // TODO
     */
    void log(String action, Class<?> extensionClass, Map<String, String> additionalParams);


    /**
     * Logs a client-side event without reference to an extension.
     *
     * @see #log(String, Class, java.util.Map)
     */
    void log(String action);
}
