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
package org.exoplatform.gwtframework.commons.rest;

import org.junit.Test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Random;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class AutoBeanUnmarshallerTest extends GWTTestCase
{
   
   private MyFactory factory;
   
   private String clazz =
            "{\"name\":\"java.lang.String\",\"modifiers\":4104,\"type\":\"CLASS\"}";
   
   private String classes =
            "[{\"name\":\"java.lang.String$1\",\"modifiers\":4104,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringCoding$1\",\"modifiers\":4104,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringValue\",\"modifiers\":0,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringIndexOutOfBoundsException\",\"modifiers\":1,\"type\":\"CLASS\"},{\"name\":\"java.lang.StackTraceElement\",\"modifiers\":17,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringBuffer\",\"modifiers\":17,\"type\":\"CLASS\"},{\"name\":\"java.lang.StackOverflowError\",\"modifiers\":1,\"type\":\"CLASS\"},{\"name\":\"java.lang.StrictMath\",\"modifiers\":17,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringCoding$StringEncoder\",\"modifiers\":10,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringBuilder\",\"modifiers\":17,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringCoding$StringDecoder\",\"modifiers\":10,\"type\":\"CLASS\"},{\"name\":\"java.lang.StringCoding\",\"modifiers\":0,\"type\":\"CLASS\"},{\"name\":\"java.lang.String\",\"modifiers\":17,\"type\":\"CLASS\"},{\"name\":\"java.lang.String$CaseInsensitiveComparator\",\"modifiers\":10,\"type\":\"CLASS\"},{\"name\":\"java.util.logging.StreamHandler\",\"modifiers\":1,\"type\":\"CLASS\"},{\"name\":\"java.util.StringTokenizer\",\"modifiers\":1,\"type\":\"CLASS\"},{\"name\":\"java.util.Stack\",\"modifiers\":1,\"type\":\"CLASS\"}]";


   @Test
   public void testUnmarshallerEmptyBean() throws Exception
   {
      AutoBean<ShortTypeInfo> ab = factory.create(ShortTypeInfo.class);
      AutoBeanUnmarshaller<ShortTypeInfo> unmarshaller = new AutoBeanUnmarshaller<ShortTypeInfo>(ab);
      MockResponse response = new MockResponse(clazz);
      unmarshaller.unmarshal(response);
      assertEquals("java.lang.String", ab.as().getName());
      assertEquals(4104, ab.as().getModifiers());
      assertEquals("CLASS", ab.as().getType());
      assertNull(ab.as().getDesc());
   }
   
   @Test
   public void testUnmarshallerPrepareBean() throws Exception
   {
      AutoBean<ShortTypeInfo> ab = factory.type();
      String str = String.valueOf(Random.nextDouble());
      ab.as().setDesc(str);
      AutoBeanUnmarshaller<ShortTypeInfo> unmarshaller = new AutoBeanUnmarshaller<ShortTypeInfo>(ab);
      MockResponse response = new MockResponse(clazz);
      unmarshaller.unmarshal(response);
      assertEquals("java.lang.String", ab.as().getName());
      assertEquals(4104, ab.as().getModifiers());
      assertEquals("CLASS", ab.as().getType());
      assertEquals(str, ab.as().getDesc());
   }
   
   @Test
   public void testUnmarshallerImplBean() throws Exception
   {
      Impl impl = new Impl("type", 1024, "name", "desc");
      AutoBean<ShortTypeInfo> ab = factory.type(impl);
      assertEquals(ab.as().getDesc(), "desc");
      String json = AutoBeanCodex.encode(ab).getPayload();
      AutoBean<ShortTypeInfo> decoded = AutoBeanCodex.decode(factory, ab.getType(), json);
      assertTrue(AutoBeanUtils.deepEquals(ab, decoded));
     
      //rewrite existing information in bean
      AutoBeanUnmarshaller<ShortTypeInfo> unmarshaller = new AutoBeanUnmarshaller<ShortTypeInfo>(decoded);
      MockResponse response = new MockResponse(clazz);
      unmarshaller.unmarshal(response);
      assertEquals("java.lang.String", decoded.as().getName());
      assertEquals(4104, decoded.as().getModifiers());
      assertEquals("CLASS", decoded.as().getType());
      assertEquals("desc", decoded.as().getDesc());//from impl class
   }
   
   
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.gwtframework.commons.Commons";
   }
   
   interface MyFactory extends AutoBeanFactory
   {
      AutoBean<ShortTypeInfo> type();
      AutoBean<ShortTypeInfo> type(Impl impl);
   }
   
   @Override
   protected void gwtSetUp() throws Exception
   {
      factory = GWT.create(MyFactory.class);
   }
   
   
   public class MockResponse extends Response
   {

      int status;

      String text;

      /**
       * @param status
       * @param text
       */
      public MockResponse(String text)
      {
         this.status = 200;
         this.text = text;
      }

//      public MockResponce()
//      {
//         status = 200;
//      }

      @Override
      public String getHeader(String arg0)
      {
         return null;
      }

      @Override
      public Header[] getHeaders()
      {
         return null;
      }

      @Override
      public String getHeadersAsString()
      {
         return null;
      }

      @Override
      public int getStatusCode()
      {
         return status;
      }

      @Override
      public String getStatusText()
      {
         return null;
      }

      @Override
      public String getText()
      {
         return text;
      }

   }

   public interface ShortTypeInfo
   {

      String getType();

      void setType(String type);

      /**
       * @return the modifiers
       */
      int getModifiers();

      /**
       * @return the name
       */
      String getName();

      /**
       * @param modifiers
       *           the modifiers to set
       */
      void setModifiers(int modifiers);

      /**
       * @param name
       *           the name to set
       */
      void setName(String name);
      
      
      String getDesc();
      
      
      void setDesc(String desc);

   }
   
   
   public class Impl implements ShortTypeInfo
   {
      private String type;
      
      private int modifiers;
      
      private String name;
      
      private String desc;
      
      

      public Impl(String type, int modifiers, String name, String desc)
      {
         this.type = type;
         this.modifiers = modifiers;
         this.name = name;
         this.desc = desc;
      }

      public String getType()
      {
         return type;
      }

      public void setType(String type)
      {
         this.type = type;
      }

      public int getModifiers()
      {
         return modifiers;
      }

      public String getName()
      {
         return name;
      }

      public void setModifiers(int modifiers)
      {
         this.modifiers = modifiers;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public String getDesc()
      {
         return desc;
      }

      public void setDesc(String desc)
      {
         this.desc = desc;
      }
      
   }
   
}
