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
package org.eclipse.jdt.client.codeassistant.ui;

import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:02:23 PM 34360 2009-07-22 23:58:59Z evgen $
 * 
 */
public class StyledString
{

   public static abstract class Styler
   {

      public abstract String applyStyles(String text);

   }

   public static Styler QUALIFIER_STYLER = new QualifierStyler();

   private StringBuilder builder;

   /**
    * @param string
    */
   public StyledString(String string)
   {
      this();
      builder.append(string);
   }

   /**
    * 
    */
   public StyledString()
   {
      builder = new StringBuilder();
   }

   /**
    * @return
    */
   public String getString()
   {
      return builder.toString();
   }

   /**
    * @param completion
    */
   public void append(char[] completion)
   {
      builder.append(completion);
   }

   /**
    * @param c
    */
   public void append(char c)
   {
      builder.append(c);
   }

   /**
    * @param returnTypeSeparator
    */
   public void append(String returnTypeSeparator)
   {
      builder.append(returnTypeSeparator);
   }

   /**
    * @param qualifier
    */
   public void append(String qualifier, Styler styler)
   {
      builder.append(styler.applyStyles(qualifier));
   }

   /**
    * @param c
    */
   public void append(char c, Styler styler)
   {
      builder.append(styler.applyStyles(String.valueOf(c)));
   }

   /**
    * @param declaration
    */
   public void append(char[] declaration, Styler styler)
   {
      builder.append(styler.applyStyles(new String(declaration)));
   }

   private static class QualifierStyler extends Styler
   {

      /**
       * @see org.eclipse.jdt.client.codeassistant.ui.StyledString.Styler#applyStyles(java.lang.String)
       */
      @Override
      public String applyStyles(String text)
      {
         StringBuilder b = new StringBuilder();
         b.append("<span ").append("class=\"").append(JavaClientBundle.INSTANCE.css().fqnStyle()).append("\">");
         b.append(text).append("</span>");
         return b.toString();
      }

   }

}
