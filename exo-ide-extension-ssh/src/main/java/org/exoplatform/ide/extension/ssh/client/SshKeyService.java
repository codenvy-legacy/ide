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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.ssh.client.marshaller.GenerateSshKeysMarshaller;
import org.exoplatform.ide.extension.ssh.shared.GenKeyRequest;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshService May 18, 2011 4:49:49 PM evgen $
 *
 */
public class SshKeyService
{

   private static SshKeyService instance;

   private final String restContext;

   private final Loader loader;

   //   private final int httpsPort;

   /**
    * 
    */
   public SshKeyService(String restContext, int httpsPort, Loader loader)
   {
      this.restContext = restContext;
      this.loader = loader;
      //    this.httpsPort = httpsPort;
      instance = this;
   }

   public static SshKeyService get()
   {
      return instance;
   }

   /**
    * Receive all ssh key, stored on server
    * @param callback
    */
   public void getAllKeys(JsonpAsyncCallback<JavaScriptObject> callback)
   {
      //      UrlBuilder builder = new UrlBuilder();
      //      String url =
      //         builder.setProtocol("https").setHost(Location.getHost()).setPort(httpsPort)
      //            .setPath(GWT.getModuleName()+ "/" + sslContext + "/ide/ssh-keys/all").buildString();

      JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
      loader.setMessage("Getting SSH keys....");
      loader.show();
      callback.setLoader(loader);
      jsonp.requestObject(restContext + "/ide/ssh-keys/all", callback);
   }

   /**
    * Generate new ssh key pare
    * @param host for ssh key
    * @param callback
    */
   public void generateKey(GenKeyRequest genKey, AsyncRequestCallback<GenKeyRequest> callback)
   {
      callback.setEventBus(IDE.eventBus());
      callback.setResult(genKey);
      String url = restContext + "/ide/ssh-keys/gen";
      GenerateSshKeysMarshaller marshaller = new GenerateSshKeysMarshaller(genKey);
      loader.setMessage("Generate keys for " + genKey.getHost());
      AsyncRequest.build(RequestBuilder.POST, url, loader).data(marshaller)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * Get public ssh key
    * @param keyItem to get public key
    * @param callback
    */
   public void getPublicKey(KeyItem keyItem, JsonpAsyncCallback<JavaScriptObject> callback)
   {
      JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
      loader.setMessage("Getting public SSH key for " + keyItem.getHost());
      loader.show();
      callback.setLoader(loader);
      jsonp.requestObject(keyItem.getPublicKeyURL(), callback);
   }

   /**
    * Delete ssh key
    * @param keyItem to delete
    * @param callback
    */
   public void deleteKey(KeyItem keyItem, JsonpAsyncCallback<Void> callback)
   {
      JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
      loader.setMessage("Deleting SSH keys for " + keyItem.getHost());
      loader.show();
      callback.setLoader(loader);
      jsonp.send(keyItem.getRemoveKeyURL(), callback);
   }

}
