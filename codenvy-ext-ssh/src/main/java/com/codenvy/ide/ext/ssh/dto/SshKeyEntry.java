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
package com.codenvy.ide.ext.ssh.dto;

import com.codenvy.dto.shared.DTO;

/**
 * SSH key.
 *
 * @author andrew00x
 */
@DTO
public interface SshKeyEntry {
        /**
     * Identifier of SSH key, e.g. path to file where key stored, etc.
     *
     * @return identifier of key file
     */
    String getIdentifier();

    void setIdentifier(String identifier);

    SshKeyEntry withIdentifier(String identifier);

    /**
     * Get SSH key as byte array.
     *
     * @return SSH key as byte array
     */
    String getBytes();

    void setBytes(String bytes);

    SshKeyEntry withBytes(String bytes);
}
