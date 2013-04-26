/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.java.client.internal.corext.codemanipulation;

import com.codenvy.ide.java.client.core.dom.IVariableBinding;

public class GetterSetterEntry {
    private final IVariableBinding field;

    private final boolean isGetter;

    private final boolean isFinal;

    GetterSetterEntry(IVariableBinding field, boolean isGetterEntry, boolean isFinal) {
        this.field = field;
        this.isGetter = isGetterEntry;
        this.isFinal = isFinal;
    }

    /** @return the field */
    public IVariableBinding getField() {
        return field;
    }

    /** @return the isGetter */
    public boolean isGetter() {
        return isGetter;
    }

    /** @return the isFinal */
    public boolean isFinal() {
        return isFinal;
    }

}