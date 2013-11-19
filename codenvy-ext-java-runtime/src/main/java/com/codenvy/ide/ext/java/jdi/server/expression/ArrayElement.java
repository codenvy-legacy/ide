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

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.VMCannotBeModifiedException;
import com.sun.jdi.Value;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ArrayElement implements ExpressionValue {
    private final ArrayReference array;
    private final int            indx;
    private       Value          value;

    public ArrayElement(ArrayReference array, int indx) {
        this.array = array;
        this.indx = indx;
    }

    @Override
    public Value getValue() {
        if (value == null) {
            try {
                value = array.getValue(indx);
            } catch (IndexOutOfBoundsException e) {
                throw new ExpressionException(e.getMessage(), e);
            }
        }
        return value;
    }

    @Override
    public void setValue(Value value) {
        try {
            array.setValue(indx, value);
        } catch (InvalidTypeException e) {
            throw new ExpressionException(e.getMessage(), e);
        } catch (ClassNotLoadedException e) {
            throw new ExpressionException(e.getMessage(), e);
        } catch (VMCannotBeModifiedException e) {
            throw new ExpressionException(e.getMessage(), e);
        } catch (IndexOutOfBoundsException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        this.value = value;
    }
}
