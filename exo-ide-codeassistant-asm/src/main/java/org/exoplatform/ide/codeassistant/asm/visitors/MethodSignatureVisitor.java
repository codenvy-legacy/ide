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

import java.util.List;

enum Stage {

    FORMAL, PARAMETERS, RETURN, EXCEPTIONS

}

/**
 * This visitor parses return type and parameters of method's signature. Formal
 * type parameters are skipped.
 */
public class MethodSignatureVisitor implements SignatureVisitor {

    private ParameterSignatureVisitor returnType;

    private ParameterSignatureVisitor parameters;

    private ParameterSignatureVisitor exceptions;

    private Stage stage;

    public MethodSignatureVisitor() {
        this.returnType = new ParameterSignatureVisitor();
        this.parameters = new ParameterSignatureVisitor();
        this.exceptions = new ParameterSignatureVisitor();
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        stage = Stage.FORMAL;
    }

    @Override
    public SignatureVisitor visitClassBound() {
        return this;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        return this;
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
        stage = Stage.PARAMETERS;
        return parameters;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        stage = Stage.RETURN;
        return returnType;
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        stage.equals(Stage.EXCEPTIONS);
        return exceptions;
    }

    @Override
    public void visitBaseType(char descriptor) {
        if (stage.equals(Stage.FORMAL)) {
            // skip
        } else {
            throw new UnsupportedOperationException("Event not supported by parameter visitor");
        }
    }

    @Override
    public void visitTypeVariable(String name) {
        if (stage.equals(Stage.FORMAL)) {
            // skip
        } else {
            throw new UnsupportedOperationException("Event not supported by parameter visitor");
        }
    }

    @Override
    public SignatureVisitor visitArrayType() {
        if (stage.equals(Stage.FORMAL)) {
            // skip
            return this;
        } else {
            throw new UnsupportedOperationException("Event not supported by parameter visitor");
        }
    }

    @Override
    public void visitClassType(String name) {
        if (stage.equals(Stage.FORMAL)) {
            // skip
        } else {
            throw new UnsupportedOperationException("Event not supported by parameter visitor");
        }
    }

    @Override
    public void visitInnerClassType(String name) {
        if (stage.equals(Stage.FORMAL)) {
            // skip
        } else {
            throw new UnsupportedOperationException("Event not supported by parameter visitor");
        }
    }

    @Override
    public void visitTypeArgument() {
        if (stage.equals(Stage.FORMAL)) {
            // skip
        } else {
            throw new UnsupportedOperationException("Event not supported by parameter visitor");
        }
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        if (stage.equals(Stage.FORMAL)) {
            // skip
            return this;
        } else {
            throw new UnsupportedOperationException("Event not supported by parameter visitor");
        }
    }

    @Override
    public void visitEnd() {
    }

    public List<String> getParameters() {
        return parameters.getParameters();
    }

    public String getReturnType() {
        if (returnType.getParameters().isEmpty()) {
            return null;
        }
        return returnType.getParameters().get(0);
    }

}
