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
package com.codenvy.ide.extension.cloudfoundry.shared;

/**
 * Cloud Foundry application resources info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: CloudFoundryApplicationResources.java Mar 16, 2012 2:14:15 PM azatsarynnyy $
 */
public interface CloudFoundryApplicationResources {
    /**
     * Get amount of memory available for application (in MB).
     *
     * @return amount of memory.
     */
    int getMemory();

    /**
     * Get amount disk space available for application (in MB).
     *
     * @return amount of disk space.
     */
    int getDisk();
}