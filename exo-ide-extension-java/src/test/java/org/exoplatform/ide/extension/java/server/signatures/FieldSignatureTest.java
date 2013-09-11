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
package org.exoplatform.ide.extension.java.server.signatures;

import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:28:15 PM Mar 13, 2012 evgen $
 */
@RunWith(MockitoJUnitRunner.class)
public class FieldSignatureTest extends SignatureBase {
    @Test
    public void fieldNonGenericSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public String field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        assertThat(typeInfo.getFields()).isNotEmpty().hasSize(1);
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNull();
    }

    @Test
    public void fieldGenericSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public E field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isNotEmpty();
    }

    @Test
    public void fieldGenericSignatureTest() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public E field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("TE;");
    }

    @Test
    public void fieldTypeGeneric() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public List<String> field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<Ljava/lang/String;>;");
    }

    @Test
    public void fieldTypeGeneric2() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public List<E> field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<TE;>;");
    }

    @Test
    public void wildcardsSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public List<?> field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<*>;");
    }

    @Test
    public void covarianceSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public List<? extends String> field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<+Ljava/lang/String;>;");
    }

    @Test
    public void contravarianceSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public List<? super String> field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<-Ljava/lang/String;>;");
    }

    @Test
    public void mixedWildcardSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.ArrayList;\n");
        b.append("import java.util.Map;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public ArrayList<? super Map<String, ?>> list;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/ArrayList<-Ljava/util/Map<Ljava/lang/String;*>;>;");
    }

    @Test
    public void mixedWildcardSignature2() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.ArrayList;\n");
        b.append("import java.util.Map;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public ArrayList<? extends Map<String, ? extends E>> list;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/ArrayList<+Ljava/util/Map<Ljava/lang/String;+TE;>;>;");
    }

    @Test
    public void mixedWildcardSignature3() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.ArrayList;\n");
        b.append("import java.util.Map;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public ArrayList<? extends Map<String, E>> list;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/ArrayList<+Ljava/util/Map<Ljava/lang/String;TE;>;>;");
    }

    @Test
    public void genericArraySignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.ArrayList;\n");
        b.append("import java.util.Map;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public E[] field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("[TE;");
    }

    @Test
    public void typeParameterizedArray() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("import java.util.Map;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public List<E[]> field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<[TE;>;");
    }

    @Test
    public void typeParameterizedArray2() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("import java.util.Map;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public List<String[]> field;\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        FieldInfo fieldInfo = typeInfo.getFields().get(0);
        assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<[Ljava/lang/String;>;");
    }

}
