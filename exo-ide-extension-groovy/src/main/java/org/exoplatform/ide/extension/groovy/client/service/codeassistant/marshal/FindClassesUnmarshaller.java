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
package org.exoplatform.ide.extension.groovy.client.service.codeassistant.marshal;

import java.util.List;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtProperties;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtType;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 4:51:03 PM evgen $
 *
 */
public class FindClassesUnmarshaller implements Unmarshallable
{

   private static final String NAME = "name";

   private static final String FQN = "qualifiedName";

   private static final String MODIFIERS = "modifiers";

   private static final String TYPE = "type";

   private List<TokenExt> tokens;

   /**
    * @param tokens
    */
   public FindClassesUnmarshaller(List<TokenExt> tokens)
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

   private native JavaScriptObject getClasses(String json)/*-{
      return eval('(' + json + ')');
   }-*/;

   private void parseClassesName(String body)
   {
      //    JSONObject json = new JSONObject(getClasses(body));
      JSONArray jArray = new JSONArray(getClasses(body));

      for (int i = 0; i < jArray.size(); i++)
      {
         JSONObject jObject = jArray.get(i).isObject();
         //     GroovyToken token = new GroovyToken(jObject.get("CLASS").isString().stringValue(), GroovyTokenType.CLASS);

         TokenExt token =
            new TokenExt(jObject.get(NAME).isString().stringValue(), TokenExtType.valueOf(jObject.get(TYPE)
               .isString().stringValue()));

         for (String key : jObject.keySet())
         {
            if (key.equals(FQN))
            {
               token.setProperty(TokenExtProperties.FQN, jObject.get(key).isString().stringValue());
            }
            if (key.equals(MODIFIERS))
            {
               token.setProperty(TokenExtProperties.MODIFIERS, "" +jObject.get(key).isNumber().doubleValue());
            }
            
         }
         tokens.add(token);
      }

   }

}
