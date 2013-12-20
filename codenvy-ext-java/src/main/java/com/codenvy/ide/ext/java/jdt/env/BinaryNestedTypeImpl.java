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
package com.codenvy.ide.ext.java.jdt.env;

import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryNestedType;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class BinaryNestedTypeImpl implements IBinaryNestedType {

    private final Jso nestedType;

    private final char[] parentType;

    /**
     * @param parentType
     * @param nestedType
     */
    public BinaryNestedTypeImpl(char[] parentType, Jso nestedType) {
        super();
        this.parentType = parentType;
        this.nestedType = nestedType;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryNestedType#getEnclosingTypeName() */
    @Override
    public char[] getEnclosingTypeName() {
        return parentType;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryNestedType#getModifiers() */
    @Override
    public int getModifiers() {
        return nestedType.getIntField("modifiers");
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryNestedType#getName() */
    @Override
    public char[] getName() {
        return nestedType.getStringField("name").replaceAll("\\.", "/").toCharArray();
    }

}
