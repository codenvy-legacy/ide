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
package com.codenvy.ide.ext.java.server.signatures;

import com.codenvy.ide.ext.java.shared.FieldInfo;
import com.codenvy.ide.ext.java.shared.MethodInfo;
import com.codenvy.ide.ext.java.shared.TypeInfo;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  5:32:31 PM Mar 15, 2012 evgen $
 */
public class DescriptorTest extends SignatureBase {

    @Test
    public void fieldGenericSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public E field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getDescriptor()).isEqualTo("Ljava/lang/Object;");
    }


    @Test
    public void fieldInitialValue() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass{\n");
        b.append("public final Integer field = new Integer(10);\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getValue()).isNull();
    }

    @Test
    public void fieldPrimitiveSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public int field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getDescriptor()).isEqualTo("I");
    }

    @Test
    public void fieldStringSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public String field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getDescriptor()).isEqualTo("Ljava/lang/String;");
    }

    @Test
    public void fieldStringArraySignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public String[][][] field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getDescriptor()).isEqualTo("[[[Ljava/lang/String;");
    }

    @Test
    public void methodReturnArraySignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public String[] field(){return null;};\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getDescriptor()).isEqualTo("()[Ljava/lang/String;");
    }

    @Test
    public void methodReturnMultiArraySignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public String[][][][] field(){return null;};\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getDescriptor()).isEqualTo("()[[[[Ljava/lang/String;");
    }

    @Test
    public void methodParameterArraySignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public void field(String[] ss){return;};\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getDescriptor()).isEqualTo("([Ljava/lang/String;)V");
    }

}
