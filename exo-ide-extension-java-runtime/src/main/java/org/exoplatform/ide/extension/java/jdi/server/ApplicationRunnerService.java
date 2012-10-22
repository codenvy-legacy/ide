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
package org.exoplatform.ide.extension.java.jdi.server;

import static org.exoplatform.ide.commons.JsonHelper.toJson;

import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.DebugApplicationInstance;
import org.exoplatform.ide.websocket.MessageBroker;
import org.exoplatform.ide.websocket.MessageBroker.Channels;

import java.net.URL;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * Provide access to {@link ApplicationRunner} through HTTP.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/java/runner")
public class ApplicationRunnerService
{
   @Inject
   private ApplicationRunner runner;

   /** Component for sending message to client over WebSocket connection. */
   @Inject
   private MessageBroker messageBroker;

   @Path("run")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public ApplicationInstance runApplication(@QueryParam("war") final URL war,
                                             @Context final UriInfo uriInfo,
                                             @QueryParam("usewebsocket") boolean useWebSocket) throws ApplicationRunnerException
   {
      if (!useWebSocket)
      {
         return doRunApplication(war, uriInfo);
      }
      else
      {
         new Runnable()
         {
            @Override
            public void run()
            {
               try
               {
                  ApplicationInstance app = doRunApplication(war, uriInfo);
                  publishWebSocketMessage(MessageBroker.Channels.APP_STARTED, toJson(app), null);
               }
               catch (ApplicationRunnerException e)
               {
                  publishWebSocketMessage(MessageBroker.Channels.APP_STARTED, null, e);
               }
            }
         }.run();
         return null;
      }
   }

   private ApplicationInstance doRunApplication(URL war, UriInfo uriInfo) throws ApplicationRunnerException
   {
      ApplicationInstance app = runner.runApplication(war);
      app.setStopURL(uriInfo.getBaseUriBuilder().path(ApplicationRunnerService.this.getClass(), "stopApplication")
         .queryParam("name", app.getName()).build().toString());
      return app;
   }

   @Path("debug")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public DebugApplicationInstance debugApplication(@QueryParam("war") final URL war,
                                                    @QueryParam("suspend") final boolean suspend,
                                                    @Context final UriInfo uriInfo,
                                                    @QueryParam("usewebsocket") boolean useWebSocket) throws ApplicationRunnerException
   {
      if (!useWebSocket)
      {
         return doDebugApplication(war, suspend, uriInfo);
      }
      else
      {
         new Runnable()
         {
            
            @Override
            public void run()
            {
               try
               {
                  DebugApplicationInstance app = doDebugApplication(war, suspend, uriInfo);
                  publishWebSocketMessage(Channels.DEBUGGER_STARTED, toJson(app), null);
               }
               catch (ApplicationRunnerException e)
               {
                  publishWebSocketMessage(Channels.DEBUGGER_STARTED, null, e);
               }
            }
         }.run();
         return null;
      }
   }

   private DebugApplicationInstance doDebugApplication(URL war, boolean suspend, UriInfo uriInfo) throws ApplicationRunnerException
   {
      DebugApplicationInstance app = runner.debugApplication(war, suspend);
      app.setStopURL(uriInfo.getBaseUriBuilder().path(ApplicationRunnerService.this.getClass(), "stopApplication")
         .queryParam("name", app.getName()).build().toString());
      return app;
   }

   @GET
   @Path("logs")
   @Produces(MediaType.TEXT_PLAIN)
   public String getLogs(@QueryParam("name") String name) throws ApplicationRunnerException
   {
      return runner.getLogs(name);
   }

   @GET
   @Path("stop")
   public void stopApplication(@QueryParam("name") String name) throws ApplicationRunnerException
   {
      runner.stopApplication(name);
   }

   @GET
   @Path("prolong")
   public void prolongExpirationTime(@QueryParam("name") String name, @QueryParam("time")  long time)
   {
      runner.prolongExpirationTime(name, time);
   }

   /**
    * Publishes message over WebSocket connection.
    * 
    * @param data
    *    the data to be sent to the client
    * @param e
    *    an exception to be sent to the client
    */
   private void publishWebSocketMessage(Channels channel, String data, Exception e)
   {
      messageBroker.publish(channel.toString(), data, e, null);
   }
}
