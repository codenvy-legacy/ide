/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
