/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.groovy.service.groovy;

import java.util.List;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.ide.client.module.groovy.service.RestServiceOutput;
import org.exoplatform.ide.client.module.groovy.service.SimpleParameterEntry;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyUndeployResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyValidateResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.marshal.RestServiceOutputUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class GroovyServiceImpl extends GroovyService
{

   public static final String SERVLET_CONTEXT = "/services/groovy";

   public static final String LOAD = "/load";

   public static final String VALIDATE = "/validate";

   private HandlerManager eventBus;
   
   private String restServiceContext;

   private Loader loader;

   public GroovyServiceImpl(HandlerManager eventBus, String restServiceContext, Loader loader)
   {
      this.eventBus = eventBus;
      this.restServiceContext = restServiceContext;
      this.loader = loader;
   }

   /* (non-Javadoc)
    * @see org.exoplatform.gadgets.devtool.client.model.groovy.GroovyService#deploy(java.lang.String)
    */
   @Override
   public void deploy(String href)
   {
      String url = restServiceContext + SERVLET_CONTEXT + LOAD + "?state=true";
      deploy(href, url);
   }
   
   /**
    * @see org.exoplatform.ide.client.model.groovy.GroovyService#deploy(java.lang.String, java.lang.String)
    */
   @Override
   public void deploy(String href, String url)
   {
      GroovyDeployResultReceivedEvent event = new GroovyDeployResultReceivedEvent(href);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, event);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.LOCATION, href).send(callback);
   }

   /* (non-Javadoc)
    * @see org.exoplatform.gadgets.devtool.client.model.groovy.GroovyService#undeploy(java.lang.String)
    */
   @Override
   public void undeploy(String href)
   {
      String url = restServiceContext + SERVLET_CONTEXT + LOAD + "?state=false";
      undeploy(href, url);
   }
   
   /**
    * @see org.exoplatform.ide.client.model.groovy.GroovyService#undeploy(java.lang.String, java.lang.String)
    */
   @Override
   protected void undeploy(String href, String url)
   {
      GroovyUndeployResultReceivedEvent event = new GroovyUndeployResultReceivedEvent(href);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, event);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.LOCATION, href).send(callback);
   }

   /* (non-Javadoc)
    * @see org.exoplatform.gadgets.devtool.client.model.groovy.GroovyService#validate(java.lang.String, java.lang.String)
    */
   @Override
   public void validate(String href, String content)
   {
      String url = restServiceContext + SERVLET_CONTEXT + VALIDATE;
      validate(href, content, url);
   }
   
   protected void validate(String href, String content, String url)
   {
      GroovyValidateResultReceivedEvent event = new GroovyValidateResultReceivedEvent(href);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, event);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.CONTENT_TYPE, "script/groovy").header(
         HTTPHeader.LOCATION, href).data(content).send(callback);
   }

   @Override
   public void getOutput(String url, String method, List<SimpleParameterEntry> headers,
      List<SimpleParameterEntry> params, String body)
   {
      RestServiceOutput output = new RestServiceOutput(url, method);

      if (params != null && params.size() > 0)
      {
         if (!url.contains("?"))
         {
            url += "?";
         }
         for (SimpleParameterEntry param : params)
         {
            if (param.getName() != null && param.getName().length() != 0)
               url += param.getName() + "=" + param.getValue() + "&";
         }
         url = url.substring(0, url.lastIndexOf("&"));
      }

      Method httpMethod;
      if (method.equals(HTTPMethod.GET))
      {
         httpMethod = RequestBuilder.GET;
      }
      else if (method.equals(HTTPMethod.POST))
      {
         httpMethod = RequestBuilder.POST;
      }
      else
      {
         httpMethod = RequestBuilder.POST;
         headers.add(new SimpleParameterEntry(HTTPHeader.X_HTTP_METHOD_OVERRIDE, method));  // add X_HTTP_METHOD_OVERRIDE header for the methods like OPTION, PUT, DELETE and others. 
      }
      
      RestServiceOutputReceivedEvent event = new RestServiceOutputReceivedEvent(output);
      RestServiceOutputUnmarshaller unmarshaller = new RestServiceOutputUnmarshaller(output);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, event);
      AsyncRequest request = AsyncRequest.build(httpMethod, url, loader);
      if (headers != null)
      {
         for (SimpleParameterEntry header : headers)
         {
            if (header.getName() != null && header.getName().length() != 0)
               request.header(header.getName(), header.getValue());
         }
      }
      
      //Add this code for fix bug http://jira.exoplatform.org/browse/IDE-229
      body = body.intern();
      if (body != null && body.length() > 0)
      {
         request.data(body);
      }
      request.send(callback);
   }

}
