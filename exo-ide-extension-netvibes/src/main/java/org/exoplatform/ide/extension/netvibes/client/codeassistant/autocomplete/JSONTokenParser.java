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
package org.exoplatform.ide.extension.netvibes.client.codeassistant.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

/**
 * Token parser for JSOT tokens.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JSONTokenParser.java Jan 24, 2011 10:37:59 AM vereshchaka $
 *
 */
public class JSONTokenParser
{

   private interface TokenFields
   {
      public static final String NAME = "name";

      public static final String TYPE = "type";

      public static final String LINE_NUMBER = "lineNumber";

      public static final String SUB_TOKEN_LIST = "subTokenList";

      public static final String SHORT_DECRIPTION = "shortDescription";

      public static final String CODE = "code";

      public static final String FULL_DESCRIPTION = "fullDescription";

      public static final String MIME_TYPE = "mimeType";

      public static final String FQN = "fqn";
      
      public static final String VARTYPE = "varType";
   }

   public List<Token> getTokens(JSONArray json)
   {
      List<Token> tokens = new ArrayList<Token>();
      for (int i = 0; i < json.size(); i++)
      {
         JSONObject jObject = json.get(i).isObject();
         Token t =
            new Token(jObject.get(TokenFields.NAME).isString().stringValue(), TokenType.valueOf(jObject
               .get(TokenFields.TYPE).isString().stringValue()));

         if (jObject.get(TokenFields.LINE_NUMBER) != null)
         {

            t.setLineNumber(Integer.parseInt(jObject.get(TokenFields.LINE_NUMBER).isNumber().toString()));
         }

         if (jObject.get(TokenFields.SHORT_DECRIPTION) != null)
         {
            t.setShortDescription(jObject.get(TokenFields.SHORT_DECRIPTION).isString().stringValue());
         }

         if (jObject.get(TokenFields.CODE) != null)
         {
            t.setCode(jObject.get(TokenFields.CODE).isString().stringValue());
         }
         if (jObject.get(TokenFields.FULL_DESCRIPTION) != null)
         {
            t.setFullDescription(jObject.get(TokenFields.FULL_DESCRIPTION).isString().stringValue());
         }
         if (jObject.get(TokenFields.MIME_TYPE) != null)
         {
            t.setMimeType(jObject.get(TokenFields.MIME_TYPE).isString().stringValue());
         }
         if (jObject.get(TokenFields.FQN) != null)
         {
            t.setFqn(jObject.get(TokenFields.FQN).isString().stringValue());
         }

         if (jObject.get(TokenFields.SUB_TOKEN_LIST) != null)
         {
            t.setSubTokenList(getTokens(jObject.get(TokenFields.SUB_TOKEN_LIST).isArray()));
         }
         if (jObject.get(TokenFields.VARTYPE) != null)
         {
            t.setElementType(jObject.get(TokenFields.VARTYPE).isString().stringValue());
         }

         tokens.add(t);
      }

      return tokens;
   }

}
