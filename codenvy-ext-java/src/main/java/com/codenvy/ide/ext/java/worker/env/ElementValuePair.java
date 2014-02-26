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

import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryElementValuePair;
import com.codenvy.ide.ext.java.worker.env.json.ElementValuePairJso;

/**
 * @author Evgen Vidolob
 */
public class ElementValuePair implements IBinaryElementValuePair {

    private ElementValuePairJso jso;

    public ElementValuePair(ElementValuePairJso jso) {
        this.jso = jso;
    }

    @Override
    public char[] getName() {
        if(jso.getName() == null) return null;
        return jso.getName().toCharArray();
    }

    @Override
    public Object getValue() {
        return Util.getDefaultValue(jso.getValue());
    }
}
