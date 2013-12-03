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
package com.codenvy.ide.ext.java.worker.env;

import com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryElementValuePair;
import com.codenvy.ide.collections.JsonObject;
import com.codenvy.ide.collections.js.Jso;
import com.codenvy.ide.collections.js.JsoArray;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class BinaryAnnotationImpl implements IBinaryAnnotation {

    private Jso annotation;


    public BinaryAnnotationImpl(Jso annotation) {
        super();
        this.annotation = annotation;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryAnnotation#getTypeName() */
    @Override
    public char[] getTypeName() {
        return annotation.getStringField("typeName").toCharArray();
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryAnnotation#getElementValuePairs() */
    @Override
    public IBinaryElementValuePair[] getElementValuePairs() {
        JsoArray<JsonObject> value = annotation.getArrayField("annotationParameters");
        if (value == null)
            return null;
        IBinaryElementValuePair[] val = new IBinaryElementValuePair[value.size()];
        for (int i = 0; i < value.size(); i++) {
            val[i] = new BinaryElementValuePairImpl((Jso)value.get(i));
        }
        return val;
    }

}
