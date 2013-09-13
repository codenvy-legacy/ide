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

import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

/**
 * AsmFieldInfo based on org.objectweb.asm.tree.AsmTypeInfo created during class
 * file parsing.
 *
 * @see org.objectweb.asm.tree.AsmTypeInfo
 */
public class AsmFieldInfo extends AsmMember implements FieldInfo {
    private final FieldNode fieldNode;

    private final AsmTypeInfo declaredClass;

    public AsmFieldInfo(FieldNode fieldNode, AsmTypeInfo declaredClass) {
        super(fieldNode.name, fieldNode.access);
        this.fieldNode = fieldNode;
        this.declaredClass = declaredClass;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.FieldInfo#getDeclaringClass() */
    @Override
    public String getDeclaringClass() {
        return declaredClass.getName();
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.FieldInfo#getType() */
    @Override
    public String getType() {
        return Type.getType(fieldNode.desc).getClassName();
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.FieldInfo#setDeclaringClass(java.lang.String) */
    @Override
    public void setDeclaringClass(String declaringClass) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.FieldInfo#setType(java.lang.String) */
    @Override
    public void setType(String type) {
        throw new UnsupportedOperationException("Set not supported");
    }

    public String getDescriptor() {
        return fieldNode.desc;
    }

    public String getSignature() {
        return fieldNode.signature;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo#getValue() */
    @Override
    public String getValue() {
        if (fieldNode.value != null)
            return fieldNode.value.toString();
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setSignature(String signature) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** {@inheritDoc} */
    @Override
    public void setDescriptor(String descriptor) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo#setValue(java.lang.String) */
    @Override
    public void setValue(String value) {
        throw new UnsupportedOperationException("Set not supported");
    }

}
