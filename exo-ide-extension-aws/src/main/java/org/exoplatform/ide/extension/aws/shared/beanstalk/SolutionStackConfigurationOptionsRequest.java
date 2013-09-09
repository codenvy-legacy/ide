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
 * Get options possible for configuration for specified amazon solution stack.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface SolutionStackConfigurationOptionsRequest {
    /**
     * Get name of amazon solution stack, e.g. '64bit Amazon Linux running Tomcat 6'.
     *
     * @return name of amazon solution stack
     */
    String getSolutionStackName();

    /**
     * Set name of amazon solution stack.
     *
     * @param solutionStackName
     *         name of amazon solution stack
     */
    void setSolutionStackName(String solutionStackName);
}
