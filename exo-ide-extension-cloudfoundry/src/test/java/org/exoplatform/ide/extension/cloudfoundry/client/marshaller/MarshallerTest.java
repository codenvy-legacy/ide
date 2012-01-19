/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

public class MarshallerTest
{
   interface MyFactory extends AutoBeanFactory
   {
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
   public void testName2() throws Exception
   {
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
               "{\"generic\":\"generic\",\"declaringClass\":\"declaringClass\",\"genericParameterTypes\":\"genericParameterTypes\",\"returnType\":\"returnType\",\"parameterTypes\":\"parameterTypes\",\"genericReturnType\":\"genericReturnType\"}\"");
      
     
      
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
