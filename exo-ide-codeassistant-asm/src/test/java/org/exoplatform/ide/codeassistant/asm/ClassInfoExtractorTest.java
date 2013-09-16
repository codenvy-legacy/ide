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
package org.exoplatform.ide.codeassistant.asm;

import junit.framework.Assert;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import org.exoplatform.ide.codeassistant.asm.test.*;
import org.exoplatform.ide.codeassistant.jvm.shared.*;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;

/**
 */
public class ClassInfoExtractorTest {

    @Test
    public void shouldExtractCorrectInterface() throws Exception {
        TypeInfo cd = ClassParser.parse(I.class);
        assertEquals(JavaType.INTERFACE.toString(), cd.getType());
    }

    @Test
    public void shouldExtractCorrectAnnotation() throws Exception {
        TypeInfo cd = ClassParser.parse(Foo.class);
        assertEquals(JavaType.ANNOTATION.toString(), cd.getType());
    }

    @Test
    public void shouldExtractCorrectEnum() throws Exception {
        TypeInfo cd = ClassParser.parse(E.class);
        assertEquals(JavaType.ENUM.toString(), cd.getType());
    }

    @Test
    public void testExtractClass() throws ClassFormatError, ClassNotFoundException, IOException {
        TypeInfo cd = ClassParser.parse(A.class);
        assertEquals(9, cd.getMethods().size());
        assertEquals(2, cd.getFields().size());
        assertEquals(A.class.getCanonicalName(), cd.getName());
    }

    @Test
    public void shouldExtractNamesOfMethodParameters() throws IOException {
        TypeInfo cd = ClassParser.parse(A.class);
        List<MethodInfo> methods = cd.getMethods();
        //check names of  public A(String string, Integer integer, long l)
        for (MethodInfo methodInfo : methods) {
            if (methodInfo.isConstructor() && methodInfo.getParameterTypes().size() == 3) {
                assertArrayEquals(new String[]{"string", "integer", "tt"}, methodInfo.getParameterNames().toArray());
            }
        }

    }

    @Test
    public void shouldExtractGenerics() throws Exception {
        TypeInfo cd = ClassParser.parse(A.class);
        List<MethodInfo> methods = cd.getMethods();
        for (MethodInfo methodInfo : methods) {

            if (methodInfo.isConstructor()) {
                if (methodInfo.getParameterNames().size() == 1) {
                    assertArrayEquals(new String[]{"java.util.Set<java.lang.Class<?>>"}, methodInfo.getParameterTypes()
                                                                                                   .toArray());
                } else if (methodInfo.getParameterNames().size() == 3) {
                    assertArrayEquals(new String[]{"java.lang.String", "java.lang.Integer",
                                                   "java.util.List<java.lang.String>"}, methodInfo.getParameterTypes().toArray());
                }

            }
        }
    }

    @Test
    public void testMapGeneric() throws IOException {
        TypeInfo cd = ClassParser.parse(B.class);
        List<MethodInfo> methods = cd.getMethods();
        boolean isFound = false;
        for (MethodInfo method : methods) {
            if (method.getName().equals("methodWithMap")) {
                method.getParameterTypes();
                Assert.assertEquals(method.getDeclaringClass(), "org.exoplatform.ide.codeassistant.asm.test.B");
                Assert.assertEquals(method.getModifiers(), 1);
                Assert.assertEquals(method.getReturnType(), "T");
                Assert.assertEquals(method.getExceptionTypes().size(), 0);
                Assert.assertEquals(method.getParameterNames().size(), 1);
                Assert.assertEquals(method.getParameterNames().get(0), "map");
                Assert.assertEquals(method.getParameterTypes().size(), 1);
                Assert.assertEquals(method.getParameterTypes().get(0),
                                    "java.util.Map<java.util.Map<java.util.Map<K, ? extends "
                                    + "java.util.Map<? super java.util.List<java.lang.Number>, V>>, java.lang.Number>, V>");
                isFound = true;
                break;
            }
        }
        Assert.assertTrue(isFound);
    }

    @Test
    public void testExtractMethod() throws IOException {
        TypeInfo cd = ClassParser.parse(B.class);
        List<MethodInfo> mds = cd.getMethods();
        Method[] methods = B.class.getDeclaredMethods();
        for (Method method : methods) {
            MethodInfo md = getMethodInfo(mds, method);
            Assert.assertNotNull("Method " + method.getName() + " not found.", md);
            assertEquals(method.getModifiers(), md.getModifiers());
            assertEquals(typeToString(method.getGenericReturnType()), md.getReturnType());
        }
    }

    @Test
    public void testExtractClassSignature() throws IOException {
        TypeInfo cd = ClassParser.parse(List.class);
        assertEquals("<E:Ljava/lang/Object;>Ljava/lang/Object;Ljava/util/Collection<TE;>;", cd.getSignature());

    }

    @Test
    public void testFieldDescriptor() throws IOException {
        String[] descriptors = {"Ljava/util/List;", "Ljava/util/Collection;"};
        String[] signatures = {"Ljava/util/List<Ljava/lang/Boolean;>;", "Ljava/util/Collection<Ljava/lang/Double;>;"};
        TypeInfo cd = ClassParser.parse(B.class);
        assertEquals(1, cd.getFields().size());

        assertEquals(descriptors[0], cd.getFields().get(0).getDescriptor());

        assertEquals(signatures[0], cd.getFields().get(0).getSignature());

        cd = ClassParser.parse(A.class);

        for (FieldInfo f : cd.getFields()) {

            assertNull(f.getSignature());
            assertNotNull(f.getDescriptor());
        }
    }

    @Test
    public void testEnumExtract() throws IOException {
        TypeInfo en = ClassParser.parse(E.class);
        assertEquals(JavaType.ENUM.name(), en.getType());
        assertEquals(3, en.getFields().size());
        assertEquals("ONE", en.getFields().get(0).getName());
        assertEquals("TWO", en.getFields().get(1).getName());
        assertEquals("THREE", en.getFields().get(2).getName());
    }

    @Test
    public void testNestedTypesExtract() throws IOException {
        TypeInfo en = ClassParser.parse(WithNestedTypes.class);
        List<Member> nestedTypes = en.getNestedTypes();
        assertNotNull(nestedTypes);
        assertEquals(1, nestedTypes.size());
        Member member = nestedTypes.get(0);
        assertEquals("org/exoplatform/ide/codeassistant/asm/test/WithNestedTypes$Nested1", member.getName());
        assertTrue(Modifier.isInterface(member.getModifiers()));
    }

    private MethodInfo getMethodInfo(List<MethodInfo> mds, Method method) {
        for (MethodInfo md : mds) {
            if (md.getName().equals(method.getName())
                && md.getParameterTypes().size() == method.getGenericParameterTypes().length) {
                Type[] types = method.getGenericParameterTypes();
                boolean z = true;
                for (int i = 0; i < types.length; i++) {
                    if (!md.getParameterTypes().get(i).equals(typeToString(types[i]))) {
                        z = false;
                        break;
                    }
                }
                if (z) {
                    return md;
                }
            }
        }
        return null;
    }

    public String typeToString(Type type) {
        if (type instanceof Class<?>) {
            return ((Class<?>)type).getCanonicalName();
        }
        if (type instanceof ParameterizedTypeImpl) {
            return type.toString();
        }
        if (type instanceof TypeVariableImpl) {
            return type.toString();
        }
        Assert.fail("This implementation of java.lang.Type not supported!");
        throw new UnsupportedOperationException("This implementation of java.lang.Type not supported!");
    }

}
