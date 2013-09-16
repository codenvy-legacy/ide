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

import org.exoplatform.ide.codeassistant.jvm.shared.Member;
import org.objectweb.asm.Type;

/**
 * Member based on org.objectweb.asm.tree.MemberNode created during class file
 * parsing.
 *
 * @see org.objectweb.asm.tree.MemberNode
 */
public class AsmMember implements Member {
    private final String name;

    private final int modifiers;

    public AsmMember(String name, int modifiers) {
        super();
        this.name = name;
        this.modifiers = modifiers;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.Member#getModifiers() */
    @Override
    public int getModifiers() {
        return modifiers;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.Member#getName() */
    @Override
    public String getName() {
        return name;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.Member#setModifiers(int) */
    @Override
    public void setModifiers(int modifiers) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.Member#setName(java.lang.String) */
    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Set not supported");
    }

    public static String classNameFromType(String type) {
        // can be null for Object super class.
        return type == null ? "" : Type.getObjectType(type).getClassName();
    }

}
