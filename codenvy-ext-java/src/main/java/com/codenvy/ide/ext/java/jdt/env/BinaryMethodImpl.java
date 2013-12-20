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

import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.jdt.internal.compiler.codegen.ConstantPool;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.collections.js.JsoArray;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 17, 2012 4:52:34 PM evgen $
 */
public class BinaryMethodImpl implements IBinaryMethod {

    private Jso method;

    public BinaryMethodImpl(Jso method) {
        super();
        this.method = method;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IGenericMethod#getModifiers() */
    @Override
    public int getModifiers() {

        int modifiers = method.getIntField("modifiers");
        //asm not add  AccAnnotationDefault constant for method modifiers, so add manual for annotation methods with default values
        if (getDefaultValue() != null) {
            modifiers |= ClassFileConstants.AccAnnotationDefault;
        }
        return modifiers;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IGenericMethod#isConstructor() */
    @Override
    public boolean isConstructor() {
        return method.getBooleanField("constructor");
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IGenericMethod#getArgumentNames() */
    @Override
    public char[][] getArgumentNames() {
        JsoArray<String> array = method.getJsObjectField("parameterNames").cast();
        if (array.size() == 0)
            return null;

        char res[][] = new char[array.size()][];
        for (int i = 0; i < array.size(); i++) {
            res[i] = array.get(i).toCharArray();
        }

        return res;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod#getAnnotations() */
    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return null;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod#getDefaultValue() */
    @Override
    public Object getDefaultValue() {
        if (method.hasOwnProperty("annotationDefault")) {
            if (method.getJsObjectField("annotationDefault")  != null) {
                return AnnotationParseUtil.getValue((Jso)method.getJsObjectField("annotationDefault"));
            }
        }
        return null;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod#getExceptionTypeNames() */
    @Override
    public char[][] getExceptionTypeNames() {
        JsoArray<String> array = method.getJsObjectField("exceptionTypes").cast();
        if (array.size() == 0)
            return null;

        char res[][] = new char[array.size()][];
        for (int i = 0; i < array.size(); i++) {
            res[i] = array.get(i).replaceAll("\\.", "/").toCharArray();
        }

        return res;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod#getGenericSignature() */
    @Override
    public char[] getGenericSignature() {
        if (method.hasOwnProperty("signature") && method.getStringField("signature") != null) {
            String stringValue = method.getStringField("signature");
            if (!stringValue.isEmpty())
                return stringValue.toCharArray();
        }
        return null;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod#getMethodDescriptor() */
    @Override
    public char[] getMethodDescriptor() {
        return method.getStringField("descriptor").toCharArray();
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod#getParameterAnnotations(int) */
    @Override
    public IBinaryAnnotation[] getParameterAnnotations(int index) {
        return null;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod#getSelector() */
    @Override
    public char[] getSelector() {
        if (isConstructor())
            return ConstantPool.Init;

        return Signature.getSimpleName(method.getStringField("name").toCharArray());
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod#getTagBits() */
    @Override
    public long getTagBits() {
        return 0;
    }

    /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod#isClinit() */
    @Override
    public boolean isClinit() {
        // TODO Auto-generated method stub
        return false;
    }

}
