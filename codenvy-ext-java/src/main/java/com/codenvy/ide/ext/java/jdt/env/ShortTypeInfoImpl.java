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

import com.codenvy.ide.ext.java.shared.ShortTypeInfo;
import com.google.gwt.json.client.JSONObject;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ShortTypeInfoImpl implements ShortTypeInfo {
    private JSONObject object;

    public ShortTypeInfoImpl(JSONObject object) {
        this.object = object;
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return object.get("type").isString().stringValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setType(String type) {
    }

    /** {@inheritDoc} */
    @Override
    public String getSignature() {
        return object.get("signature").isString().stringValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setSignature(String signature) {
    }

    /** {@inheritDoc} */
    @Override
    public int getModifiers() {
        return (int)object.get("modifiers").isNumber().doubleValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setModifiers(int modifiers) {
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return object.get("name").isString().stringValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
    }
}
