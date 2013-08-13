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
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 7, 2012 11:49:20 AM anya $
 */
public interface CreateApplicationRequest {
    /** @return the server */
    String getServer();

    /** @return the name */
    String getName();

    /** @return the type */
    String getType();

    /** @return the url */
    String getUrl();

    /** @return the instances */
    int getInstances();

    /** @return the memory */
    int getMemory();

    /** @return the nostart */
    boolean isNostart();

    /** @return the vfsId */
    String getVfsid();

    /** @return the projectId */
    String getProjectid();

    /** @return the war */
    String getWar();

    /** @return CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc. */
    String getPaasprovider();
}