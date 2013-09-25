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

import com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryElementValuePair;

import com.google.gwt.json.client.JSONObject;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class BinaryElementValuePairImpl implements IBinaryElementValuePair {

    private final JSONObject val;

    /** @param val */
    public BinaryElementValuePairImpl(JSONObject val) {
        this.val = val;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryElementValuePair#getName() */
    @Override
    public char[] getName() {
        return val.get("name").isString().stringValue().toCharArray();
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryElementValuePair#getValue() */
    @Override
    public Object getValue() {
        return AnnotationParseUtil.getValue(val.get("value").isObject());
    }

}
