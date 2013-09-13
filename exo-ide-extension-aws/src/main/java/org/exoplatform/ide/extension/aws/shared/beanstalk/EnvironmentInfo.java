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
public interface EnvironmentInfo {
    String getName();

    void setName(String name);

    String getId();

    void setId(String id);

    String getApplicationName();

    void setApplicationName(String applicationName);

    String getVersionLabel();

    void setVersionLabel(String versionLabel);

    String getSolutionStackName();

    void setSolutionStackName(String solutionStackName);

    String getTemplateName();

    void setTemplateName(String templateName);

    String getDescription();

    void setDescription(String description);

    String getEndpointUrl();

    void setEndpointUrl(String endpointURL);

    String getCname();

    void setCname(String cNAME);

    long getCreated();

    void setCreated(long created);

    long getUpdated();

    void setUpdated(long updated);

    EnvironmentStatus getStatus();

    void setStatus(EnvironmentStatus status);

    EnvironmentHealth getHealth();

    void setHealth(EnvironmentHealth health);
}
