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
package org.exoplatform.ide.extension.chromattic.client.model.service;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.extension.chromattic.client.model.EnumAlreadyExistsBehaviour;
import org.exoplatform.ide.extension.chromattic.client.model.EnumNodeTypeFormat;
import org.exoplatform.ide.extension.chromattic.client.model.GenerateNodeTypeResult;
import org.exoplatform.ide.extension.chromattic.client.model.service.marshaller.GenerateNodeTypeResultUnmarshaller;

/**
 * The concrete implementation of {@link ChrommaticService}.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ChrommaticServiceImpl extends ChrommaticService
{

   public static final String GENERATE_NODE_TYPE_METHOD_CONTEXT = "/ide/chromattic/generate-nodetype-definition";

   public static final String DEPLOY_NODE_TYPE_METHOD_CONTEXT = "/ide/chromattic/register-nodetype/";

   public static final String COMPILE_METHOD_CONTEXT = "/ide/chromattic/compile";

   private HandlerManager eventBus;

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   /**
    * @param eventBus handler manager
    * @param restServiceContext REST service context
    * @param loader loader
    */
   public ChrommaticServiceImpl(HandlerManager eventBus, String restServiceContext, Loader loader)
   {
      this.eventBus = eventBus;
      this.restServiceContext = restServiceContext;
      this.loader = loader;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.model.service.ChrommaticService#compile(org.exoplatform.ide.client.framework.vfs.File)
    */
   @Override
   public void compile(File file, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + COMPILE_METHOD_CONTEXT;

      callback.setResult(file.getHref());
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.LOCATION, file.getHref()).send(callback);
   }

   
   @Override
   public void generateNodeType(String location, EnumNodeTypeFormat nodeTypeFormat, AsyncRequestCallback<GenerateNodeTypeResult> callback)
   {
      String url = restServiceContext + GENERATE_NODE_TYPE_METHOD_CONTEXT;
      GenerateNodeTypeResult result = new GenerateNodeTypeResult();
      callback.setResult(result);
      
      GenerateNodeTypeResultUnmarshaller unmarshaller = new GenerateNodeTypeResultUnmarshaller(result);
      String params = "do-location=" + location + "&";
      params += "nodeTypeFormat=" + nodeTypeFormat.value();

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).send(callback);
   }

   @Override
   public void createNodeType(String nodeType, EnumNodeTypeFormat nodeTypeFormat, EnumAlreadyExistsBehaviour alreadyExistsBehaviour,
      AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + DEPLOY_NODE_TYPE_METHOD_CONTEXT;
      String path = (nodeTypeFormat == null) ? EnumNodeTypeFormat.EXO.value() : nodeTypeFormat.value();
      path += "/";
      path +=
         (alreadyExistsBehaviour == null) ? EnumAlreadyExistsBehaviour.FAIL_IF_EXISTS.getCode()
            : alreadyExistsBehaviour.getCode();

         callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url + path, loader).data(nodeType).send(callback);      
   }

}
