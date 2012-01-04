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
package test.javadoc;

import java.util.List;

/**
 * <p>
 * Class java doc<br>
 * with tags and<br>
 * few lines.
 * </p>
 */
public class JavaDocClass
{

   public String fieldWithoutJavaDoc;

   /**
    * Field java doc
    */
   public String field;

   /**
    * Constructor java doc with parameters
    */
   public JavaDocClass(int p1, Integer p2)
   {
   }

   public JavaDocClass()
   {
   }

   /**
    * Method java doc
    */
   public void method()
   {
   }

   /**
    * Method with primitive param
    */
   public void method(int p1)
   {
   }

   /**
    * Method with object param
    */
   public void method(Double p1)
   {
   }

   /**
    * Method with primitive and object params
    */
   public void method(int p1, Double p2)
   {
   }

   public void methodWithoutJavaDocs(Object p1)
   {
   }

   /**
    * Private class with java doc
    */
   private class PrivateClass
   {

      /**
       * Constructor of private class
       */
      public PrivateClass()
      {
      }

      /**
       * Method of private class
       */
      public void method()
      {
      }

   }

   private class ClassWithoutJavadoc
   {
      
      /**
       * Method with java docs in uncommented class
       */
      public void method()
      {
      }
      
   }

}

/**
 * Second private class
 */
class PrivateClass
{

}

/**
 * Class with generics
 */
class ClassWithGenerics<T extends Number>
{

   /**
    * Field with generics
    */
   T genericField;

   /**
    * Method with generics
    */
   public T method(T p1)
   {
      return null;
   }
   
   /**
    * Method with list as parameter
    */
   public T method(List<? extends Number> p1)
   {
      return null;
   }

}
