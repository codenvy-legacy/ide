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
package org.exoplatform.ide.extension.samples.client.paas.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.List;

/**
 * Unmarshaller for type of application on OpenShift.
 * <p/>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: OpenShiftTypesUnmarshaller.java Nov 23, 2011 3:30:08 PM vereshchaka $
 */
public class OpenShiftTypesUnmarshaller implements Unmarshallable
{
   /**
    * List of application types.
    */
   private List<String> applicationTypes;
   
   public OpenShiftTypesUnmarshaller(List<String> applicationTypes)
   {
      this.applicationTypes = applicationTypes;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONValue json = JSONParser.parseStrict(response.getText());
         JSONArray array = json.isArray();
         if (array == null || array.size() <= 0)
         {
            return;
         }
         for (int i = 0; i < array.size(); i++)
         {
            String value = array.get(i).isString().stringValue();
            applicationTypes.add(value);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new UnmarshallerException("Can't parse application types on OpenShitf.");
      }
   }

}
