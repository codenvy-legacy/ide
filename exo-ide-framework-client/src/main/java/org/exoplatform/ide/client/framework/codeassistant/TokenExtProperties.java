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
package org.exoplatform.ide.client.framework.codeassistant;

/**
 * Properties, that can be applied to {@link TokenExt}
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:31:49 PM evgen $
 *
 */
public interface TokenExtProperties
{

   public static String CLASS = "CLASS";

   public static String FQN = "FQN";

   public static String PACKAGE = "PACKAGE";

   public static String MODIFIERS = "MODIFIERS";

   public static String DECLARINGCLASS = "DECLARINGCLASS";

   public static String GENERICPARAMETERTYPES = "GENERICPARAMETERTYPES";

   public static String TYPE = "TYPE";

   public static String GENERICRETURNTYPE = "GENERICRETURNTYPE";

   public static String PARAMETERTYPES = "PARAMETERTYPES";

   public static String RETURNTYPE = "RETURNTYPE";
   
   /**
    * Code, that will be inserted if token selected.
    * Used only for template tokens.
    */
   public static String CODE = "CODE";
   
   /**
    * Used for template tokens.
    * Short hint, that will be displayed near name in autocomplete form.
    * Can help user quickly to identify the purpose of template.
    */
   public static String SHORT_HINT =  "SHORT-HINT";
   
   /**
    * Used for template tokens.
    * Full text of template, that will be inserted to code editor.
    * Also, can be shown in javadoc window.
    */
   public static String FULL_TEXT =  "FULL-TEXT";
}
