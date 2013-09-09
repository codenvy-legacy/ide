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
