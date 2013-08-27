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
package org.exoplatform.ide.extension.aws.shared.ec2;

/**
 * EC2 region description.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RegionInfo {
    /**
     * Get region name.
     *
     * @return region name
     */
    String getName();

    /**
     * Set region name.
     *
     * @param name
     *         region name
     */
    void setName(String name);

    /**
     * Get region service endpoint.
     *
     * @return region service endpoint
     */
    String getEndpoint();

    /**
     * Set region service endpoint.
     *
     * @param endpoint
     *         region service endpoint
     */
    void setEndpoint(String endpoint);
}
