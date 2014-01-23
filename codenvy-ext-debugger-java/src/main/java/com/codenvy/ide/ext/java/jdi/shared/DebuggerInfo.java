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

/**
 * Summary of debugger information.
 *
 * @author andrew00x
 */
@DTO
public interface DebuggerInfo {
    String getHost();

    void setHost(String host);

    DebuggerInfo withHost(String host);

    int getPort();

    void setPort(int port);

    DebuggerInfo withPort(int port);

    String getId();

    void setId(String id);

    DebuggerInfo withId(String id);

    String getVmName();

    void setVmName(String vmName);

    DebuggerInfo withVmName(String vmName);

    String getVmVersion();

    void setVmVersion(String vmVersion);

    DebuggerInfo withVmVersion(String vmVersion);
}