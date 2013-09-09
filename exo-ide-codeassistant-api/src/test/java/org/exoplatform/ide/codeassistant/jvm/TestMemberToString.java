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
package org.exoplatform.ide.codeassistant.jvm;

import org.exoplatform.ide.codeassistant.jvm.bean.FieldInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.MethodInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.ShortTypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/** Check result of method toString() classes which extends Member */
public class TestMemberToString {
    @Test
    public void testFieldInfoToString() {
        FieldInfo fieldInfo = new FieldInfoBean("field", Modifier.PUBLIC, "java.lang.String", "test.TestClass", null, null, null);

        assertEquals("public java.lang.String test.TestClass.field", fieldInfo.toString());
    }

    @Test
    public void testMethodInfoToString() {
        MethodInfo methodInfo =
                new MethodInfoBean("method", Modifier.PUBLIC, new ArrayList<String>(), new ArrayList<String>(),
                                   Arrays.asList(new String[]{"param1"}), false, "", "test.TestClass", null, null, null);

        assertEquals("public void test.TestClass.method()", methodInfo.toString());
    }

    @Test
    public void testShortTypeInfoToString() {
        ShortTypeInfoBean shortTypeInfo = new ShortTypeInfoBean("test.TestClass", Modifier.PUBLIC, "CLASS", null);

        assertEquals("public CLASS test.TestClass", shortTypeInfo.toString());
    }

    @Ignore
    @Test
    public void testTypeInfoToString() {
        TypeInfoBean typeInfo = new TypeInfoBean();
        typeInfo.setModifiers(Modifier.PROTECTED);
        typeInfo.setType(JavaType.CLASS.toString());
        typeInfo.setName("test.TestClass2");
        typeInfo.setSuperClass("test.TestClass1");
        typeInfo.setInterfaces(Arrays.asList(new String[]{"test.TestInterface1", "test.TestInterface2"}));

        assertEquals(
                "protected CLASS test.TestClass2 extends test.TestClass1 implements test.TestInterface1, test.TestInterface2",
                typeInfo.toString());
    }
}
