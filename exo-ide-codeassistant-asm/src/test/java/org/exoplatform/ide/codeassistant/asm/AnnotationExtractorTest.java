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

import org.exoplatform.ide.codeassistant.asm.test.AnnotationWithAnnotationParameter;
import org.exoplatform.ide.codeassistant.asm.test.Bar;
import org.exoplatform.ide.codeassistant.jvm.shared.Annotation;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AnnotationExtractorTest {
    @Test
    public void shouldExtractDefailtValues() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        assertNotNull(cd.getMethods().get(0).getAnnotationDefault());
    }

    @Test
    public void shouldExtractPrimitiveIntDefailtValue() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(0).getAnnotationDefault();
        String[] primitiveType = defaultValue.getPrimitiveType();
        assertNotNull(primitiveType);
        assertEquals(2, primitiveType.length);
        assertEquals("Integer", primitiveType[0]);
        assertEquals("42", primitiveType[1]);
    }

    @Test
    public void shouldExtractPrimitiveStringDefailtValue() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(1).getAnnotationDefault();
        String[] primitiveType = defaultValue.getPrimitiveType();
        assertNotNull(primitiveType);
        assertEquals(2, primitiveType.length);
        assertEquals("String", primitiveType[0]);
        assertEquals("", primitiveType[1]);
    }

    @Test
    public void shouldExtractEnumDefailtValue() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(2).getAnnotationDefault();
        String[] primitiveType = defaultValue.getPrimitiveType();
        assertNull(primitiveType);
        String[] constant = defaultValue.getEnumConstant();
        assertEquals(2, constant.length);
        assertEquals("Lorg/exoplatform/ide/codeassistant/asm/test/E;", constant[0]);
        assertEquals("ONE", constant[1]);
        assertThat(defaultValue.getArrayType()).isNullOrEmpty();
    }

    @Test
    public void shouldExtractClassDefailtValue() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(3).getAnnotationDefault();
        assertEquals("Ljava/lang/String;", defaultValue.getClassSignature());
    }

    @Test
    public void shouldExtractStringArrayDefailtValue() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(4).getAnnotationDefault();
        String[] arrayType = defaultValue.getArrayType();
        assertThat(arrayType).isNotEmpty().containsOnly("String", "str");
    }

    @Test
    public void shouldExtractDoubleArrayDefailtValue() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(5).getAnnotationDefault();
        String[] arrayType = defaultValue.getArrayType();
        assertThat(arrayType).isNotEmpty().containsOnly("Double", "1.4", "2.04", "5.0007");
    }

    @Test
    public void shouldExtractClassArrayDefailtValue() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(6).getAnnotationDefault();
        String[] arrayType = defaultValue.getArrayType();
        assertThat(arrayType).isNotEmpty().containsOnly("Type", "Ljava/lang/Integer;", "Ljava/util/List;",
                                                        "Ljava/lang/Math;");
    }

    @Test
    public void shouldExtractAnnotationDefailtValue() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(7).getAnnotationDefault();
        Annotation annotation = defaultValue.getAnnotation();
        assertThat(annotation).isNotNull();
        assertThat(annotation.getTypeName()).isEqualTo("Lorg/exoplatform/ide/codeassistant/asm/test/Foo;");
        assertThat(annotation.getAnnotationParameters()).hasSize(2);
    }

    @Test
    public void shouldExtractAnnotationPrimitiveParameter() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(7).getAnnotationDefault();
        Annotation annotation = defaultValue.getAnnotation();
        AnnotationParameter parameter = annotation.getAnnotationParameters()[0];
        assertThat(parameter.getName()).isEqualTo("foo");
        assertThat(parameter.getValue().getPrimitiveType()).containsOnly("Integer", "5");
    }

    @Test
    public void shouldExtractAnnotationArrayParameter() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(7).getAnnotationDefault();
        Annotation annotation = defaultValue.getAnnotation();
        AnnotationParameter parameter = annotation.getAnnotationParameters()[1];
        assertThat(parameter.getName()).isEqualTo("bar");
        assertThat(parameter.getValue().getArrayType()).containsOnly("String", "aaa", "bbb");
    }

    @Test
    public void shouldChecIfDefaultParameterHasDefaultValue() throws Exception {
        TypeInfo cd = ClassParser.parse(AnnotationWithAnnotationParameter.class);
        AnnotationValue defaultValue = cd.getMethods().get(0).getAnnotationDefault();
        Annotation annotation = defaultValue.getAnnotation();
        assertThat(annotation.getAnnotationParameters()).isEmpty();

    }

    @Test
    public void shouldExtractAnnotationArrayDefaultParameter() throws Exception {
        TypeInfo cd = ClassParser.parse(Bar.class);
        AnnotationValue defaultValue = cd.getMethods().get(8).getAnnotationDefault();
        Annotation[] annotations = defaultValue.getAnnotations();
        assertThat(annotations).hasSize(2);
        assertThat(defaultValue.getArrayType()).isNullOrEmpty();
        Annotation annotation = annotations[1];
        AnnotationParameter parameter = annotation.getAnnotationParameters()[0];
        assertThat(parameter.getName()).isEqualTo("foo");
        assertThat(parameter.getValue().getPrimitiveType()).containsOnly("Integer", "10");
    }

}
