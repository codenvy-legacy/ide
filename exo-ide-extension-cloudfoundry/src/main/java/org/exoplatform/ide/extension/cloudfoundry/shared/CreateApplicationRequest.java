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
package org.exoplatform.ide.extension.cloudfoundry.shared;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 7, 2012 11:49:20 AM anya $
 */
public interface CreateApplicationRequest {

    /** @return the server */
    public abstract String getServer();

    /**
     * @param server
     *         the server to set
     */
    public abstract void setServer(String server);

    /** @return the name */
    public abstract String getName();

    /**
     * @param name
     *         the name to set
     */
    public abstract void setName(String name);

    /** @return the type */
    public abstract String getType();

    /**
     * @param type
     *         the type to set
     */
    public abstract void setType(String type);

    /** @return the url */
    public abstract String getUrl();

    /**
     * @param url
     *         the url to set
     */
    public abstract void setUrl(String url);

    /** @return the instances */
    public abstract int getInstances();

    /**
     * @param instances
     *         the instances to set
     */
    public abstract void setInstances(int instances);

    /** @return the memory */
    public abstract int getMemory();

    /**
     * @param memory
     *         the memory to set
     */
    public abstract void setMemory(int memory);

    /** @return the nostart */
    public abstract boolean isNostart();

    /**
     * @param nostart
     *         the nostart to set
     */
    public abstract void setNostart(boolean nostart);

    /** @return the vfsId */
    public abstract String getVfsid();

    /**
     * @param vfsId
     *         the vfsId to set
     */
    public abstract void setVfsid(String vfsId);

    /** @return the projectId */
    public abstract String getProjectid();

    /**
     * @param projectId
     *         the projectId to set
     */
    public abstract void setProjectid(String projectId);

    /** @return the war */
    public abstract String getWar();

    /**
     * @param war
     *         the war to set
     */
    public abstract void setWar(String war);

    /** @return CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc. */
    public abstract String getPaasprovider();

    /**
     * @param paasProvider
     *         CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     */
    public abstract void setPaasprovider(String paasProvider);

}