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
