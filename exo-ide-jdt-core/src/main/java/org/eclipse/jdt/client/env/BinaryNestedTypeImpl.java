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
package org.eclipse.jdt.client.env;

import com.google.gwt.json.client.JSONObject;

import org.eclipse.jdt.client.internal.compiler.env.IBinaryNestedType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class BinaryNestedTypeImpl implements IBinaryNestedType {

    private final JSONObject nestedType;

    private final char[] parentType;

    /**
     * @param parentType
     * @param nestedType
     */
    public BinaryNestedTypeImpl(char[] parentType, JSONObject nestedType) {
        super();
        this.parentType = parentType;
        this.nestedType = nestedType;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryNestedType#getEnclosingTypeName() */
    @Override
    public char[] getEnclosingTypeName() {
        return parentType;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryNestedType#getModifiers() */
    @Override
    public int getModifiers() {
        return (int)nestedType.get("modifiers").isNumber().doubleValue();
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryNestedType#getName() */
    @Override
    public char[] getName() {
        return nestedType.get("name").isString().stringValue().replaceAll("\\.", "/").toCharArray();
    }

}
