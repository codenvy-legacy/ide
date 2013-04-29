/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
