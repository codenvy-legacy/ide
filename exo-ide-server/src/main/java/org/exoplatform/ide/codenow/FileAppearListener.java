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
package org.exoplatform.ide.codenow;

import static org.exoplatform.ide.commons.JsonHelper.toJson;

import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.Pair;
import org.everrest.websockets.message.RESTfulOutputMessage;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent.ChangeType;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.server.observation.TypeFilter;
import org.exoplatform.ide.vfs.server.observation.VfsIDFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * Use for determinate that file by specified path already add to JCR storage after Git clone command.
 * We need it because we use davFs like buffer layer between File System and JCR storage.
 * DavVfs work via WebDav asynchronously and we don't now when file appear in JCR storage.
 * 
 *  This need for CodeNow feature  we must be sure that file already exist before open it.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: FileAppearListener.java Nov 28, 2012 vetal $
 *
 */
@Path("/ide/codenow/file-appear-listener")
public class FileAppearListener
{
   @Inject
   private EventListenerList listenerList;
   
   private static final Log LOG = ExoLogger.getLogger(FileAppearListener.class);
   
   @Path("start/{vfsid}")
   @GET
   public void start(@PathParam("vfsid") String vfsid, @QueryParam("file-path") final String filePath)
   {
      if (vfsid == null || vfsid.isEmpty())
         throw new RuntimeException("VFS ID " + vfsid + "not configured.");

      if (filePath == null || filePath.isEmpty())
         throw new RuntimeException("File Path can't be empty.");

       EventListener l = new EventListener()
       {
         @Override
         public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
         {
            LOG.info("file " + filePath + " exist ::: ide/codenow/file-appear-listener");
            publishWebSocketMessage("file " + filePath + " exist", "/ide/codenow/file-appear-listener");
         }
      };
      VfsIDFilter idFilter = new VfsIDFilter(vfsid);
      FullQualifiedPathFilter pathFilter = new FullQualifiedPathFilter(filePath);
      listenerList.addEventListener(
         ChangeEventFilter.createAndFilter(idFilter, new TypeFilter(ChangeType.CREATED), pathFilter), l);
      LOG.debug("Start File Appear Listener for file " + filePath);
   }
   
   
   @Path("stop/{vfsid}")
   @GET
   public void stop(@PathParam("vfsid") String vfsid, @QueryParam("file-path") final String filePath)
   {
      if (vfsid == null || vfsid.isEmpty())
         throw new RuntimeException("VFS ID " + vfsid + "not configured.");

      if (filePath == null || filePath.isEmpty())
         throw new RuntimeException("File Path can't be empty.");

      EventListener l = new EventListener()
      {
         @Override
         public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
         {
            publishWebSocketMessage("file " + filePath + " exist", "/ide/codenow/file-appear-listener");
         }
      };
      VfsIDFilter idFilter = new VfsIDFilter(vfsid);
      FullQualifiedPathFilter pathFilter = new FullQualifiedPathFilter(filePath);
      listenerList.removeEventListener(ChangeEventFilter.createAndFilter(idFilter, new TypeFilter(ChangeType.CREATED), pathFilter), l);
      LOG.debug("Stop File Appear Listener for file " + filePath);
      
   }
   
   /**
    * Publish the message over WebSocket connection.
    *
    * @param data
    *    the data to be sent to the client
    * @param channelID
    *    channelID channel identifier
    */
   private void publishWebSocketMessage(Object data, String channelID)
   {
      RESTfulOutputMessage message = new RESTfulOutputMessage();
      message.setHeaders(new Pair[]{new Pair("x-everrest-websocket-message-type", "subscribed-message"),
                                    new Pair("x-everrest-websocket-channel", channelID)});
      message.setResponseCode(200);
      if (data instanceof String)
      {
         message.setBody((String)data);
      }
      else if (data != null)
      {
         message.setBody(toJson(data));
      }

      try
      {
         WSConnectionContext.sendMessage(channelID, message);
      }
      catch (Exception e)
      {
         LOG.error("Failed to send message over WebSocket.", e);
      }
   }


   /**
    * Filter for events by path of changed item. Path specified in constructor.
    * 
    */
   public final class FullQualifiedPathFilter extends ChangeEventFilter
   {

      private final String path;

      public FullQualifiedPathFilter(String path)
      {
         this.path = path;

      }

      @Override
      public boolean matched(ChangeEvent event)
      {
         String path = event.getItemPath();
         return path != null && this.path != null && this.path.equals(path);
      }
   }

}
