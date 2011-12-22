/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.jvm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Modifier;

/**
 * 
 * Member is reflects identifying information about a single member (a field or
 * a method) or a constructor.
 * 
 * @see TypeInfo
 * @see FieldInfo
 * @see MethodInfo
 * 
 *      Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public abstract class Member implements Externalizable
{

   protected Integer modifiers;

   protected String name;

   public Member()
   {
   }

   public Member(Integer modifiers, String name)
   {
      this.modifiers = modifiers;
      this.name = name;
   }

   /**
    * @return the modifiers
    */
   public Integer getModifiers()
   {
      return modifiers;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param modifiers
    *           the modifiers to set
    */
   public void setModifiers(Integer modifiers)
   {
      this.modifiers = modifiers;
   }

   /**
    * @param name
    *           the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Return a string describing the access modifier flags in the specified
    * modifier. For example: <blockquote>
    * 
    * <pre>
    *    public final synchronized strictfp
    * </pre>
    * 
    * </blockquote> The modifier names are returned in an order consistent with
    * the suggested modifier orderings given in <a href=
    * "http://java.sun.com/docs/books/jls/second_edition/html/j.title.doc.html">
    * <em>The
    * Java Language Specification, Second Edition</em></a> sections <a href=
    * "http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html#21613"
    * >&sect;8.1.1</a>, <a href=
    * "http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html#78091"
    * >&sect;8.3.1</a>, <a href=
    * "http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html#78188"
    * >&sect;8.4.3</a>, <a href=
    * "http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html#42018"
    * >&sect;8.8.3</a>, and <a href=
    * "http://java.sun.com/docs/books/jls/second_edition/html/interfaces.doc.html#235947"
    * >&sect;9.1.1</a>. The full modifier ordering used by this method is:
    * <blockquote> <code> 
    * public protected private abstract static final transient
    * volatile synchronized native strictfp
    * interface </code> </blockquote> The <code>interface</code> modifier
    * discussed in this class is not a true modifier in the Java language and it
    * appears after all other modifiers listed by this method. This method may
    * return a string of modifiers that are not valid modifiers of a Java
    * entity; in other words, no checking is done on the possible validity of
    * the combination of modifiers represented by the input.
    * 
    * @return a string representation of the set of modifiers represented by
    *         <code>modifiers</code>
    */
   public String modifierToString()
   {
      StringBuffer sb = new StringBuffer();
      int len;

      if ((modifiers & Modifier.PUBLIC) != 0)
      {
         sb.append("public ");
      }
      if ((modifiers & Modifier.PROTECTED) != 0)
      {
         sb.append("protected ");
      }
      if ((modifiers & Modifier.PRIVATE) != 0)
      {
         sb.append("private ");
      }

      /* Canonical order */
      if ((modifiers & Modifier.ABSTRACT) != 0)
      {
         sb.append("abstract ");
      }
      if ((modifiers & Modifier.STATIC) != 0)
      {
         sb.append("static ");
      }
      if ((modifiers & Modifier.FINAL) != 0)
      {
         sb.append("final ");
      }
      if ((modifiers & Modifier.TRANSIENT) != 0)
      {
         sb.append("transient ");
      }
      if ((modifiers & Modifier.VOLATILE) != 0)
      {
         sb.append("volatile ");
      }
      if ((modifiers & Modifier.SYNCHRONIZED) != 0)
      {
         sb.append("synchronized ");
      }
      if ((modifiers & Modifier.NATIVE) != 0)
      {
         sb.append("native ");
      }
      if ((modifiers & Modifier.STRICT) != 0)
      {
         sb.append("strictfp ");
      }
      if ((modifiers & Modifier.INTERFACE) != 0)
      {
         sb.append("interface ");
      }

      if ((len = sb.length()) > 0)
      {
         return sb.toString().substring(0, len - 1);
      }
      return "";
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      out.writeInt(modifiers);
      out.writeObject(name);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      modifiers = in.readInt();
      name = (String)in.readObject();
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return modifierToString() + " " + name;
   }

}
