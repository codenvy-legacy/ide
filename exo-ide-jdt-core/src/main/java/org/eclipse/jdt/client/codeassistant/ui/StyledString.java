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

   public static Styler QUALIFIER_STYLER = new DefaultStyler(JavaClientBundle.INSTANCE.css().fqnStyle());

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
