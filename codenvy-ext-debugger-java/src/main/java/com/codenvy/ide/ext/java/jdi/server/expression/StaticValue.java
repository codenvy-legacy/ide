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
package com.codenvy.ide.ext.java.jdi.server.expression;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VMCannotBeModifiedException;
import com.sun.jdi.Value;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class StaticValue implements ExpressionValue {
    private final ReferenceType klass;
    private final Field         field;
    private       Value         value;

    public StaticValue(ReferenceType klass, Field field) {
        this.klass = klass;
        this.field = field;
    }

    @Override
    public Value getValue() {
        if (value == null) {
            try {
                value = klass.getValue(field);
            } catch (IllegalArgumentException e) {
                throw new ExpressionException(e.getMessage(), e);
            }
        }
        return value;
    }

    @Override
    public void setValue(Value value) {
        if (!(klass instanceof ClassType)) {
            throw new ExpressionException("Unable update field " + field.name());
        }
        try {
            ((ClassType)klass).setValue(field, value);
        } catch (InvalidTypeException e) {
            throw new ExpressionException(e.getMessage(), e);
        } catch (ClassNotLoadedException e) {
            throw new ExpressionException(e.getMessage(), e);
        } catch (VMCannotBeModifiedException e) {
            throw new ExpressionException(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        this.value = value;
    }
}
