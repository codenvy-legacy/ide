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

import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;

/** @author andrew00x */
public class JdiLocalVariableImpl implements JdiLocalVariable {
    private final LocalVariable variable;
    private final StackFrame    stackFrame;

    public JdiLocalVariableImpl(StackFrame stackFrame, LocalVariable variable) {
        this.stackFrame = stackFrame;
        this.variable = variable;
    }

    @Override
    public String getName() {
        return variable.name();
    }

    @Override
    public boolean isArray() throws DebuggerException {
        return JdiType.isArray(variable.signature());
    }

    @Override
    public boolean isPrimitive() throws DebuggerException {
        return JdiType.isPrimitive(variable.signature());
    }

    @Override
    public JdiValue getValue() {
        Value value = stackFrame.getValue(variable);
        if (value == null) {
            return new JdiNullValue();
        }
        return new JdiValueImpl(value);
    }

    @Override
    public String getTypeName() {
        return variable.typeName();
    }
}
