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
package org.exoplatform.ide.extension.ssh.client;

import com.google.gwt.user.client.Window;

import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.ssh.client.marshaller.SshKeysUnmarshaller;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshService May 18, 2011 4:49:49 PM evgen $
 *
 */
public class SshService
{

   private static SshService instance;

   private String restContext;

   private Loader loader;

   /**
    * 
    */
   public SshService(String restContext, Loader loader)
   {
      this.restContext = restContext;
      this.loader = loader;
      instance = this;
   }

   public static SshService get()
   {
      return instance;
   }

   public void getAllKeys(AsyncRequestCallback<List<KeyItem>> callback)
   {
      try
      {
         List<KeyItem> keyItems = new ArrayList<KeyItem>();

         callback.setEventBus(IDE.EVENT_BUS);
         callback.setResult(keyItems);
         SshKeysUnmarshaller unmarshaller = new SshKeysUnmarshaller(keyItems);
         callback.setPayload(unmarshaller);
         String url = restContext + "/ide/ssh-keys/all";
         AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
      }
      catch (Throwable e)
      {
         Window.alert(e.getMessage());
      }
   }

}
