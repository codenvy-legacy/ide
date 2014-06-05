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
package com.codenvy.ide.ext.java.client.newresource;

/**
 * Types of Java files for {@link NewJavaResourcePresenter}.
 *
 * @author Artem Zatsarynnyy
 */
enum ResourceTypes {
    CLASS("Class"),
    INTERFACE("Interface"),
    ENUM("Enum");

    private final String value;

    private ResourceTypes(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
