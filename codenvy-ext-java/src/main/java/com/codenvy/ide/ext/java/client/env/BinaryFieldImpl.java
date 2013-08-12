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

import com.codenvy.ide.ext.java.client.core.Signature;
import com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryField;
import com.codenvy.ide.ext.java.client.internal.compiler.impl.*;

import com.google.gwt.json.client.JSONObject;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 17, 2012 4:33:03 PM evgen $
 */
public class BinaryFieldImpl implements IBinaryField {

    private JSONObject field;

    /** @param field */
    public BinaryFieldImpl(JSONObject field) {
        this.field = field;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IGenericField#getModifiers() */
    @Override
    public int getModifiers() {
        return (int)field.get("modifiers").isNumber().doubleValue();
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryField#getAnnotations() */
    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return null;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryField#getTagBits() */
    @Override
    public long getTagBits() {
        return 0;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryField#getConstant() */
    @Override
    public Constant getConstant() {
        if (field.containsKey("value") && field.get("value").isString() != null) {
            try {
                String defaultValue = field.get("value").isString().stringValue();
                if (defaultValue.isEmpty())
                    return null;
                char[] elementType = Signature.getElementType(getTypeName());
                if (elementType.length > 1)
                    return null;
                switch (elementType[0]) {
                    case 'I':
                        return IntConstant.fromValue(Integer.parseInt(defaultValue));
                    case 'Z':
                        return BooleanConstant.fromValue(Boolean.parseBoolean(defaultValue));
                    case 'C':
                        return CharConstant.fromValue(defaultValue.charAt(0));
                    case 'D':
                        return DoubleConstant.fromValue(Double.parseDouble(defaultValue));
                    case 'B':
                        return ByteConstant.fromValue(Byte.parseByte(defaultValue));
                    case 'F':
                        return FloatConstant.fromValue(Float.parseFloat(defaultValue));
                    case 'J':
                        return LongConstant.fromValue(Long.parseLong(defaultValue));
                }
            } catch (Throwable e) {
                //ignore
            }
        }
        return null;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryField#getGenericSignature() */
    @Override
    public char[] getGenericSignature() {
        if (field.containsKey("signature") && field.get("signature").isNull() == null) {
            String stringValue = field.get("signature").isString().stringValue();
            if (!stringValue.isEmpty())
                return stringValue.toCharArray();
        }
        return null;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryField#getName() */
    @Override
    public char[] getName() {
        return field.get("name").isString().stringValue().toCharArray();
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryField#getTypeName() */
    @Override
    public char[] getTypeName() {
        return field.get("descriptor").isString().stringValue().toCharArray();
    }

}
