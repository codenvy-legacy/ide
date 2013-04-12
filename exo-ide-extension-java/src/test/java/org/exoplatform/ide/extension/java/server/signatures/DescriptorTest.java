/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.java.server.signatures;

import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
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
