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

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperty;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.util.JSONTokenParser;
import org.exoplatform.ide.editor.codeassistant.xml.XmlCodeAssistant;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: XmlGwtTestCodeAssistant Mar 2, 2011 9:43:41 AM evgen $
 *
 */
public class XmlGwtTestCodeAssistant extends Base
{

   public void testXmlAssistant()
   {
      class XMLAssist extends XmlCodeAssistant
      {
         /**
          * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#openForm(int, int, java.util.List, org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory, org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler)
          */
         @Override
         protected void openForm(List<Token> tokens, TokenWidgetFactory factory,
            TokenSelectedHandler handler)
         {
           assertEquals(8, tokens.size());
           assertEquals("context-param", tokens.get(0).getName());
           assertEquals("display-name", tokens.get(1).getName());
           assertEquals("filter", tokens.get(2).getName());
           assertEquals("filter-class", tokens.get(3).getName());
           assertEquals("filter-name", tokens.get(4).getName());
           assertEquals("param-name", tokens.get(5).getName());
           assertEquals("param-value", tokens.get(6).getName());
           assertEquals("web-app", tokens.get(7).getName());
           
         }
      }
     JavaScriptObject o = parseJson(CodeAssistantTestBundle.INSTANCE.cssJSON().getText());
     JSONTokenParser parser = new JSONTokenParser();
     
     List<Token> tokens = parser.getTokens(new JSONArray(o));
     new XMLAssist().autocompleteCalled(null, MimeType.TEXT_XML, 0, 0, "", 1, 1, tokens, "", null);
   }
   
   private void printTokens(List<? extends Token> tokens, int i)
   {
      String spacer = "";
      for (int j = 0; j < i; j++)
      {
         spacer += "  ";
      }
      i++;
      for (Token t : tokens)
      {
         System.out.println(spacer + t.getName() + " " + t.getType());
         TokenProperty p = t.getProperty(TokenProperties.LAST_LINE_NUMBER);
         if (p != null && p.isNumericProperty() != null)
            System.out.println(spacer + p.isNumericProperty().numericValue());
         if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)
            && t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null)
         {
            printTokens(t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue(), i);
         }
      }
   }
  
}
