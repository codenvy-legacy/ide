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
package org.exoplatform.ide.codeassistant.storage;

import org.everrest.core.impl.provider.json.*;
import org.exoplatform.ide.codeassistant.jvm.bean.FieldInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.MethodInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/** This is a perf test */
public class JsonParserAndExternatializationComparing {
    private final TypeInfoBean[] typeInfos = generateTypeInfos();

    private final TypeInfoBean typeInfo = generateTypeInfo();

    private static final int OBJECTS_COUNT = 100000;

    @Test
    public void jsonSerialization() throws JsonException, IOException {
        long startTime = System.currentTimeMillis();
        for (TypeInfo typeInfo : typeInfos) {
            JsonGenerator.createJsonObject(typeInfo);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Serialization Json time " + (endTime - startTime));
    }

    @Test
    public void extSerialization() throws JsonException, IOException {
        long startTime = System.currentTimeMillis();
        for (TypeInfoBean typeInfo : typeInfos) {
            ExternalizationTools.externalize(typeInfo);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Serialization Externalizable time " + (endTime - startTime) + "\n");
    }

    @Test
    public void jsonDeserialization() throws JsonException, IOException, ClassNotFoundException {
        JsonValue jsonValue = JsonGenerator.createJsonObject(generateTypeInfo());
        byte[] jsonBytes = jsonValue.toString().getBytes();

        System.out.println("json size " + jsonBytes.length);

        int i = 0;
        long startTime = System.currentTimeMillis();
        while (i < OBJECTS_COUNT) {
            JsonParser jsonParser = new JsonParser();
            jsonParser.parse(new ByteArrayInputStream(jsonBytes));
            JsonValue jsonValue2 = jsonParser.getJsonObject();
            ObjectBuilder.createObject(TypeInfoBean.class, jsonValue2);

            i++;
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Deserialization Json time " + (endTime - startTime));
    }

    @Test
    public void extDeserialization() throws JsonException, IOException, ClassNotFoundException {

        byte[] extBytes = ExternalizationTools.externalize(typeInfo);

        System.out.println("Externalizable size " + extBytes.length);

        int i = 0;
        long startTime = System.currentTimeMillis();
        while (i < OBJECTS_COUNT) {
            TypeInfo typeInfo2 = ExternalizationTools.readExternal(new ByteArrayInputStream(extBytes));
            i++;
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Deserialization Externalizable time " + (endTime - startTime));
    }

    private TypeInfoBean[] generateTypeInfos() {
        TypeInfoBean[] typeInfos = new TypeInfoBean[OBJECTS_COUNT];
        TypeInfoBean typeInfo = generateTypeInfo();

        for (int i = 0; i < OBJECTS_COUNT; i++) {
            typeInfos[i] = typeInfo;
        }

        return typeInfos;
    }

    private TypeInfoBean generateTypeInfo() {
        TypeInfoBean typeInfo = new TypeInfoBean();

        typeInfo.setModifiers(Modifier.PUBLIC);
        typeInfo.setName("test.TestClass");
        typeInfo.setSuperClass("java.lang.Object");
        typeInfo.setType("CLASS");

        String[] interfaces = new String[]{"java.io.Serializable"};
        typeInfo.setInterfaces(Arrays.asList(interfaces));

        MethodInfoBean publicConstructor =
                new MethodInfoBean("test.TestClass", Modifier.PUBLIC, Arrays.asList(new String[]{"java.io.IOException",
                                                                                                 "java.lang.IllegalStateException"}),
                                   Arrays.asList(new String[]{"java.lang.Object", "Object"}),
                                   Arrays.asList(new String[]{"param1", "param2"}), true, "", "test.TestClass", "dummyDescriptor", null,
                                   null);
        MethodInfoBean protectedConstructor =
                new MethodInfoBean("test.TestClass", Modifier.PROTECTED, Arrays.asList(new String[]{"java.io.IOException"}),
                                   Arrays.asList(new String[]{"java.lang.String", "String"}),
                                   Arrays.asList(new String[]{"param1", "param2"}),
                                   true, "", "test.TestClass", "dummyDescriptor", null, null);

        MethodInfoBean publicMethod =
                new MethodInfoBean("method1", Modifier.PUBLIC, Arrays.asList(new String[]{"java.io.IOException"}),
                                   Arrays.asList(new String[]{"java.lang.Object", "Object"}),
                                   Arrays.asList(new String[]{"param1", "param2"}),
                                   false, "test.TestClass", "java.lang.Integer", "dummyDescriptor", null, null);
        MethodInfoBean privateMethod =
                new MethodInfoBean("method2", Modifier.PRIVATE, Arrays.asList(new String[]{"java.io.IOException"}),
                                   Arrays.asList(new String[]{"java.lang.String", "String"}),
                                   Arrays.asList(new String[]{"param1", "param2"}),
                                   false, "test.TestClass", "java.lang.Integer", "dummyDescriptor", null, null);
        typeInfo.setMethods(Arrays.asList(new MethodInfo[]{publicConstructor, protectedConstructor, publicMethod,
                                                           privateMethod}));

        FieldInfoBean publicField = new FieldInfoBean("field1", Modifier.PUBLIC, "test.TestClass", "String", "dummyDescriptor", null, null);
        FieldInfoBean privateField =
                new FieldInfoBean("field2", Modifier.PRIVATE, "test.TestClass", "Integer", "dummyDescriptor", null, null);
        typeInfo.setFields(Arrays.asList(new FieldInfo[]{publicField, privateField}));
        return typeInfo;
    }
}
