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
package org.exoplatform.ide.editor.codeassistant;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.codeassistant.css.CssCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.html.HtmlCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.javascript.JavaScriptCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.xml.XmlCodeAssistant;

/**
 * This factory use for autocompletion for default file type
 * (not extensions) (e.g HTML, XML, JavaScript, CSS)  
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: DefaultCodeAssistantFactory Mar 3, 2011 5:07:20 PM evgen $
 *
 */
public class CodeAssistantFactory
{
   private static Map<String, CodeAssistant> assistance = new HashMap<String, CodeAssistant>();

   private static CodeAssistant defaultCodeAssistant = new DefaultCodeAssistant();

   static
   {
      XmlCodeAssistant xmlCodeAssistant = new XmlCodeAssistant();
      assistance.put(MimeType.APPLICATION_XML, xmlCodeAssistant);
      assistance.put(MimeType.TEXT_XML, xmlCodeAssistant);
      
      JavaScriptCodeAssistant javaScriptCodeAssistant = new JavaScriptCodeAssistant();
      
      assistance.put(MimeType.APPLICATION_JAVASCRIPT, javaScriptCodeAssistant);
      assistance.put(MimeType.APPLICATION_X_JAVASCRIPT, javaScriptCodeAssistant);
      assistance.put(MimeType.TEXT_JAVASCRIPT, javaScriptCodeAssistant);
      
      CssCodeAssistant cssCodeAssistant = new CssCodeAssistant();
      assistance.put(MimeType.TEXT_CSS, cssCodeAssistant);
      
      HtmlCodeAssistant htmlCodeAssistant = new HtmlCodeAssistant();
      
      assistance.put(MimeType.TEXT_HTML, htmlCodeAssistant);      
   }

   public static CodeAssistant getCodeAssistant(String mimeType)
   {
      if (assistance.containsKey(mimeType))
         return assistance.get(mimeType);
      else
         return defaultCodeAssistant;
   }
}
