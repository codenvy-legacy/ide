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
package org.exoplatform.ide.git.client.service;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.git.client.GitClientUtil;
import org.exoplatform.ide.git.client.service.marshaller.CloneRequestMarshaller;
import org.exoplatform.ide.git.client.service.marshaller.InitRequestMarshaller;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.InitRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 23, 2011 11:52:24 AM anya $
 *
 */
public class GitClientServiceImpl extends GitClientService
{
   public static final String INIT= "/ide/git/init";
   
   public static final String CLONE = "/ide/git/clone";
   
   private HandlerManager eventBus;

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;
   
   
   public GitClientServiceImpl(HandlerManager eventBus, String restContext, Loader loader)
   {
      this.loader = loader;
      this.eventBus = eventBus;
      this.restServiceContext = restContext;
   }
   
   /**
    * @see org.exoplatform.ide.git.client.service.GitClientService#init(java.lang.String, boolean)
    */
   @Override
   public void init(String href, boolean bare, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + INIT;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);

      InitRequest initRequest = new InitRequest(workDir, bare, null);
      InitRequestMarshaller marshaller = new InitRequestMarshaller(initRequest);
      
      String params = "workdir="+workDir;
      
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.git.client.service.GitClientService#cloneRepository(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void cloneRepository(String href, String remoteUri, String remoteName, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + CLONE;
      String workDir = GitClientUtil.getWorkingDirFromHref(href, restServiceContext);
      callback.setEventBus(eventBus);
      CloneRequest cloneRequest = new CloneRequest(remoteUri, workDir, null);
      CloneRequestMarshaller marshaller = new CloneRequestMarshaller(cloneRequest);
      
      String params = "workdir="+workDir;
      
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).data(marshaller).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

}
