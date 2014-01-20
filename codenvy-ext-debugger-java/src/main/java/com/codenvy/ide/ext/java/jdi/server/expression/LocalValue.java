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
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

/** @author andrew00x */
public class LocalValue implements ExpressionValue {
    private final ThreadReference thread;
    private final LocalVariable   variable;
    private       Value           value;

    public LocalValue(ThreadReference thread, LocalVariable variable) {
        this.thread = thread;
        this.variable = variable;
    }

    @Override
    public Value getValue() {
        if (value == null) {
            try {
                value = thread.frame(0).getValue(variable);
            } catch (IncompatibleThreadStateException | IllegalArgumentException | InvalidStackFrameException e) {
                throw new ExpressionException(e.getMessage(), e);
            }
        }
        return value;
    }

    @Override
    public void setValue(Value value) {
        try {
            thread.frame(0).setValue(variable, value);
        } catch (IncompatibleThreadStateException | InvalidTypeException | ClassNotLoadedException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        this.value = value;
    }
}
