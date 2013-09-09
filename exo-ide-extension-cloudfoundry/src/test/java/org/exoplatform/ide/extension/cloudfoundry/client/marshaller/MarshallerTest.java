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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

public class MarshallerTest {
    interface MyFactory extends AutoBeanFactory {
//      AutoBean<ISystemResources> systemResources();
//
//      AutoBean<ISystemInfo> systemInfo();
//
//      AutoBean<ITypeInfo> typeInfo();
//
//      AutoBean<IMethodInfo> methodInfo();

//      AutoBean<IFieldInfo> fieldInfo();
    }


    @Test
    @Ignore
    public void testName2() throws Exception {
        MyFactory factory = AutoBeanFactorySource.create(MyFactory.class);

        IFieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setDeclaringClass("declaringClass");
        fieldInfo.setModifiers(10);
        fieldInfo.setName("name");
        fieldInfo.setType("type");

        AutoBean<IFieldInfo> fib = factory.create(IFieldInfo.class, fieldInfo);
        System.out.println("MarshallerTest.testName2()" + AutoBeanCodex.encode(fib).getPayload());


        IMethodInfo methodInfo = new MethodInfo();
        methodInfo.setDeclaringClass("declaringClass");
        methodInfo.setGeneric("generic");
        List<String> ss = new ArrayList<String>();
        ss.add("ffff");
        methodInfo.setGenericExceptionTypes(ss);
        methodInfo.setGenericParameterTypes("genericParameterTypes");
        methodInfo.setGenericReturnType("genericReturnType");
        methodInfo.setParameterTypes("parameterTypes");
        methodInfo.setReturnType("returnType");

        AutoBean<IMethodInfo> mi = factory.create(IMethodInfo.class, methodInfo);

//      System.out.println("MarshallerTest.testName2()" + AutoBeanCodex.encode(mi).getPayload());

        AutoBean<IMethodInfo> mid =
                AutoBeanCodex
                        .decode(
                                factory,
                                IMethodInfo.class,
                                "{\"generic\":\"generic\",\"declaringClass\":\"declaringClass\"," +
                                "\"genericParameterTypes\":\"genericParameterTypes\",\"returnType\":\"returnType\"," +
                                "\"parameterTypes\":\"parameterTypes\",\"genericReturnType\":\"genericReturnType\"}\"");


        IFieldInfo fi = new FieldInfo("type", 10, "name", "declaringClass");
        List<IFieldInfo> fis = new ArrayList<IFieldInfo>();
        fis.add(fieldInfo);

        ITypeInfo ti = new TypeInfo();
        List<IRoutineInfo> ris = new ArrayList<IRoutineInfo>();
        ris.add(methodInfo);
        List<IMethodInfo> mis = new ArrayList<IMethodInfo>();
        mis.add(methodInfo);


        ti.setConstructors(ris);
        ti.setDeclaredConstructors(ris);
        ti.setDeclaredFields(fis);
        ti.setFields(fis);
        ti.setDeclaredMethods(mis);
        ti.setInterfaces(ss);
        ti.setModifiers(10);
        ti.setName("name");
        ti.setQualifiedName("qualifiedName");
        ti.setSuperClass("superClass");
        ti.setType("type");
        ti.setMethods(mis);

        AutoBean<ITypeInfo> tiBean = factory.create(ITypeInfo.class, ti);
        System.out.println("MarshallerTest.testName2()" + AutoBeanCodex.encode(tiBean).getPayload());


        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("json.json");
        byte[] b = new byte[stream.available()];
        stream.read(b);
        String s = new String(b);

        System.out.println("MarshallerTest.testName2()" + s);
        AutoBean<ITypeInfo> decode = AutoBeanCodex.decode(factory, ITypeInfo.class, s);
        ITypeInfo ti2 = decode.as();
        System.err.println(ti2.getName());
    }
}
