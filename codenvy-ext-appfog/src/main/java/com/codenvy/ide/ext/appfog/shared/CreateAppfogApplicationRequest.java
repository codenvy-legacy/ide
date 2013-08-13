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
package com.codenvy.ide.ext.appfog.shared;

/** @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a> */
public interface CreateAppfogApplicationRequest {
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

    /** @return the infra */
    String getInfra();
}