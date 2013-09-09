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
package org.exoplatform.ide.client.framework.discovery;

import java.util.List;

/**
 * Interface describe list of REST-services.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: RestServicesList.java Mar 26, 2012 11:22:53 AM azatsarynnyy $
 */
public interface RestServicesList {
    /**
     * Returns the list of REST-services.
     *
     * @return the root resources
     */
    public List<IRestService> getRootResources();

    /**
     * Set the list of REST-services.
     *
     * @param rootResources
     *         the root resources
     */
    public void setRootResources(List<IRestService> rootResources);
}
