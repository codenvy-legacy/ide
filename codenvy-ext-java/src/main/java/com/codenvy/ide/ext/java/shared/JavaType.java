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
package com.codenvy.ide.ext.java.shared;

public enum JavaType {
    CLASS, INTERFACE, ANNOTATION, ENUM;

    /**
     * Return JavaType depends on the value of the class attribute. See private
     * constants in class Modifier and Class.
     */
    public static JavaType fromClassAttribute(int attr) {

        if ((0x00002000 & attr) != 0) {
            return ANNOTATION;
        } else if ((0x00000200 & attr) != 0) {
            return INTERFACE;
        } else if ((0x00004000 & attr) != 0) {
            return ENUM;
        } else {
            return CLASS;
        }
    }
}
