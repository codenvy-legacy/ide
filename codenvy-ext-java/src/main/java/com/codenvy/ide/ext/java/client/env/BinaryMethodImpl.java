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
import com.codenvy.ide.ext.java.client.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.client.internal.compiler.codegen.ConstantPool;
import com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 17, 2012 4:52:34 PM evgen $
 */
public class BinaryMethodImpl implements IBinaryMethod {

    private JSONObject method;

    /** @param method */
    public BinaryMethodImpl(JSONObject method) {
        super();
        this.method = method;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IGenericMethod#getModifiers() */
    @Override
    public int getModifiers() {

        int modifiers = (int)method.get("modifiers").isNumber().doubleValue();
        //asm not add  AccAnnotationDefault constant for method modifiers, so add manual for annotation methods with default values
        if (getDefaultValue() != null) {
            modifiers |= ClassFileConstants.AccAnnotationDefault;
        }
        return modifiers;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IGenericMethod#isConstructor() */
    @Override
    public boolean isConstructor() {
        return method.get("constructor").isBoolean().booleanValue();
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IGenericMethod#getArgumentNames() */
    @Override
    public char[][] getArgumentNames() {
        JSONArray array = method.get("parameterNames").isArray();
        if (array.size() == 0)
            return null;

        char res[][] = new char[array.size()][];
        for (int i = 0; i < array.size(); i++) {
            res[i] = array.get(i).isString().stringValue().toCharArray();
        }

        return res;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod#getAnnotations() */
    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return null;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod#getDefaultValue() */
    @Override
    public Object getDefaultValue() {
        if (method.containsKey("annotationDefault")) {
            if (method.get("annotationDefault").isNull() == null) {
                return AnnotationParseUtil.getValue(method.get("annotationDefault").isObject());
            }
        }
        return null;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod#getExceptionTypeNames() */
    @Override
    public char[][] getExceptionTypeNames() {
        JSONArray array = method.get("exceptionTypes").isArray();
        if (array.size() == 0)
            return null;

        char res[][] = new char[array.size()][];
        for (int i = 0; i < array.size(); i++) {
            res[i] = array.get(i).isString().stringValue().replaceAll("\\.", "/").toCharArray();
        }

        return res;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod#getGenericSignature() */
    @Override
    public char[] getGenericSignature() {
        if (method.containsKey("signature") && method.get("signature").isNull() == null) {
            String stringValue = method.get("signature").isString().stringValue();
            if (!stringValue.isEmpty())
                return stringValue.toCharArray();
        }
        return null;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod#getMethodDescriptor() */
    @Override
    public char[] getMethodDescriptor() {
        return method.get("descriptor").isString().stringValue().toCharArray();
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod#getParameterAnnotations(int) */
    @Override
    public IBinaryAnnotation[] getParameterAnnotations(int index) {
        return null;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod#getSelector() */
    @Override
    public char[] getSelector() {
        if (isConstructor())
            return ConstantPool.Init;

        return Signature.getSimpleName(method.get("name").isString().stringValue().toCharArray());
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod#getTagBits() */
    @Override
    public long getTagBits() {
        return 0;
    }

    /** @see com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod#isClinit() */
    @Override
    public boolean isClinit() {
        // TODO Auto-generated method stub
        return false;
    }

}
