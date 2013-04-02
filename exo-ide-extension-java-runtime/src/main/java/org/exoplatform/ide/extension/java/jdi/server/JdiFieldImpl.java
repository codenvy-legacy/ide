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
package org.exoplatform.ide.extension.java.jdi.server;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
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
