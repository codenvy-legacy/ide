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
package org.exoplatform.ide.codeassistant.asm;

import org.exoplatform.ide.codeassistant.asm.visitors.MethodSignatureVisitor;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AsmMethodInfo based on org.objectweb.asm.tree.AsmMember created during class
 * file parsing.
 *
 * @see org.objectweb.asm.tree.AsmMember
 */
public class AsmMethodInfo extends AsmMember implements MethodInfo {

    public final static String CONSTRUCTOR_METHOD_NAME = "<init>";

    private final MethodNode methodNode;

    private final AsmTypeInfo declaredClass;

    public AsmMethodInfo(MethodNode methodNode, AsmTypeInfo declaredClass) {
        super(methodNode.name, methodNode.access);
        this.methodNode = methodNode;
        this.declaredClass = declaredClass;
    }

    /** @see org.exoplatform.ide.codeassistant.asm.AsmMember#getName() */
    @Override
    public String getName() {
        return isConstructor() ? getDeclaringClass() : super.getName();
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#getDeclaringClass() */
    @Override
    public String getDeclaringClass() {
        return declaredClass.getName();
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#getExceptionTypes() */
    @Override
    public List<String> getExceptionTypes() {
        List<String> result = new ArrayList<String>(methodNode.exceptions.size());
        for (Object type : methodNode.exceptions) {
            result.add(classNameFromType((String)type));
        }
        return result;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#getParameterNames() */
    @Override
    public List<String> getParameterNames() {
        if (methodNode.localVariables == null || methodNode.localVariables.size() < 1) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<String>(methodNode.localVariables.size() - 1);
        for (int i = 1; i < methodNode.localVariables.size(); i++) {
            LocalVariableNode var = (LocalVariableNode)methodNode.localVariables.get(i);
            result.add(var.name);
        }
        return result;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#getParameterTypes() */
    @Override
    public List<String> getParameterTypes() {
        if (methodNode.signature != null) {
            SignatureReader reader = new SignatureReader(methodNode.signature);
            MethodSignatureVisitor v = new MethodSignatureVisitor();
            reader.accept(v);
            return v.getParameters();
        } else {
            Type[] types = Type.getArgumentTypes(methodNode.desc);
            List<String> result = new ArrayList<String>(types.length);
            for (Type type : types) {
                result.add(type.getClassName());
            }
            return result;
        }
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#getReturnType() */
    @Override
    public String getReturnType() {
        if (methodNode.signature != null) {
            SignatureReader reader = new SignatureReader(methodNode.signature);
            MethodSignatureVisitor v = new MethodSignatureVisitor();
            reader.accept(v);
            return v.getReturnType();
        }
        return Type.getReturnType(methodNode.desc).getClassName();
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#isConstructor() */
    @Override
    public boolean isConstructor() {
        return CONSTRUCTOR_METHOD_NAME.equals(methodNode.name);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescriptor() {
        return methodNode.desc;
    }

    /** {@inheritDoc} */
    @Override
    public String getSignature() {
        return methodNode.signature;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#getAnnotationDefault() */
    @Override
    public AnnotationValue getAnnotationDefault() {
        if (methodNode.annotationDefault != null) {
            return new AsmAnnotationValue(methodNode.annotationDefault);
        } else
            return null;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setConstructor(boolean) */
    @Override
    public void setConstructor(boolean isConstructor) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setDeclaringClass(java.lang.String) */
    @Override
    public void setDeclaringClass(String declaringClass) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setExceptionTypes(java.util.List) */
    @Override
    public void setExceptionTypes(List<String> exceptionTypes) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setParameterNames(java.util.List) */
    @Override
    public void setParameterNames(List<String> parameterNames) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setParameterTypes(java.util.List) */
    @Override
    public void setParameterTypes(List<String> parameterTypes) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setReturnType(java.lang.String) */
    @Override
    public void setReturnType(String returnType) {
        throw new UnsupportedOperationException("Set not supported");
    }

    @Override
    public void setDescriptor(String descriptor) {
        throw new UnsupportedOperationException("Set not supported");
    }

    @Override
    public void setSignature(String signature) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#setAnnotationDefault(org.exoplatform.ide.codeassistant.jvm.shared
     * .AnnotationValue) */
    @Override
    public void setAnnotationDefault(AnnotationValue value) {
        throw new UnsupportedOperationException("Set not supported");
    }

}
