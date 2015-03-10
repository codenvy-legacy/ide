/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.ssh.dto;

import org.eclipse.che.dto.shared.DTO;


/** @author andrew00x */
@DTO
public interface PublicKey {
    String getHost();

    void setHost(String host);

    PublicKey withHost(String host);

    String getKey();

    void setKey(String key);

    PublicKey withKey(String key);
}