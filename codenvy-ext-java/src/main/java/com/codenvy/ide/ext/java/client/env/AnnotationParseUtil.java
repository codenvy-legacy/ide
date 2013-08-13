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

import com.codenvy.ide.ext.java.client.internal.compiler.env.ClassSignature;
import com.codenvy.ide.ext.java.client.internal.compiler.env.EnumConstantSignature;
import com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.client.internal.compiler.impl.*;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AnnotationParseUtil {

    public static Object getValue(JSONObject value) {

        if (value.get("primitiveType").isNull() == null && value.get("primitiveType").isArray().size() != 0) {
            JSONArray array = value.get("primitiveType").isArray();
            String type = array.get(0).isString().stringValue();
            String val = array.get(1).isString().stringValue();
            return getConstant(type, val);
        } else if (value.get("enumConstant").isNull() == null && value.get("enumConstant").isArray().size() != 0) {
            JSONArray array = value.get("enumConstant").isArray();
            return new EnumConstantSignature(array.get(0).isString().stringValue().toCharArray(), array.get(1).isString()
                                                                                                       .stringValue().toCharArray());
        } else if (value.get("arrayType").isNull() == null && value.get("arrayType").isArray().size() != 0) {
            JSONArray array = value.get("arrayType").isArray();
            if (array.size() >= 1) {
                String type = array.get(0).isString().stringValue();
                if ("Type".equals(type)) {
                    ClassSignature[] classes = new ClassSignature[array.size() - 1];
                    for (int i = 1; i < array.size(); i++) {
                        classes[i - 1] = new ClassSignature(array.get(i).isString().stringValue().toCharArray());
                    }
                    return classes;
                } else {
                    return getAraysOfType(array);
                }
            } else
                return new Object[0];
        } else if (value.get("classSignature").isString() != null
                   && !value.get("classSignature").isString().stringValue().isEmpty()) {
            return new ClassSignature(value.get("classSignature").isString().stringValue().toCharArray());
        } else if (value.get("annotation").isNull() == null) {
            return new BinaryAnnotationImpl(value.get("annotation").isObject());
        }
        return new IBinaryAnnotation[0];
    }

    /**
     * @param array
     * @return
     */
    private static Object getAraysOfType(JSONArray array) {
        String type = array.get(0).isString().stringValue();
        Constant[] cons = new Constant[array.size() - 1];
        for (int i = 1; i < array.size(); i++) {
            cons[i - 1] = getConstant(type, array.get(i).isString().stringValue());
        }
        return cons;
    }

    public static Constant getConstant(String type, String value) {
        if ("Byte".equals(type))
            return ByteConstant.fromValue(Byte.parseByte(value));
        else if ("Boolean".equals(type))
            return BooleanConstant.fromValue(Boolean.parseBoolean(value));
        else if ("Character".equals(type))
            return CharConstant.fromValue(value.charAt(0));
        else if ("Short".equals(type))
            return ShortConstant.fromValue(Short.valueOf(value));
        else if ("Integer".equals(type))
            return IntConstant.fromValue(Integer.parseInt(value));
        else if ("Long".equals(type))
            return LongConstant.fromValue(Long.parseLong(value));
        else if ("Float".equals(type))
            return FloatConstant.fromValue(Float.parseFloat(value));
        else if ("Double".equals(type))
            return DoubleConstant.fromValue(Double.parseDouble(value));
        else
            return StringConstant.fromValue(value);
    }
}
