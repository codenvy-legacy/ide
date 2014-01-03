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

import com.codenvy.ide.ext.java.jdt.core.Flags;
import com.codenvy.ide.ext.java.shared.JavaType;
import com.codenvy.ide.ext.java.shared.TypeInfo;
import com.thoughtworks.qdox.model.JavaClass;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.StringReader;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 10:13:42 AM Mar 13, 2012 evgen $
 */
@RunWith(MockitoJUnitRunner.class)
public class TypeSignatureTest extends SignatureBase {

    @Test
    public void classNotGeneric() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class NotGenericClass{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.NotGenericClass");
        assertThat(typeInfo.getSignature()).isNull();
    }

    @Test
    public void classGeneric() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class GenericClass<E>{}");
        StringReader reader = new StringReader(b.toString());
        javaDocBuilderVfs.addSource(reader);
        JavaClass clazz = javaDocBuilderVfs.getClassByName("test.GenericClass");
        assertThat(clazz.getTypeParameters()).isNotEmpty();
    }

    @Test
    public void classGenericSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class GenericClass<E>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo("<E:Ljava/lang/Object;>Ljava/lang/Object;");
    }

    @Test
    public void classGenericWithUpperLimit() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public class GenericClass<E extends String>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo("<E:Ljava/lang/String;>Ljava/lang/Object;");
    }

    @Test
    public void classGenericWithDoubleLimit() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.Comparator;\n");
        b.append("public class GenericClass<E extends String & Comparator>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo(
                "<E:Ljava/lang/String;:Ljava/util/Comparator;>Ljava/lang/Object;");
    }

    @Test
    public void genericParameter() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.Comparator;\n");
        b.append("public class GenericClass<E extends Comparator<String>>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo(
                "<E::Ljava/util/Comparator<Ljava/lang/String;>;>Ljava/lang/Object;");
    }

    @Test
    public void genericParameterSelf() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.Comparator;\n");
        b.append("public class GenericClass<E extends Comparator<E>>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo("<E::Ljava/util/Comparator<TE;>;>Ljava/lang/Object;");
    }

    @Test
    public void genericParameterNotExist() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.Comparator;\n");
        b.append("import org.test.Dummy;\n");
        b.append("public class GenericClass<E extends Dummy>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo("<E:Lorg/test/Dummy;>Ljava/lang/Object;");
    }

    @Test
    public void genericWithClassAndInterfaceSelf() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.Comparator;\n");
        b.append("import java.util.ArrayList;\n");
        b.append("public class GenericClass<E extends ArrayList<E> & Comparator<E>>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo(
                "<E:Ljava/util/ArrayList<TE;>;:Ljava/util/Comparator<TE;>;>Ljava/lang/Object;");
    }

    @Test
    public void genericParameterListOfMapOfSelf() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.ArrayList;\n");
        b.append("import java.util.Map;");
        b.append("public class GenericClass<E extends ArrayList<Map<String, E>>>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo(
                "<E:Ljava/util/ArrayList<Ljava/util/Map<Ljava/lang/String;TE;>;>;>Ljava/lang/Object;");
    }

    @Test
    public void signatureWithSuperClass() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.ArrayList;\n");
        b.append("public class GenericClass<E> extends ArrayList{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo("<E:Ljava/lang/Object;>Ljava/util/ArrayList;");
    }

    @Test
    public void signatureWithGenericSuperClass() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.ArrayList;\n");
        b.append("public class GenericClass<E> extends ArrayList<E>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo("<E:Ljava/lang/Object;>Ljava/util/ArrayList<TE;>;");
    }

    @Test
    public void signatureWithHashMapSuperClass() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.HashMap;\n");
        b.append("import java.util.ArrayList;\n");
        b.append("public class GenericClass<E> extends ArrayList<E>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo("<E:Ljava/lang/Object;>Ljava/util/ArrayList<TE;>;");
    }

    @Test
    public void signatureWithInterface() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.AbstractCollection;\n");
        b.append("import java.util.List;\n");
        b.append("public class GenericClass<E> extends AbstractCollection implements List{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo(
                "<E:Ljava/lang/Object;>Ljava/util/AbstractCollection;Ljava/util/List;");
    }

    @Test
    public void signatureWithGenericInterface() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.AbstractCollection;\n");
        b.append("import java.util.List;\n");
        b.append("public class GenericClass<E> extends AbstractCollection<String> implements List<E>{}");
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo(
                "<E:Ljava/lang/Object;>Ljava/util/AbstractCollection<Ljava/lang/String;>;Ljava/util/List<TE;>;");
    }

    @Test
    public void typeParameterAsTypeInSamePackage() throws Exception {
        StringBuilder s = new StringBuilder("package test;\n");
        s.append("public class MyType{}");
        javaDocBuilderVfs.addSource(new StringReader(s.toString()));
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.AbstractCollection;\n");
        b.append("import java.util.List;\n");
        b.append("public class GenericClass extends AbstractCollection<MyType> implements List<MyType>{}");

        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo(
                "Ljava/util/AbstractCollection<Ltest/MyType;>;Ljava/util/List<Ltest/MyType;>;");
    }

    @Test
    public void superClassGeneric() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.AbstractCollection;\n");
        b.append("import java.util.List;\n");
        b.append("public class GenericClass extends AbstractCollection<Integer>{}");

        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/AbstractCollection<Ljava/lang/Integer;>;");
    }

    @Test
    public void interfaceGeneric() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.AbstractCollection;\n");
        b.append("import java.util.List;\n");
        b.append("public class GenericClass implements List<Integer>{}");

        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo("Ljava/lang/Object;Ljava/util/List<Ljava/lang/Integer;>;");
    }


    @Test
    public void interfaceGeneric2() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.AbstractCollection;\n");
        b.append("import java.util.List;\n");
        b.append("public interface GenericClass extends List<Integer>{}");

        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull().isEqualTo("Ljava/lang/Object;Ljava/util/List<Ljava/lang/Integer;>;");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void externalClassAsTypeBound() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.AbstractCollection;\n");
        b.append("import java.util.List;\n");
        b.append("import org.springframework.web.servlet.mvc.Controller;\n");
        b.append("public class GenericClass<T extends Controller>{}");
        TypeInfo type = Mockito.mock(TypeInfo.class);
        when(type.getType()).thenReturn(JavaType.INTERFACE.toString());
        when(storage.getTypeByFqn(anyString(), anySet())).thenReturn(type);
        TypeInfo typeInfo = getTypeInfo(b, "test.GenericClass");
        assertThat(typeInfo.getSignature()).isNotNull()
                .isEqualTo("<T::Lorg/springframework/web/servlet/mvc/Controller;>Ljava/lang/Object;");
    }

    @Test
    public void annotationSignature() throws Exception {
        StringBuilder b = new StringBuilder().append("package test;")
                                   .append("import java.lang.annotation.ElementType;\n")
                                   .append("import java.lang.annotation.Retention;\n")
                                   .append("import java.lang.annotation.RetentionPolicy;\n")
                                   .append("import java.lang.annotation.Target;\n")
                                   .append("\n")
                                   .append("@Target(ElementType.TYPE)\n")
                                   .append("@Retention(RetentionPolicy.RUNTIME)\n")
                                   .append("public @interface DTO {\n").append("}");
        TypeInfo type = Mockito.mock(TypeInfo.class);
        when(type.getType()).thenReturn(JavaType.ANNOTATION.toString());
        when(storage.getTypeByFqn(anyString(), anySet())).thenReturn(type);
        TypeInfo typeInfo = getTypeInfo(b, "test.DTO");
        assertThat((typeInfo.getModifiers() & Flags.AccAnnotation) != 0);
    }
}
