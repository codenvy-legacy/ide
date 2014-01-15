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
package com.codenvy.ide.ext.java.jdi.server;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;

/** @author andrew00x */
public class JdiFieldImpl implements JdiField, Comparable<JdiFieldImpl> {
    private final Field           field;
    private final ReferenceType   type;
    private final ObjectReference object;

    public JdiFieldImpl(Field field, ObjectReference object) {
        this.field = field;
        this.object = object;
        this.type = null;
    }

    public JdiFieldImpl(Field field, ReferenceType type) {
        this.field = field;
        this.type = type;
        this.object = null;
    }

    @Override
    public String getName() {
        return field.name();
    }

    @Override
    public boolean isStatic() {
        return field.isStatic();
    }

    @Override
    public boolean isTransient() {
        return field.isTransient();
    }

    @Override
    public boolean isVolatile() {
        return field.isVolatile();
    }

    @Override
    public boolean isFinal() {
        return field.isFinal();
    }

    @Override
    public boolean isArray() throws DebuggerException {
        return JdiType.isArray(field.signature());
    }

    @Override
    public boolean isPrimitive() throws DebuggerException {
        return JdiType.isPrimitive(field.signature());
    }

    @Override
    public JdiValue getValue() {
        Value value = object == null ? type.getValue(field) : object.getValue(field);
        if (value == null) {
            return new JdiNullValue();
        }
        return new JdiValueImpl(value);
    }

    @Override
    public String getTypeName() {
        return field.typeName();
    }

    @Override
    public int compareTo(JdiFieldImpl o) {
        final boolean thisStatic = isStatic();
        final boolean thatStatic = o.isStatic();
        if (thisStatic && !thatStatic) {
            return -1;
        }
        if (!thisStatic && thatStatic) {
            return 1;
        }
        final String thisName = getName();
        final String thatName = o.getName();
        return thisName.compareTo(thatName);
    }
}
