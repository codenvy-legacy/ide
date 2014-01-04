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
package com.codenvy.ide.ext.java.jdi.shared;

import com.codenvy.dto.shared.DTO;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
@DTO
public interface ApplicationInstance {
    String getName();

    String getHost();

    int getPort();

    String getStopURL();

    /**
     * Lifetime of application instance in minutes. After this time instance may be stopped.
     * Method may return -1 if lifetime of instance is unknown.
     *
     * @return application instance lifetime in minutes
     */
    int getLifetime();

    // when application started under debug.

    String getDebugHost();

    int getDebugPort();

    void setDebugHost(String debugHost);

    void setDebugPort(int debugPort);

    void setHost(String host);

    void setLifetime(int lifetime);

    void setName(String name);

    void setPort(int port);

    void setStopURL(String stopURL);
}