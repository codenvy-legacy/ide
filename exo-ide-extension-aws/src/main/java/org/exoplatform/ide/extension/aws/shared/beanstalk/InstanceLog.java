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
 * Information about instance log in specified environment
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface InstanceLog {
    /**
     * Get ID of the instance
     *
     * @return ID of the instance
     */
    String getInstanceId();

    /**
     * Set ID of the instance
     *
     * @param instanceId
     *         ID of the instance
     */
    void setInstanceId(String instanceId);

    /**
     * Get url of the application logs
     *
     * @return url of the logs
     */
    String getLogUrl();

    /**
     * Set url of the application logs
     *
     * @param logUrl
     *         url of the logs
     */
    void setLogUrl(String logUrl);
}
