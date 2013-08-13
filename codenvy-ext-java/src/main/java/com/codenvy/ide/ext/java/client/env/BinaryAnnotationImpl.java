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
package com.codenvy.ide.ext.java.client.env;

import com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryElementValuePair;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class BinaryAnnotationImpl implements IBinaryAnnotation {

    private JSONObject annotation;

    /** @param annotation */
    public BinaryAnnotationImpl(JSONObject annotation) {
        super();
        this.annotation = annotation;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryAnnotation#getTypeName() */
    @Override
    public char[] getTypeName() {
        return annotation.get("typeName").isString().stringValue().toCharArray();
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryAnnotation#getElementValuePairs() */
    @Override
    public IBinaryElementValuePair[] getElementValuePairs() {
        JSONValue value = annotation.get("annotationParameters");
        if (value.isNull() != null)
            return null;
        JSONArray array = value.isArray();
        IBinaryElementValuePair[] val = new IBinaryElementValuePair[array.size()];
        for (int i = 0; i < array.size(); i++) {
            val[i] = new BinaryElementValuePairImpl(array.get(i).isObject());
        }
        return val;
    }

}
