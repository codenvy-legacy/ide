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
package org.exoplatform.ide.editor.java.client.codeassistant.services.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.ide.editor.api.codeassitant.NumericProperty;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 4:51:03 PM evgen $
 * 
 */
public class FindClassesUnmarshaller implements Unmarshallable<List<Token>>
{

   private static final String NAME = "name";

   private static final String MODIFIERS = "modifiers";

   private static final String TYPE = "type";

   private List<Token> tokens;

   /**
    * @param tokens
    */
   public FindClassesUnmarshaller(List<Token> tokens)
   {
      this.tokens = tokens;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseClassesName(response.getText());
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse classes names.");
      }

   }

   private void parseClassesName(String body)
   {
      JSONArray jArray = JSONParser.parseLenient(body).isArray();
      for (int i = 0; i < jArray.size(); i++)
      {
         JSONObject jObject = jArray.get(i).isObject();
         if (jObject.containsKey(NAME) && jObject.containsKey(MODIFIERS) && jObject.containsKey(TYPE))
         {
            String fqn = jObject.get(NAME).isString().stringValue();
            String name = fqn.substring(fqn.lastIndexOf(".") + 1);
            String type = jObject.get(TYPE).isString().stringValue();
            double modifiers = (int)jObject.get(MODIFIERS).isNumber().doubleValue();
            Token token = new TokenImpl(name, TokenType.valueOf(type));
            token.setProperty(TokenProperties.FQN, new StringProperty(fqn));
            token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifiers));
            tokens.add(token);
         }
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public List<Token> getPayload()
   {
      return tokens;
   }
}
