/**
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
 *
 */

package org.exoplatform.ide.client.module.chromattic.model;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.chromattic.model.event.CompileGroovyResultReceivedEvent;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ChrommaticServiceImpl extends ChrommaticService
{

   public static final String COMPILE_METHOD_CONTEXT = "/ide/chromattic/compile";

   private HandlerManager eventBus;

   private String restServiceContext;

   private Loader loader;

   public ChrommaticServiceImpl(HandlerManager eventBus, String restServiceContext, Loader loader)
   {
      this.eventBus = eventBus;
      this.restServiceContext = restServiceContext;
      this.loader = loader;
   }

   @Override
   public void compile(File file)
   {
      System.out.println("compiling >>>> " + file.getHref());

      String url = restServiceContext + COMPILE_METHOD_CONTEXT;

      CompileGroovyResultReceivedEvent event = new CompileGroovyResultReceivedEvent(file.getHref());
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, event);
      AsyncRequest.build(RequestBuilder.POST, url, loader)
         .header(HTTPHeader.LOCATION, file.getHref())
         .send(callback);
   }

}
