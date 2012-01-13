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
package org.exoplatform.ide.editor.api.codeassitant;

/**
 * Properties, that can be applied to {@link Token}
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:31:49 PM evgen $
 * 
 */
public interface TokenProperties
{

   String CLASS = "CLASS";

   String FQN = "FQN";

   String PACKAGE = "PACKAGE";

   String MODIFIERS = "MODIFIERS";

   String DECLARING_CLASS = "DECLARINGCLASS";

   String GENERIC_PARAMETER_TYPES = "GENERICPARAMETERTYPES";

   String GENERIC_RETURN_TYPE = "GENERICRETURNTYPE";

   String PARAMETER_TYPES = "PARAMETERTYPES";

   String RETURN_TYPE = "RETURNTYPE";

   /**
    * Code, that will be inserted if token selected. Used only for template tokens.
    */
   String CODE = "CODE";

   /**
    * Used for template tokens. Short hint, that will be displayed near name in autocomplete form. Can help user quickly to
    * identify the purpose of template.
    */
   String SHORT_HINT = "SHORT-HINT";

   /**
    * Used for template tokens. Full text of template, that will be inserted to code editor. Also, can be shown in javadoc window.
    */
   String FULL_TEXT = "FULL-TEXT";

   String LINE_NUMBER = "lineNumber";

   String MIME_TYPE = "mimeType";

   String SHORT_DESCRIPTION = "shortDescription";

   String FULL_DESCRIPTION = "fullDescription";

   /**
    * Used for sub token list property. Contains {@link ArrayProperty}
    */
   String SUB_TOKEN_LIST = "subTokenList";

   String ELEMENT_TYPE = "elementType";

   String LAST_LINE_NUMBER = "lastLineNumber";

   String ANNOTATIONS = "annotations";

   String PARENT_TOKEN = "parentToken";

   String PARAMETERS = "parameters";

   String INITIALIZATION_STATEMENT = "initializationStatement";

   String GENERIC_EXCEPTIONTYPES = "genericExceptionTypes";

}
