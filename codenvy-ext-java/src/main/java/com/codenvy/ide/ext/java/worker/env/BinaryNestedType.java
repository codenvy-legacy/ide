/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.worker.env;

import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryNestedType;
import com.codenvy.ide.ext.java.worker.env.json.MemberTypesJso;

/**
 * @author Evgen Vidolob
 */
public class BinaryNestedType implements IBinaryNestedType {

    private MemberTypesJso jso;

    public BinaryNestedType(MemberTypesJso jso) {
        this.jso = jso;
    }

    @Override
    public char[] getEnclosingTypeName() {
        if(jso.getEnclosingTypeName() == null) return null;
        return jso.getEnclosingTypeName().toCharArray();
    }

    @Override
    public int getModifiers() {
        return jso.getModifiers();
    }

    @Override
    public char[] getName() {
        if(jso.getName() == null) return null;
        return jso.getName().toCharArray();
    }
}
