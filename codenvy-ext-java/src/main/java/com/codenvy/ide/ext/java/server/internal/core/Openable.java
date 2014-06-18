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
package com.codenvy.ide.ext.java.server.internal.core;

import org.eclipse.jdt.internal.core.JavaElement;

/**
 * @author Evgen Vidolob
 */
public abstract class Openable extends org.eclipse.jdt.internal.core.Openable {
    protected Openable(JavaElement parent) {
        super(parent);
    }

    @Override
    public String toString() {
        return String.valueOf(hashCode());
    }
}
