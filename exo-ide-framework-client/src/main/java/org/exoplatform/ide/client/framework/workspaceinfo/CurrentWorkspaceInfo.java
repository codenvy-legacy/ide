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
package org.exoplatform.ide.client.framework.workspaceinfo;

/**
 * Interface describe information about current workspace.
 *
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: CurrentWorkspaceInfo.java 
 */
public interface CurrentWorkspaceInfo {
    
    /**
     * Returns the workspace's URL.
     *
     * @return workspace's URL
     */
    public String getUrl();

    /**
     * Change the workspace's URL.
     *
     * @param name
     *         workspace's URL
     */
    public void setUrl(String url); 

    /**
     * Returns the workspace's temporary.
     *
     * @return workspace's temporary
     */
    public boolean isTemporary();

    /**
     * Change the workspace's temporary.
     *
     * @param name
     *         workspace's temporary
     */
    public void setTemporary(boolean temporary);

    /**
     * Returns the workspace's name.
     *
     * @return workspace's name
     */
    public String getName();

    /**
     * Change the workspace's name.
     *
     * @param name
     *         workspace's name
     */
    public void setName(String name);

    /**
     * Returns the workspace's id.
     *
     * @return workspace's id
     */
    public String getId();

    /**
     * Change the workspace's id.
     *
     * @param id
     *         workspace's id
     */
    public void setId(String id);
}
