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

import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryElementValuePair;
import com.codenvy.ide.ext.java.worker.env.json.AnnotationJso;
import com.codenvy.ide.ext.java.worker.env.json.ElementValuePairJso;

/**
 * @author Evgen Vidolob
 */
public class BinaryAnnotation implements IBinaryAnnotation {

    private AnnotationJso jso;

    public BinaryAnnotation(AnnotationJso jso) {
        this.jso = jso;
    }

    @Override
    public char[] getTypeName() {
        if(jso.getTypeName() == null) return null;
        return jso.getTypeName().toCharArray();
    }

    @Override
    public IBinaryElementValuePair[] getElementValuePairs() {
        if(jso.getElementValuePairs() == null) return null;

        JsoArray<ElementValuePairJso> elementValuePairs = jso.getElementValuePairs();
        IBinaryElementValuePair[] pairs = new IBinaryElementValuePair[elementValuePairs.size()];
        for (int i = 0; i < elementValuePairs.size(); i++) {
            pairs[i] = new ElementValuePair(elementValuePairs.get(i));
        }
        return pairs;
    }
}
