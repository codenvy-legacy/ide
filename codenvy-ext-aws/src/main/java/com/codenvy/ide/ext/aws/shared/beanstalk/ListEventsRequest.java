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
package com.codenvy.ide.ext.aws.shared.beanstalk;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ListEventsRequest {

    /**
     * Get name of application.
     *
     * @return name of application
     */
    String getApplicationName();

    /**
     * Get application version label. If <code>null</code> get events for all versions of application
     *
     * @return application version label
     */
    String getVersionLabel();

    /**
     * Get name of configuration template. If <code>null</code> get events for all configuration template of
     * application.
     *
     * @return name of configuration template
     */
    String getTemplateName();

    /**
     * Get id of environment. If <code>null</code> get events for all application environments.
     *
     * @return id of environment
     */
    String getEnvironmentId();

    /**
     * Get events severity. Only events with specified severity or higher will be returned.
     *
     * @return events severity
     */
    EventsSeverity getSeverity();

    /**
     * Get token to get the next batch of results.
     *
     * @return token to get the next batch of results
     * @see EventsList#getNextToken()
     */
    String getNextToken();

    /**
     * The start date from which we should take events list.
     *
     * @return The start date for getting events.
     */
    double getStartTime();

    /**
     * The end date to which we should take events list.
     *
     * @return The end date for getting events.
     */
    double getEndTime();

    /**
     * Get maximum number of items in result. Max value: 1000.
     *
     * @return maximum number of items in result
     */
    int getMaxRecords();
}
