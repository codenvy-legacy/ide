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
package org.exoplatform.ide.client.model.discovery.marshal;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;
import org.exoplatform.ide.client.framework.discovery.event.EntryPointsReceivedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class EntryPointListUnmarshaller implements Unmarshallable
{
   
   private EntryPointsReceivedEvent event;
   
   public EntryPointListUnmarshaller(EntryPointsReceivedEvent event)
   {
      this.event = event;
   }

   public void unmarshal(Response response) throws UnmarshallerException
   {
      List<EntryPoint> entryPointList = new ArrayList<EntryPoint>();
      
      JavaScriptObject json = build(response.getText());
      JSONArray jsonArray = new JSONArray(json);
      
      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONValue value = jsonArray.get(i);
         entryPointList.add(EntryPoint.build(value.toString()));
         
      }
      event.setEntryPointList(entryPointList);
   }
   
   public static native JavaScriptObject build(String json) /*-{
         return eval('(' + json + ')');      
      }-*/;

}
