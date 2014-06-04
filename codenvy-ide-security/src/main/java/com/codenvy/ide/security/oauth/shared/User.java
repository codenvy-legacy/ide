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
package com.codenvy.ide.security.oauth.shared;

/**
 * Represents an User with unique identifier. Have such interface to be able use GWT AutoBean feature. Any interface
 * that represents an User should extend this interface.
 */
public interface User {
    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getEmail();

    void setEmail(String email);
}
