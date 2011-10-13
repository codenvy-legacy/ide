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
package org.exoplatform.ide.extension.ruby.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.safehtml.shared.UriUtils;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RubyService
{
   private static final String BASE_URL = "/ide/application/ruby";

   private static final String CREATE_PROJECT = BASE_URL + "/create";

   /**
    * REST service context.
    */
   private String restContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   private static RubyService instance;
   
   public static RubyService get()
   {
      return instance;
   }
   
   /**
    * @param restContext
    * @param loader
    */
   public RubyService(String restContext, Loader loader)
   {
      super();
      this.restContext = restContext;
      this.loader = loader;
      instance = this;
   }

   public void createProject(String name, String parentId, String vfsId, AsyncRequestCallback<?> callback)
   {
      
      final String url = restContext + CREATE_PROJECT;
      String params = "name=" + name + "&";
      params += "parentId=" + parentId + "&";
      params += "vfsid=" + vfsId;
      callback.setEventBus(IDE.EVENT_BUS);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).send(callback);
   }
}
