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
package com.codenvy.ide.websocket.rest;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.rest.HTTPStatus;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;


/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: AutoBeanUnmarshallerWS.java Nov 19, 2012 11:44:19 AM azatsarynnyy $
 *
 * @param <T>
 */
public class AutoBeanUnmarshallerWS<T> implements Unmarshallable<T>
{
   private AutoBean<T> bean;

   public AutoBeanUnmarshallerWS(AutoBean<T> autoBean)
   {
      this.bean = autoBean;
   }

   /**
    * @see com.codenvy.ide.client.framework.websocket.rest.Unmarshallable#unmarshal(com.codenvy.ide.client.framework.websocket.rest.ResponseMessage)
    */
   @Override
   public void unmarshal(ResponseMessage response) throws UnmarshallerException
   {
      if (response.getResponseCode() != HTTPStatus.NO_CONTENT && response.getBody() != null)
      {
         Splittable data = StringQuoter.split(response.getBody());
         AutoBeanCodex.decodeInto(data, bean);
      }
   }

   /**
    * @see com.codenvy.ide.client.framework.websocket.rest.Unmarshallable#getPayload()
    */
   @Override
   public T getPayload()
   {
      return bean.as();
   }
}
