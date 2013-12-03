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
package com.codenvy.ide.ext.java.worker.env;

import com.codenvy.ide.ext.java.worker.core.Signature;
import com.codenvy.ide.ext.java.worker.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryField;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryMethod;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryNestedType;
import com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType;
import com.codenvy.ide.collections.JsonObject;
import com.codenvy.ide.collections.js.Jso;
import com.codenvy.ide.collections.js.JsoArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 17, 2012 2:41:08 PM evgen $
 */
public class BinaryTypeImpl implements IBinaryType {

    private Jso jsObj;

    /** @param jsObj */
    public BinaryTypeImpl(Jso jsObj) {
        super();
        this.jsObj = jsObj;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IGenericType#getModifiers() */
    @Override
    public int getModifiers() {
        return jsObj.getIntField("modifiers");
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IGenericType#isBinaryType() */
    @Override
    public boolean isBinaryType() {
        return true;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IDependent#getFileName() */
    @Override
    public char[] getFileName() {
        return getSourceName();
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getAnnotations() */
    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return null;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getEnclosingMethod() */
    @Override
    public char[] getEnclosingMethod() {
        return null;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getEnclosingTypeName() */
    @Override
    public char[] getEnclosingTypeName() {

        char[] name = getName();
        if (CharOperation.contains('$', name)) {
            return CharOperation.subarray(name, 0, CharOperation.indexOf('$', name));
        }
        return null;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getFields() */
    @Override
    public IBinaryField[] getFields() {
        JsoArray<JsonObject> array = jsObj.getArrayField("fields");
        if (array.size() == 0)
            return null;

        IBinaryField[] fields = new IBinaryField[array.size()];

        for (int i = 0; i < array.size(); i++) {
            fields[i] = new BinaryFieldImpl((Jso)array.get(i));
        }

        return fields;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getGenericSignature() */
    @Override
    public char[] getGenericSignature() {
        if (jsObj.hasOwnProperty("signature") && jsObj.getStringField("signature") != null) {
            String stringValue = jsObj.getStringField("signature");
            if (!stringValue.isEmpty())
                return stringValue.toCharArray();
        }
        return null;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getInterfaceNames() */
    @Override
    public char[][] getInterfaceNames() {
        JsoArray<String> array = jsObj.getJsObjectField("interfaces").cast();
        if (array.size() == 0)
            return null;
        char res[][] = new char[array.size()][];
        for (int i = 0; i < array.size(); i++) {
            res[i] = array.get(i).replaceAll("\\.", "/").toCharArray();
        }

        return res;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getMemberTypes() */
    @Override
    public IBinaryNestedType[] getMemberTypes() {
        if (jsObj.getArrayField("nestedTypes") != null) {
            JsoArray<JsonObject> array = jsObj.getArrayField("nestedTypes");
            IBinaryNestedType[] nested = new IBinaryNestedType[array.size()];
            char[] parentType = getName();
            for (int i = 0; i < array.size(); i++) {
                nested[i] = new BinaryNestedTypeImpl(parentType, (Jso)array.get(i));
            }
            return nested;
        }
        return null;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getMethods() */
    @Override
    public IBinaryMethod[] getMethods() {
        JsoArray<JsonObject> array = jsObj.getArrayField("methods");
        if (array.size() == 0)
            return null;

        // remove methods not declared in this class
        List<IBinaryMethod> methods = new ArrayList<IBinaryMethod>();
        String fqn = jsObj.getStringField("name");
        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i);
            if (object.getStringField("declaringClass").equals(fqn))
                methods.add(new BinaryMethodImpl((Jso)object));
        }

        return methods.toArray(new IBinaryMethod[methods.size()]);
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getMissingTypeNames() */
    @Override
    public char[][][] getMissingTypeNames() {
        return null;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getName() */
    @Override
    public char[] getName() {
        return jsObj.getStringField("name").replaceAll("\\.", "/").toCharArray();
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getSourceName() */
    @Override
    public char[] getSourceName() {
        String name = jsObj.getStringField("name");
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
        return jsObj.getStringField("name").toCharArray();
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getSuperclassName() */
    @Override
    public char[] getSuperclassName() {
        String value = jsObj.getStringField("superClass");
        if (value.isEmpty())
            return null;
        return value.replaceAll("\\.", "/").toCharArray();
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#getTagBits() */
    @Override
    public long getTagBits() {
        return 0;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#isAnonymous() */
    @Override
    public boolean isAnonymous() {
        return false;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#isLocal() */
    @Override
    public boolean isLocal() {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#isMember() */
    @Override
    public boolean isMember() {
        return CharOperation.contains('$', getName());
    }

    /** @see com.codenvy.ide.ext.java.worker.internal.compiler.env.IBinaryType#sourceFileName() */
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
