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
package org.exoplatform.ide.codeassistant.asm.visitors;

import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

enum Operation {

    GENERIC, ARRAY, CLASS, BASE

}

/**
 * Visitor parses part of method's signature with type or types. Its visitor may
 * be used for parsing method's return type and parameters from other visitor.
 */
public class ParameterSignatureVisitor implements SignatureVisitor {

    private StringBuilder currentParameter;

    private final List<String> parameters;

    private Stack<Operation> operationStack;

    public ParameterSignatureVisitor() {
        this.parameters = new ArrayList<String>();
        this.currentParameter = new StringBuilder();

        this.operationStack = new Stack<Operation>();
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        throw new UnsupportedOperationException("Event not supported by parameter visitor");
    }

    @Override
    public SignatureVisitor visitClassBound() {
        throw new UnsupportedOperationException("Event not supported by parameter visitor");
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        throw new UnsupportedOperationException("Event not supported by parameter visitor");
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        throw new UnsupportedOperationException("Event not supported by parameter visitor");
    }

    @Override
    public SignatureVisitor visitInterface() {
        throw new UnsupportedOperationException("Event not supported by parameter visitor");
    }

    @Override
    public SignatureVisitor visitParameterType() {
        throw new UnsupportedOperationException("Event not supported by parameter visitor");
    }

    @Override
    public SignatureVisitor visitReturnType() {
        throw new UnsupportedOperationException("Event not supported by parameter visitor");
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        throw new UnsupportedOperationException("Event not supported by parameter visitor");
    }

    @Override
    public void visitBaseType(char descriptor) {
        begin(Operation.BASE);
        switch (descriptor) {
            case 'V':
                currentParameter.append("void");
                break;
            case 'B':
                currentParameter.append("byte");
                break;
            case 'J':
                currentParameter.append("long");
                break;
            case 'Z':
                currentParameter.append("boolean");
                break;
            case 'I':
                currentParameter.append("int");
                break;
            case 'S':
                currentParameter.append("short");
                break;
            case 'C':
                currentParameter.append("char");
                break;
            case 'F':
                currentParameter.append("float");
                break;
            case 'D':
                currentParameter.append("double");
                break;
        }
        end();
    }

    @Override
    public void visitTypeVariable(String name) {
        begin(Operation.BASE);
        currentParameter.append(name);
        end();
    }

    @Override
    public SignatureVisitor visitArrayType() {
        begin(Operation.ARRAY);
        return this;
    }

    @Override
    public void visitClassType(String name) {
        this.currentParameter.append(name.replace('/', '.'));
        begin(Operation.CLASS);
    }

    @Override
    public void visitInnerClassType(String name) {
        currentParameter.append("$");
        currentParameter.append(name);
    }

    @Override
    public void visitTypeArgument() {
        currentParameter.append("<?>");
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        if (operationStack.peek().equals(Operation.GENERIC)) {
            currentParameter.append(", ");
        } else {
            begin(Operation.GENERIC);
            currentParameter.append("<");
        }

        if (wildcard == SignatureVisitor.EXTENDS) {
            currentParameter.append("? extends ");
        } else if (wildcard == SignatureVisitor.SUPER) {
            currentParameter.append("? super ");
        }
        return this;
    }

    @Override
    public void visitEnd() {
        end();
    }

    private void begin(Operation operation) {
        operationStack.push(operation);
    }

    private void end() {
        if (operationStack.peek().equals(Operation.GENERIC)) {
            currentParameter.append(">");
            operationStack.pop();
        }
        operationStack.pop();
        while (!operationStack.isEmpty() && operationStack.peek().equals(Operation.ARRAY)) {
            operationStack.pop();
            currentParameter.append("[]");
        }
        if (operationStack.isEmpty()) {
            this.parameters.add(currentParameter.toString());
            currentParameter = new StringBuilder();
        }
    }

    public List<String> getParameters() {
        return parameters;
    }

}
