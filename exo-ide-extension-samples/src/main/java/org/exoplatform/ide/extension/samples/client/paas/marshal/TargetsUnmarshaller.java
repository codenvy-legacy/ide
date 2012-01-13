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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.List;

/**
 * Unmarshaller for the list of targets, received from server.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TargetsUnmarshaller.java Sep 21, 2011 5:34:51 PM vereshchaka $
 */
public class TargetsUnmarshaller implements Unmarshallable
{
   private List<String> targets;

   public TargetsUnmarshaller(List<String> targets)
   {
      this.targets = targets;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JavaScriptObject json = build(response.getText());
      JSONArray jsonArray = new JSONArray(json);

      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONValue value = jsonArray.get(i);
         if (value.isString() != null)
         {
            targets.add(value.isString().stringValue());
         }
      }
   }

   public static native JavaScriptObject build(String json) /*-{
                                                            return eval('(' + json + ')');      
                                                            }-*/;

}
