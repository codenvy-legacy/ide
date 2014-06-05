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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.collections.JsonObject;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ClassSignature;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.EnumConstantSignature;
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
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.TypeIds;
import com.codenvy.ide.ext.java.worker.env.json.AnnotationJso;

/**
 * @author Evgen Vidolob
 */
public class Util {

    public static char[][] arrayStringToCharArray(Array<String> strings) {
        if (strings == null) return null;
        if(strings.size() == 0) return null;
        char[][] result = new char[strings.size()][];
        for (int i = 0; i < strings.size(); i++) {
            result[i] = strings.get(i).toCharArray();
        }
        return result;
    }

    public static Object getDefaultValue(Jso jso){
        if(jso == null) return null;
        if(jso.hasOwnProperty("constant")) {
            return getConstant(jso.getJsObjectField("constant").<Jso>cast());
        } else if(jso.hasOwnProperty("class")) {
            return new ClassSignature(jso.getStringField("class").toCharArray());
        } else if (jso.hasOwnProperty("annotation")) {
            return new BinaryAnnotation(jso.getJsObjectField("annotation").<AnnotationJso>cast());
        }else if(jso.hasOwnProperty("enum")){
            JsonObject anEnum = jso.getObjectField("enum");
            return new EnumConstantSignature(anEnum.getStringField("typeName").toCharArray(),
                                             anEnum.getStringField("constantName").toCharArray());
        }
        else if(jso.hasOwnProperty("array")){
            JsoArray<JsonObject> array = jso.getArrayField("array");
            Object[] arr = new Object[array.size()];
            for (int i = 0; i < array.size(); i++) {
                arr[i] = getDefaultValue((Jso)array.get(i));
            }
            return arr;
        }
        return null;
    }

    public static Constant getConstant(Jso constant) {
        int typeId = constant.getIntField("typeId");
        Constant con = null;
        switch (typeId) {
            case TypeIds.T_int :
                con = IntConstant.fromValue(constant.getIntField("value"));
                break;
            case TypeIds.T_byte :
                con = ByteConstant.fromValue(Byte.parseByte(constant.getStringField("value")));
                break;
            case TypeIds.T_short :
                con = ShortConstant.fromValue((short)constant.getIntField("value"));
                break;
            case TypeIds.T_char :
                con = CharConstant.fromValue(constant.getStringField("value").charAt(0));
                break;
            case TypeIds.T_float :
                con = FloatConstant.fromValue(Float.valueOf(constant.getStringField("value")));
                break;
            case TypeIds.T_double :
                if(constant.hasOwnProperty("NotAConstant")){
                   con = Constant.NotAConstant;
                }
                else {
                   con = DoubleConstant.fromValue(constant.getDoubleField("value"));
                }
                break;
            case TypeIds.T_boolean :
                con = BooleanConstant.fromValue(constant.getBooleanField("value"));
                break;
            case TypeIds.T_long :
                con = LongConstant.fromValue(Long.parseLong(constant.getStringField("value")));
                break;
            case TypeIds.T_JavaLangString :
                con = StringConstant.fromValue(constant.getStringField("value"));
        }
        return con;
    }
}
