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
package org.exoplatform.ide.extension.aws.shared.beanstalk;

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
     * Set name of application.
     *
     * @param applicationName
     *         name of application
     */
    void setApplicationName(String applicationName);

    /**
     * Get application version label. If <code>null</code> get events for all versions of application
     *
     * @return application version label
     */
    String getVersionLabel();

    /**
     * Set application version label. If <code>null</code> get events for all versions of application
     *
     * @param versionLabel
     *         application version label
     */
    void setVersionLabel(String versionLabel);

    /**
     * Get name of configuration template. If <code>null</code> get events for all configuration template of
     * application.
     *
     * @return name of configuration template
     */
    String getTemplateName();

    /**
     * Set name of configuration template. If <code>null</code> get events for all configuration template of
     * application.
     *
     * @param templateName
     *         name of configuration template
     */
    void setTemplateName(String templateName);

    /**
     * Get id of environment. If <code>null</code> get events for all application environments.
     *
     * @return id of environment
     */
    String getEnvironmentId();

    /**
     * Set id of environment. If <code>null</code> get events for all application environments.
     *
     * @param environmentId
     *         id of environment
     */
    void setEnvironmentId(String environmentId);

    /**
     * Get events severity. Only events with specified severity or higher will be returned.
     *
     * @return events severity
     */
    EventsSeverity getSeverity();

    /**
     * Set events severity. Only events with specified severity or higher will be returned.
     *
     * @param severity
     *         events severity
     */
    void setSeverity(EventsSeverity severity);

    /**
     * Get token to get the next batch of results.
     *
     * @return token to get the next batch of results
     * @see EventsList#getNextToken()
     */
    String getNextToken();

    /**
     * Set token to get the next batch of results.
     *
     * @param nextToken
     *         token to get the next batch of results
     * @see EventsList#getNextToken()
     */
    void setNextToken(String nextToken);

    long getStartTime();

    void setStartTime(long startTime);

    long getEndTime();

    void setEndTime(long endTime);

    /**
     * Get maximum number of items in result. Max value: 1000.
     *
     * @return maximum number of items in result
     */
    int getMaxRecords();

    /**
     * Set maximum number of items in result. Max value: 1000.
     *
     * @param maxRecords
     *         maximum number of items in result
     */
    void setMaxRecords(int maxRecords);
}
