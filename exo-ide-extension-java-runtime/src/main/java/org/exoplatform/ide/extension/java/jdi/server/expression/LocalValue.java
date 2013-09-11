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
package org.exoplatform.ide.extension.java.jdi.server.expression;

import com.sun.jdi.*;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
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
            } catch (IncompatibleThreadStateException e) {
                throw new ExpressionException(e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                throw new ExpressionException(e.getMessage(), e);
            } catch (InvalidStackFrameException e) {
                throw new ExpressionException(e.getMessage(), e);
            }
        }
        return value;
    }

    @Override
    public void setValue(Value value) {
        try {
            thread.frame(0).setValue(variable, value);
        } catch (IncompatibleThreadStateException e) {
            throw new ExpressionException(e.getMessage(), e);
        } catch (InvalidTypeException e) {
            throw new ExpressionException(e.getMessage(), e);
        } catch (ClassNotLoadedException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        this.value = value;
    }
}
