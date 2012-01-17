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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.extension.chromattic.client.model.EnumAlreadyExistsBehaviour;
import org.exoplatform.ide.extension.chromattic.client.model.EnumNodeTypeFormat;
import org.exoplatform.ide.vfs.client.model.FileModel;

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

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   /**
    * @param restServiceContext REST service context
    * @param loader loader
    */
   public ChrommaticServiceImpl(String restServiceContext, Loader loader)
   {
      this.restServiceContext = restServiceContext;
      this.loader = loader;
   }

   @Override
   public void generateNodeType(FileModel file, String vfsid, EnumNodeTypeFormat nodeTypeFormat,
      AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      String url = restServiceContext + GENERATE_NODE_TYPE_METHOD_CONTEXT;

      StringBuffer params = new StringBuffer();
      params.append("vfsid=").append(vfsid).append("&").append("nodeTypeFormat=").append(nodeTypeFormat.value())
         .append("&").append("id=").append(file.getId());
      if (file.getProject() != null)
         params.append("&").append("projectid=").append(file.getProject().getId());
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).send(callback);
   }

   @Override
   public void createNodeType(String nodeType, EnumNodeTypeFormat nodeTypeFormat,
      EnumAlreadyExistsBehaviour alreadyExistsBehaviour, AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + DEPLOY_NODE_TYPE_METHOD_CONTEXT;
      String path = (nodeTypeFormat == null) ? EnumNodeTypeFormat.EXO.value() : nodeTypeFormat.value();
      path += "/";
      path +=
         (alreadyExistsBehaviour == null) ? EnumAlreadyExistsBehaviour.FAIL_IF_EXISTS.getCode()
            : alreadyExistsBehaviour.getCode();

      AsyncRequest.build(RequestBuilder.POST, url + path).loader(loader).data(nodeType).send(callback);
   }
}
