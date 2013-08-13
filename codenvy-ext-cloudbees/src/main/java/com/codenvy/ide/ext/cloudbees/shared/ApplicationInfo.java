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
package com.codenvy.ide.ext.cloudbees.shared;

/**
 * Application info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInfo.java Mar 15, 2012 9:25:11 AM azatsarynnyy $
 */
public interface ApplicationInfo {
    /** @return the id */
    String getId();

    /** @return the title */
    String getTitle();

    /** @return the status */
    String getStatus();

    /** @return the url */
    String getUrl();

    /** @return the instances */
    @Deprecated
    String getInstances();

    /** @return the securityMode */
    String getSecurityMode();

    /** @return the maxMemory */
    String getMaxMemory();

    /** @return the idleTimeout */
    String getIdleTimeout();

    /** @return the serverPull */
    String getServerPool();

    /** @return the container */
    String getContainer();

    /** @return size of the cluster */
    String getClusterSize();
}