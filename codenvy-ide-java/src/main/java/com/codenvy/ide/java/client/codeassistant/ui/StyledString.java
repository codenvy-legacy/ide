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
package com.codenvy.ide.java.client.codeassistant.ui;

import com.codenvy.ide.java.client.JavaClientBundle;


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

   public static Styler QUALIFIER_STYLER =  new DefaultStyler(JavaClientBundle.INSTANCE.css().fqnStyle());

   public static Styler COUNTER_STYLER = new DefaultStyler(JavaClientBundle.INSTANCE.css().counter());

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
    * @param cs
    */
   public StyledString(char[] cs)
   {
      this();
      append(cs);
   }

   /**
    * @param name
    * @param styler
    */
   public StyledString(String name, Styler styler)
   {
      this();
      append(name, styler);
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
      builder.append(htmlEncode(new String(completion)));
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

   /**
    * HTML-encode a string. This simple method only replaces the five characters &, <, >, ", and '.
    * 
    * @param input the String to convert
    * @return a new String with HTML encoded characters
    */
   public static String htmlEncode(String input)
   {
      String output = input.replaceAll("&", "&amp;");
      output = output.replaceAll("<", "&lt;");
      output = output.replaceAll(">", "&gt;");
      output = output.replaceAll("\"", "&quot;");
      output = output.replaceAll("'", "&#039;");
      return output;
   }

}
