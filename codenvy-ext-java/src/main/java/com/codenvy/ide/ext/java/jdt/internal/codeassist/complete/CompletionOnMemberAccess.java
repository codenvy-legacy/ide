/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.codeassist.complete;

/*
 * Completion node build by the parser in any case it was intending to
 * reduce an access to a member (field reference or message send)
 * containing the completion identifier.
 * e.g.
 *
 *   class X {
 *    void foo() {
 *      bar().fred[cursor]
 *    }
 *  }
 *
 *   ---> class X {
 *         void foo() {
 *           <CompleteOnMemberAccess:bar().fred>
 *         }
 *       }
 *
 * The source range of the completion node denotes the source range
 * which should be replaced by the completion.
 */

import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.Expression;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.FieldReference;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.MessageSend;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.ThisReference;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.BlockScope;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ProblemMethodBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ProblemReasons;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeBinding;

public class CompletionOnMemberAccess extends FieldReference {

    public boolean isInsideAnnotation;

    public CompletionOnMemberAccess(char[] source, long pos, boolean isInsideAnnotation) {

        super(source, pos);
        this.isInsideAnnotation = isInsideAnnotation;
    }

    public StringBuffer printExpression(int indent, StringBuffer output) {

        output.append("<CompleteOnMemberAccess:"); //$NON-NLS-1$
        return super.printExpression(0, output).append('>');
    }

    public TypeBinding resolveType(BlockScope scope) {

        this.actualReceiverType = this.receiver.resolveType(scope);

        if ((this.actualReceiverType == null || !this.actualReceiverType.isValidBinding())
            && this.receiver instanceof MessageSend) {
            MessageSend messageSend = (MessageSend)this.receiver;
            if (messageSend.receiver instanceof ThisReference) {
                Expression[] arguments = messageSend.arguments;
                int length = arguments == null ? 0 : arguments.length;
                TypeBinding[] argBindings = new TypeBinding[length];
                for (int i = 0; i < length; i++) {
                    argBindings[i] = arguments[i].resolvedType;
                    if (argBindings[i] == null || !argBindings[i].isValidBinding()) {
                        throw new CompletionNodeFound();
                    }
                }

                ProblemMethodBinding problemMethodBinding =
                        new ProblemMethodBinding(messageSend.selector, argBindings, ProblemReasons.NotFound);
                throw new CompletionNodeFound(this, problemMethodBinding, scope);
            }
        }

        if (this.actualReceiverType == null || this.actualReceiverType.isBaseType()
            || !this.actualReceiverType.isValidBinding())
            throw new CompletionNodeFound();
        else
            throw new CompletionNodeFound(this, this.actualReceiverType, scope);
        // array types are passed along to find the length field
    }
}
