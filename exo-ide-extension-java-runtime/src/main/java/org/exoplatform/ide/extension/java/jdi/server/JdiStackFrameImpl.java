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
public class JdiStackFrameImpl implements JdiStackFrame {
    private final StackFrame         stackFrame;
    private       JdiField[]         fields;
    private       JdiLocalVariable[] localVariables;

    public JdiStackFrameImpl(StackFrame stackFrame) {
        this.stackFrame = stackFrame;
    }

    @Override
    public JdiField[] getFields() throws DebuggerException {
        if (fields == null) {
            try {
                ObjectReference object = stackFrame.thisObject();
                if (object == null) {
                    ReferenceType type = stackFrame.location().declaringType();
                    List<Field> fs = stackFrame.location().declaringType().allFields();
                    fields = new JdiField[fs.size()];
                    int i = 0;
                    for (Field f : fs) {
                        fields[i++] = new JdiFieldImpl(f, type);
                    }
                } else {
                    List<Field> fs = object.referenceType().allFields();
                    fields = new JdiField[fs.size()];
                    int i = 0;
                    for (Field f : fs) {
                        fields[i++] = new JdiFieldImpl(f, object);
                    }
                }

                Arrays.sort(fields);
            } catch (InvalidStackFrameException e) {
                throw new DebuggerException(e.getMessage(), e);
            }
        }
        return fields;
    }

    @Override
    public JdiField getFieldByName(String name) throws DebuggerException {
        if (name == null) {
            throw new IllegalArgumentException("Field name may not be null. ");
        }
        for (JdiField f : getFields()) {
            if (name.equals(f.getName())) {
                return f;
            }
        }
        return null;
    }

    @Override
    public JdiLocalVariable[] getLocalVariables() throws DebuggerException {
        if (localVariables == null) {
            try {
                List<LocalVariable> targetVariables = stackFrame.visibleVariables();
                localVariables = new JdiLocalVariable[targetVariables.size()];
                int i = 0;
                for (LocalVariable var : targetVariables) {
                    localVariables[i++] = new JdiLocalVariableImpl(stackFrame, var);
                }
            } catch (AbsentInformationException e) {
                throw new DebuggerException(e.getMessage(), e);
            } catch (InvalidStackFrameException e) {
                throw new DebuggerException(e.getMessage(), e);
            } catch (NativeMethodException e) {
                throw new DebuggerException(e.getMessage(), e);
            }
        }
        return localVariables;
    }

    @Override
    public JdiLocalVariable getLocalVariableByName(String name) throws DebuggerException {
        if (name == null) {
            throw new IllegalArgumentException("Field name may not be null. ");
        }
        for (JdiLocalVariable var : getLocalVariables()) {
            if (name.equals(var.getName())) {
                return var;
            }
        }
        return null;
    }
}
