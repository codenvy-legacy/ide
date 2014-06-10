/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
