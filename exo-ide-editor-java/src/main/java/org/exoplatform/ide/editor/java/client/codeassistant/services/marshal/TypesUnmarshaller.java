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
package org.exoplatform.ide.editor.java.client.codeassistant.services.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.editor.java.client.model.ShortTypeInfo;
import org.exoplatform.ide.editor.java.client.model.Types;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 5, 2011 12:06:38 PM evgen $
 *
 */
public class TypesUnmarshaller implements Unmarshallable
{

   private static final String NAME = "name";

   private static final String FQN = "qualifiedName";

   private static final String MODIFIERS = "modifiers";

   private static final String TYPE = "type";

   private List<ShortTypeInfo> types;

   /**
    * @param types
    */
   public TypesUnmarshaller(List<ShortTypeInfo> types)
   {
      super();
      this.types = types;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         doParse(response.getText());
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse classes names.");
      }
   }

   private void doParse(String body)
   {
      JSONArray jArray = JSONParser.parseLenient(body).isArray();

      for (int i = 0; i < jArray.size(); i++)
      {
         JSONObject jObject = jArray.get(i).isObject();
         ShortTypeInfo info = new ShortTypeInfo();
         info.setName(jObject.get(NAME).isString().stringValue());
         info.setType(Types.valueOf(jObject.get(TYPE).isString().stringValue()));

         for (String key : jObject.keySet())
         {
            if (key.equals(FQN))
            {
               info.setQualifiedName(jObject.get(key).isString().stringValue());
            }
            if (key.equals(MODIFIERS))
            {
               info.setModifiers(new Integer((int)jObject.get(key).isNumber().doubleValue()));
            }

         }
         types.add(info);
      }
   }
}
