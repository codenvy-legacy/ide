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
package org.exoplatform.ide.client.model.template;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;

import java.util.List;

/**
 * Unmarshaller for default templates.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TemplateListUnmarshaller , 11.04.2011 17:52:00 vereshchaka $
 * 
 */
public class DefaultTemplatesUnmarshaller implements Unmarshallable<List<TemplateNative>>
{

   private List<TemplateNative> templateList;

   public DefaultTemplatesUnmarshaller(List<TemplateNative> templateList)
   {
      this.templateList = templateList;
   }

   public void unmarshal(Response response) throws UnmarshallerException
   {
      JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();
      if (jsonArray == null)
      {
         return;
      }

      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONValue value = jsonArray.get(i);
         templateList.add(TemplateNative.build(value.toString()));
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public List<TemplateNative> getPayload()
   {
      return templateList;
   }

}
