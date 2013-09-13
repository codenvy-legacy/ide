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

import org.exoplatform.ide.codeassistant.jvm.shared.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * AsmTypeInfo based on org.objectweb.asm.tree.AsmMember created during class
 * file parsing.
 *
 * @see org.objectweb.asm.tree.AsmMember
 */
public class AsmTypeInfo extends AsmMember implements TypeInfo {

    /**
     * This constant got from {@list Modifier}. See {@list Modifier} sources to
     * understand why this constant not in public access.
     */
    public static final int SYNTHETIC = 0x00001000;

    private final ClassNode classNode;

    public AsmTypeInfo(ClassNode classNode) {
        super(classNameFromType(classNode.name), classNode.access);
        this.classNode = classNode;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getFields() */
    @Override
    public List<FieldInfo> getFields() {
        List<FieldInfo> result = new ArrayList<FieldInfo>(classNode.fields.size());
        for (Object node : classNode.fields) {
            AsmFieldInfo fieldInfo = new AsmFieldInfo((FieldNode)node, this);
            if (!Modifier.isPrivate(fieldInfo.getModifiers())) {
                result.add(fieldInfo);
            }
        }
        return result;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getInterfaces() */
    @Override
    public List<String> getInterfaces() {
        List<String> result = new ArrayList<String>(classNode.interfaces.size());
        for (Object type : classNode.interfaces) {
            result.add(classNameFromType((String)type));
        }
        return result;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getMethods() */
    @Override
    public List<MethodInfo> getMethods() {
        List<MethodInfo> result = new ArrayList<MethodInfo>(classNode.methods.size());
        for (Object node : classNode.methods) {
            AsmMethodInfo methodInfo = new AsmMethodInfo((MethodNode)node, this);
            if (!Modifier.isPrivate(methodInfo.getModifiers())) {
                result.add(methodInfo);
            }
        }
        return result;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getSuperClass() */
    @Override
    public String getSuperClass() {
        return classNameFromType(classNode.superName);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo#getType() */
    @Override
    public String getType() {
        return JavaType.fromClassAttribute(classNode.access).toString();
    }

    /** {@inheritDoc} */
    @Override
    public String getSignature() {
        return classNode.signature != null ? classNode.signature : "";
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#getNestedTypes() */
    @Override
    @SuppressWarnings("unchecked")
    public List<Member> getNestedTypes() {
        List<InnerClassNode> innerClasses = classNode.innerClasses;
        if (innerClasses != null) {
            List<Member> nested = new ArrayList<Member>();
            for (InnerClassNode node : innerClasses)
                nested.add(new AsmMember(node.name, node.access));
            return nested;
        }
        return null;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setFields(java.util.List) */
    @Override
    public void setFields(List<FieldInfo> fields) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setInterfaces(java.util.List) */
    @Override
    public void setInterfaces(List<String> interfaces) {
        throw new UnsupportedOperationException("Set not supported");

    }

    /** @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setMethods(java.util.List) */
    @Override
    public void setMethods(List<MethodInfo> methods) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setSuperClass(java.lang.String) */
    @Override
    public void setSuperClass(String superClass) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo#setType(java.lang.String) */
    @Override
    public void setType(String type) {
        throw new UnsupportedOperationException("Set not supported");
    }

    @Override
    public void setSignature(String signature) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#setNestedTypes(java.util.List) */
    @Override
    public void setNestedTypes(List<Member> types) {
        throw new UnsupportedOperationException("Set not supported");
    }

}
