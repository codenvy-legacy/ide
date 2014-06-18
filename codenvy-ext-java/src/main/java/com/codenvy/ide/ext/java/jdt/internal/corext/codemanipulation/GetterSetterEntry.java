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
package com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation;

import com.codenvy.ide.ext.java.jdt.core.dom.IVariableBinding;

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