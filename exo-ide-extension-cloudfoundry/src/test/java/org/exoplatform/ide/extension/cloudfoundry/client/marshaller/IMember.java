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

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public interface IMember
{

   /**
    * @return the modifiers
    */
   Integer getModifiers();

   /**
    * @return the name
    */
   String getName();

   /**
    * @param modifiers
    *           the modifiers to set
    */
   void setModifiers(Integer modifiers);

   /**
    * @param name
    *           the name to set
    */
   void setName(String name);

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
   String modifierToString();


}