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
public interface UpdateConfigurationTemplateRequest {
    /**
     * Get name of application.
     *
     * @return application name
     */
    String getApplicationName();

    /**
     * Set name of application.
     *
     * @param name
     *         application name
     * @see #getApplicationName()
     */
    void setApplicationName(String name);

    /**
     * Get name of template to delete.
     *
     * @return name of template to delete
     */
    String getTemplateName();

    /**
     * Set name of template to delete.
     *
     * @param templateName
     *         name of template to delete
     */
    void setTemplateName(String templateName);

    /**
     * Get new description of configuration template. Length: 0 - 200 characters.
     *
     * @return configuration template description
     */
    String getDescription();

    /**
     * Set new configuration template description. Length: 0 - 200 characters.
     *
     * @param description
     *         configuration template description
     * @see #getDescription()
     */
    void setDescription(String description);

}
