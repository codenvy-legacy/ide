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