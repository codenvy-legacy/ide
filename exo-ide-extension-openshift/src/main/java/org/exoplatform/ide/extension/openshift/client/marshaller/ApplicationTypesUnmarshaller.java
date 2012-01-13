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
package org.exoplatform.ide.extension.openshift.client.marshaller;

import com.google.gwt.json.client.JSONArray;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;

import java.util.List;

/**
 * Unmarshaller for application types response.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 7, 2011 1:03:13 PM anya $
 *
 */
public class ApplicationTypesUnmarshaller implements Unmarshallable, Constants
{

   /**
    * List of application types.
    */
   private List<String> applicationTypes;

   /**
    * @param applicationTypes list of application types
    */
   public ApplicationTypesUnmarshaller(List<String> applicationTypes)
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
         throw new UnmarshallerException(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationTypesUnmarshallerFail());
      }
   }

}
