/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.logger;


import com.codenvy.api.analytics.logger.AnalyticsEventLogger;

import java.util.Map;

/**
 * @author Anatoliy Bazko
 */
public interface AnalyticsEventLoggerExt extends AnalyticsEventLogger {

    /**
     * Logs arbitrary event.
     *
     * @param event
     *         the event name
     * @param additionalParams
     *         any additional parameters to log, not more than {@link #MAX_PARAMS_NUMBER}, every parameter name and its
     *         value are limited to {@link #MAX_PARAM_NAME_LENGTH} and {@link #MAX_PARAM_VALUE_LENGTH} characters
     *         correspondingly
     */
    public void logEvent(String event, Map<String, String> additionalParams);
}
