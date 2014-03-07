/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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

import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryField;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryNestedType;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryType;
import com.codenvy.ide.ext.java.worker.env.json.AnnotationJso;
import com.codenvy.ide.ext.java.worker.env.json.BinaryTypeJso;
import com.codenvy.ide.ext.java.worker.env.json.FieldJso;
import com.codenvy.ide.ext.java.worker.env.json.MemberTypesJso;
import com.codenvy.ide.ext.java.worker.env.json.MethodJso;

/**
 * @author Evgen Vidolob
 */
public class BinaryType implements IBinaryType {

    private BinaryTypeJso jso;

    public BinaryType(BinaryTypeJso jso) {
        this.jso = jso;
    }

    @Override
    public IBinaryAnnotation[] getAnnotations() {
        JsoArray<AnnotationJso> annotations = jso.getAnnotations();
        if (annotations == null) return null;
        IBinaryAnnotation[] binaryAnnotations = new IBinaryAnnotation[annotations.size()];
        for (int i = 0; i < annotations.size(); i++) {
            binaryAnnotations[i] = new BinaryAnnotation(annotations.get(i));
        }
        return binaryAnnotations;
    }

    @Override
    public char[] getEnclosingMethod() {
        if (jso.getEnclosingMethod() == null) return null;

        return jso.getEnclosingMethod().toCharArray();
    }

    @Override
    public char[] getEnclosingTypeName() {
        if (jso.getEnclosingTypeName() == null) return null;
        return jso.getEnclosingTypeName().toCharArray();
    }

    @Override
    public IBinaryField[] getFields() {
        if (jso.getFields() == null) return null;
        JsoArray<FieldJso> fields = jso.getFields();
        IBinaryField[] binaryFields = new IBinaryField[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            binaryFields[i] = new BinaryField(fields.get(i));
        }
        return binaryFields;
    }

    @Override
    public char[] getGenericSignature() {
        if (jso.getGenericSignature() == null) return null;
        return jso.getGenericSignature().toCharArray();
    }

    @Override
    public char[][] getInterfaceNames() {
        if (jso.getInterfaceNames() == null) return null;
        return Util.arrayStringToCharArray(jso.getInterfaceNames());
    }

    @Override
    public IBinaryNestedType[] getMemberTypes() {
        if (jso.getMemberTypes() == null) return null;
        JsoArray<MemberTypesJso> memberTypes = jso.getMemberTypes();
        IBinaryNestedType[] types = new IBinaryNestedType[memberTypes.size()];
        for (int i = 0; i < memberTypes.size(); i++) {
            types[i] = new BinaryNestedType(memberTypes.get(i));
        }
        return types;
    }

    @Override
    public IBinaryMethod[] getMethods() {
        if (jso.getMethods() == null) return null;
        JsoArray<MethodJso> methods = jso.getMethods();
        IBinaryMethod[] binaryMethods = new IBinaryMethod[methods.size()];
        for (int i = 0; i < methods.size(); i++) {
            binaryMethods[i] = new BinaryMethod(methods.get(i));
        }
        return binaryMethods;
    }

    @Override
    public char[][][] getMissingTypeNames() {
        if (jso.getMissingTypeNames() == null) return null;
        JsoArray<JsoArray<String>> typeNames = jso.getMissingTypeNames();
        char[][][] names = new char[typeNames.size()][][];
        for (int i = 0; i < typeNames.size(); i++) {
            names[i] = Util.arrayStringToCharArray(typeNames.get(i));
        }
        return names;
    }

    @Override
    public char[] getName() {
        return jso.getName().toCharArray();
    }

    @Override
    public char[] getSourceName() {
        if (jso.getSourceName() == null) return null;
        return jso.getSourceName().toCharArray();
    }

    @Override
    public char[] getSuperclassName() {
        if (jso.getSuperclassName() == null) return null;
        return jso.getSuperclassName().toCharArray();
    }

    @Override
    public long getTagBits() {
        return Long.parseLong(jso.getTagBits());
    }

    @Override
    public boolean isAnonymous() {
        return jso.isAnonymous();
    }

    @Override
    public boolean isLocal() {
        return jso.isLocal();
    }

    @Override
    public boolean isMember() {
        return jso.isMember();
    }

    @Override
    public char[] sourceFileName() {
        if (jso.getSourceFileName() == null) return null;
        return jso.getSourceFileName().toCharArray();
    }

    @Override
    public int getModifiers() {
        return jso.getModifiers();
    }

    @Override
    public boolean isBinaryType() {
        return jso.isBinaryType();
    }

    @Override
    public char[] getFileName() {
        if (jso.getFileName() == null) return null;
        return jso.getFileName().toCharArray();
    }
}
