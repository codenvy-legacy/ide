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
package org.exoplatform.ide.extension.cloudbees.shared;

/**
 * Application info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInfo.java Mar 15, 2012 9:25:11 AM azatsarynnyy $
 */
public interface ApplicationInfo {

    /** @return the id */
    String getId();

    /**
     * @param id
     *         the id to set
     */
    void setId(String id);

    /** @return the title */
    String getTitle();

    /**
     * @param title
     *         the title to set
     */
    void setTitle(String title);

    /** @return the status */
    String getStatus();

    /**
     * @param status
     *         the status to set
     */
    void setStatus(String status);

    /** @return the url */
    String getUrl();

    /**
     * @param url
     *         the url to set
     */
    void setUrl(String url);

    /** @return the instances */
    @Deprecated
    String getInstances();

    /**
     * @param instances
     *         the instances to set
     */
    @Deprecated
    void setInstances(String instances);

    /** @return the securityMode */
    String getSecurityMode();

    /**
     * @param securityMode
     *         the securityMode to set
     */
    void setSecurityMode(String securityMode);

    /** @return the maxMemory */
    String getMaxMemory();

    /**
     * @param maxMemory
     *         the maxMemory to set
     */
    void setMaxMemory(String maxMemory);

    /** @return the idleTimeout */
    String getIdleTimeout();

    /**
     * @param idleTimeout
     *         the idleTimeout to set
     */
    void setIdleTimeout(String idleTimeout);

    /** @return the serverPull */
    String getServerPool();

    /**
     * @param serverPool
     *         the serverPull to set
     */
    void setServerPool(String serverPool);

    /** @return the container */
    String getContainer();

    /**
     * @param container
     *         the container to set
     */
    void setContainer(String container);

    /** @return size of the cluster */
    String getClusterSize();

    /**
     * @param clusterSize
     *         size of the cluster to set
     */
    void setClusterSize(String clusterSize);
}