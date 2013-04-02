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

import com.sun.jdi.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JdiValueImpl implements JdiValue {
    private final Value         value;
    private       JdiVariable[] variables;

    public JdiValueImpl(Value value) {
        if (value == null) {
            throw new IllegalArgumentException("Underlying value may not be null. ");
        }
        this.value = value;
    }

    @Override
    public String getAsString() {
        return value.toString();
    }

    @Override
    public JdiVariable[] getVariables() {
        if (variables == null) {
            if (isPrimitive()) {
                variables = new JdiVariable[0];
            } else {
                if (isArray()) {
                    ArrayReference array = (ArrayReference)value;
                    int length = array.length();
                    variables = new JdiVariable[length];
                    for (int i = 0; i < length; i++) {
                        variables[i] = new JdiArrayElementImpl(i, array.getValue(i));
                    }
                } else {
                    ObjectReference object = (ObjectReference)value;
                    ReferenceType type = object.referenceType();
                    List<Field> fields = type.allFields();
                    variables = new JdiVariable[fields.size()];
                    int i = 0;
                    for (Field f : fields) {
                        variables[i++] = new JdiFieldImpl(f, object);
                    }
                    // See JdiFieldImpl#compareTo(JdiFieldImpl).
                    Arrays.sort(variables);
                }
            }
        }
        return variables;
    }

    @Override
    public JdiVariable getVariableByName(String name) throws DebuggerException {
        if (name == null) {
            throw new IllegalArgumentException("Variable name may not be null. ");
        }
        for (JdiVariable variable : getVariables()) {
            if (name.equals(variable.getName())) {
                return variable;
            }
        }
        return null;
    }

    private boolean isArray() {
        return value instanceof ArrayReference;
    }

    private boolean isPrimitive() {
        return value instanceof PrimitiveValue;
    }
}
