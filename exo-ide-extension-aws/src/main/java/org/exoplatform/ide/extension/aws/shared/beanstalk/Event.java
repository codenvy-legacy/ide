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
 * Description of application event.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface Event {
    long getEventDate();

    void setEventDate(long eventDate);

    String getMessage();

    void setMessage(String message);

    String getApplicationName();

    void setApplicationName(String applicationName);

    String getVersionLabel();

    void setVersionLabel(String versionLabel);

    String getTemplateName();

    void setTemplateName(String templateName);

    String getEnvironmentName();

    void setEnvironmentName(String environmentName);

    EventsSeverity getSeverity();

    void setSeverity(EventsSeverity severity);
}
