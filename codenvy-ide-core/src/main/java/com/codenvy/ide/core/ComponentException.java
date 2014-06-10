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
package com.codenvy.ide.core;

/**
 * Encapsulates Component Error
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ComponentException extends Exception {
    private static final long serialVersionUID = 1L;

    private Component component;

    /** @return the instance of failed component */
    public Component getComponent() {
        return component;
    }

    /**
     * Construct Component Exception instance with message and instance of failed component
     *
     * @param message
     * @param component
     */
    public ComponentException(String message, Component component) {
        super(message);
        this.component = component;

    }
}
