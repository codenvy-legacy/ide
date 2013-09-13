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
package org.eclipse.jdt.client.env;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.regexp.shared.RegExp;

import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.eclipse.jdt.client.internal.compiler.env.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 17, 2012 2:41:08 PM evgen $
 */
public class BinaryTypeImpl implements IBinaryType {

    public static final RegExp anonymousClass = RegExp.compile("\\$[0-9]");

    private JSONObject jsObj;

    /** @param jsObj */
    public BinaryTypeImpl(JSONObject jsObj) {
        super();
        this.jsObj = jsObj;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IGenericType#getModifiers() */
    @Override
    public int getModifiers() {
        return (int)jsObj.get("modifiers").isNumber().doubleValue();
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IGenericType#isBinaryType() */
    @Override
    public boolean isBinaryType() {
        return true;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IDependent#getFileName() */
    @Override
    public char[] getFileName() {
        return getSourceName();
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getAnnotations() */
    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return null;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getEnclosingMethod() */
    @Override
    public char[] getEnclosingMethod() {
        return null;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getEnclosingTypeName() */
    @Override
    public char[] getEnclosingTypeName() {

        char[] name = getName();
        if (CharOperation.contains('$', name)) {
            return CharOperation.subarray(name, 0, CharOperation.indexOf('$', name));
        }
        return null;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getFields() */
    @Override
    public IBinaryField[] getFields() {
        JSONArray array = jsObj.get("fields").isArray();
        if (array.size() == 0)
            return null;

        IBinaryField[] fields = new IBinaryField[array.size()];

        for (int i = 0; i < array.size(); i++) {
            fields[i] = new BinaryFieldImpl(array.get(i).isObject());
        }

        return fields;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getGenericSignature() */
    @Override
    public char[] getGenericSignature() {
        if (jsObj.containsKey("signature") && jsObj.get("signature").isNull() == null) {
            String stringValue = jsObj.get("signature").isString().stringValue();
            if (!stringValue.isEmpty())
                return stringValue.toCharArray();
        }
        return null;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getInterfaceNames() */
    @Override
    public char[][] getInterfaceNames() {
        JSONArray array = jsObj.get("interfaces").isArray();
        if (array.size() == 0)
            return null;
        char res[][] = new char[array.size()][];
        for (int i = 0; i < array.size(); i++) {
            res[i] = array.get(i).isString().stringValue().replaceAll("\\.", "/").toCharArray();
        }

        return res;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getMemberTypes() */
    @Override
    public IBinaryNestedType[] getMemberTypes() {
        if (jsObj.get("nestedTypes").isArray() != null) {
            JSONArray array = jsObj.get("nestedTypes").isArray();
            ArrayList<IBinaryNestedType> nested = new ArrayList<IBinaryNestedType>(array.size());
            char[] parentType = getName();
            for (int i = 0; i < array.size(); i++) {
                BinaryNestedTypeImpl binaryNestedType = new BinaryNestedTypeImpl(parentType, array.get(i).isObject());
                String name = new String(binaryNestedType.getName());
                if (!anonymousClass.test(name)) {
                    nested.add(binaryNestedType);
                }
            }
            return nested.toArray(new IBinaryNestedType[nested.size()]);
        }
        return null;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getMethods() */
    @Override
    public IBinaryMethod[] getMethods() {
        JSONArray array = jsObj.get("methods").isArray();
        if (array.size() == 0)
            return null;

        // remove methods not declared in this class
        List<IBinaryMethod> methods = new ArrayList<IBinaryMethod>();
        String fqn = jsObj.get("name").isString().stringValue();
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.get(i).isObject();
            if (object.get("declaringClass").isString().stringValue().equals(fqn))
                methods.add(new BinaryMethodImpl(object));
        }

        return methods.toArray(new IBinaryMethod[methods.size()]);
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getMissingTypeNames() */
    @Override
    public char[][][] getMissingTypeNames() {
        return null;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getName() */
    @Override
    public char[] getName() {
        return jsObj.get("name").isString().stringValue().replaceAll("\\.", "/").toCharArray();
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getSourceName() */
    @Override
    public char[] getSourceName() {
        String name = jsObj.get("name").isString().stringValue();
        if (name.contains("$"))
            return Signature.getSimpleName(name.substring(name.lastIndexOf("$") + 1)).toCharArray();

        return Signature.getSimpleName(name).toCharArray();

    }

    /**
     * Answer the resolved name of the type in the source file format: <code>java.lang.String</code>
     *
     * @return
     */
    public char[] getFqn() {
        return jsObj.get("name").isString().stringValue().toCharArray();
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getSuperclassName() */
    @Override
    public char[] getSuperclassName() {
        String value = jsObj.get("superClass").isString().stringValue();
        if (value.isEmpty())
            return null;
        return value.replaceAll("\\.", "/").toCharArray();
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getTagBits() */
    @Override
    public long getTagBits() {
        return 0;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#isAnonymous() */
    @Override
    public boolean isAnonymous() {
        return false;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#isLocal() */
    @Override
    public boolean isLocal() {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#isMember() */
    @Override
    public boolean isMember() {
        return CharOperation.contains('$', getName());
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#sourceFileName() */
    @Override
    public char[] sourceFileName() {
        return null;
    }

    /**
     * JSON representation of this object
     *
     * @return JSON string
     */
    public String toJsonString() {
        return jsObj.toString();
    }

}
