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

import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ClassSignature;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.EnumConstantSignature;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.BooleanConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.ByteConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.CharConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.Constant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.DoubleConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.FloatConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.IntConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.LongConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.ShortConstant;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.StringConstant;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AnnotationParseUtil {

    public static Object getValue(Jso value) {

        if (value.getJsObjectField("primitiveType") != null && value.getJsObjectField("primitiveType").<JsoArray>cast().size() != 0) {
            JsoArray<String> array = value.getJsObjectField("primitiveType").cast();
            String type = array.get(0);
            String val = array.get(1);
            return getConstant(type, val);
        } else if (value.getJsObjectField("enumConstant") != null && value.getJsObjectField("enumConstant").<JsoArray>cast().size() != 0) {
            JsoArray<String> array = value.getJsObjectField("enumConstant").cast();
            return new EnumConstantSignature(array.get(0).toCharArray(), array.get(1).toCharArray());
        } else if (value.getJsObjectField("arrayType") != null && value.getJsObjectField("arrayType").<JsoArray>cast().size() != 0) {
            JsoArray<String> array = value.getJsObjectField("arrayType").cast();
            if (array.size() >= 1) {
                String type = array.get(0);
                if ("Type".equals(type)) {
                    ClassSignature[] classes = new ClassSignature[array.size() - 1];
                    for (int i = 1; i < array.size(); i++) {
                        classes[i - 1] = new ClassSignature(array.get(i).toCharArray());
                    }
                    return classes;
                } else {
                    return getAraysOfType(array);
                }
            } else
                return new Object[0];
        } else if (value.getStringField("classSignature") != null
                   && !value.getStringField("classSignature").isEmpty()) {
            return new ClassSignature(value.getStringField("classSignature").toCharArray());
        } else if (value.getJsObjectField("annotation") != null) {
            return new BinaryAnnotationImpl(value.getJsObjectField("annotation").<Jso>cast());
        }
        return new IBinaryAnnotation[0];
    }

    /**
     * @param array
     * @return
     */
    private static Object getAraysOfType(JsoArray<String> array) {
        String type = array.get(0);
        Constant[] cons = new Constant[array.size() - 1];
        for (int i = 1; i < array.size(); i++) {
            cons[i - 1] = getConstant(type, array.get(i));
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
