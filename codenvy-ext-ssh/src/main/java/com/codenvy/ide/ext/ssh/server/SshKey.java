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
package com.codenvy.ide.ext.ssh.server;

/**
 * SSH key.
 *
 * @author andrew00x
 */
public class SshKey {
    private final String identifier;
    private final byte[] bytes;

    public SshKey(String identifier, byte[] bytes) {
        this.identifier = identifier;
        this.bytes = bytes;
    }

    /**
     * Identifier of SSH key, e.g. path to file where key stored, etc.
     *
     * @return identifier of key file
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get SSH key as byte array.
     *
     * @return SSH key as byte array
     */
    public byte[] getBytes() {
        byte[] copy = new byte[bytes.length];
        System.arraycopy(bytes, 0, copy, 0, copy.length);
        return copy;
    }
}
