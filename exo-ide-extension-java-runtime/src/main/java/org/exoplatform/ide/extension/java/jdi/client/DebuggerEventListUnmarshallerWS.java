/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulResponseMessage;
import org.exoplatform.ide.client.framework.websocket.messages.Unmarshallable;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEventList;
import org.exoplatform.ide.extension.java.jdi.shared.StepEvent;

import java.util.ArrayList;

/**
 * Unmarshaller for deserialize debugger event list, which is received over WebSocket connection.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: DebuggerEventListUnmarshallerWS.java Nov 19, 2012 12:42:58 PM azatsarynnyy $
 *
 */
public class DebuggerEventListUnmarshallerWS implements Unmarshallable<DebuggerEventList>
{
   private DebuggerEventList events;

   public DebuggerEventListUnmarshallerWS(DebuggerEventList events)
   {
      this.events = events;
      if (this.events.getEvents() == null)
         this.events.setEvents(new ArrayList<DebuggerEvent>());
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.messages.Unmarshallable#unmarshal(org.exoplatform.ide.client.framework.websocket.messages.RESTfulResponseMessage)
    */
   @Override
   public void unmarshal(RESTfulResponseMessage response) throws UnmarshallerException
   {
      JSONObject jObj = JSONParser.parseStrict(response.getBody()).isObject();
      if (jObj == null)
      {
         return;
      }

      if (jObj.containsKey("events"))
      {
         JSONArray jEvent = jObj.get("events").isArray();
         for (int i = 0; i < jEvent.size(); i++)
         {
            JSONObject je = jEvent.get(i).isObject();
            if (je.containsKey("type"))
            {
               int type = (int)je.get("type").isNumber().doubleValue();
               if (type == DebuggerEvent.BREAKPOINT)
               {
                  AutoBean<BreakPointEvent> bean = DebuggerExtension.AUTO_BEAN_FACTORY.breakPoinEvent();
                  Splittable data = StringQuoter.split(je.toString());
                  AutoBeanCodex.decodeInto(data, bean);
                  events.getEvents().add(bean.as());
               }
               else if (type == DebuggerEvent.STEP)
               {
                  AutoBean<StepEvent> bean = DebuggerExtension.AUTO_BEAN_FACTORY.stepEvent();
                  Splittable data = StringQuoter.split(je.toString());
                  AutoBeanCodex.decodeInto(data, bean);
                  events.getEvents().add(bean.as());
               }
            }
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.messages.Unmarshallable#getPayload()
    */
   @Override
   public DebuggerEventList getPayload()
   {
      return events;
   }

}
