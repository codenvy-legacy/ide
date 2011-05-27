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
package org.exoplatform.ide.extension.heroku.server.commands;

import org.exoplatform.ide.extension.heroku.server.CommandException;
import org.exoplatform.ide.extension.heroku.server.CredentialsNotFoundException;
import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuCommand;
import org.exoplatform.ide.extension.heroku.server.HerokuException;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.git.shared.RemoteListRequest;
import org.exoplatform.ide.git.shared.RemoteUpdateRequest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Rename application.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppsRename extends HerokuCommand
{
   /**
    * @param name current application name. If <code>null</code> then try to determine application name from git
    *           configuration. To be able determine application name <code>workDir</code> must not be <code>null</code>
    * @param newname new name for application. If <code>null</code> CommandException thrown
    * @param workDir git working directory. May be <code>null</code> if command executed out of git repository in this
    *           case <code>name</code> parameter must be not <code>null</code>. If not <code>null</code> and is git
    *           folder then remote configuration update
    * @return information about renamed application. Minimal set of application attributes:
    *         <ul>
    *         <li>New name</li>
    *         <li>New git URL of repository</li>
    *         <li>New HTTP URL of application</li>
    *         </ul>
    * @throws HerokuException if heroku server return unexpected or error status for request
    * @throws CredentialsNotFoundException if cannot get access to heroku.com server since user is not login yet and has
    *            not credentials. Must use {@link AuthLogin#execute(String, String)} first.
    * @throws CommandException if any other exception occurs
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> rename( //
      @QueryParam("name") String name, //
      @QueryParam("newname") String newname, //
      @QueryParam("workDir") File workDir //
   ) throws HerokuException, CredentialsNotFoundException, CommandException
   {
      if (newname == null || newname.isEmpty())
         throw new CommandException("New name may not be null or empty string. ");

      if (name == null || name.isEmpty())
      {
         name = detectAppName(workDir);
         if (name == null || name.isEmpty())
            throw new CommandException("Application name is not defined. ");
      }

      HttpURLConnection http = null;
      GitConnection git = null;
      try
      {
         URL url = new URL(Heroku.HEROKU_API + "/apps/" + name);
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("PUT");
         http.setRequestProperty("Accept", "application/xml, */*");
         http.setRequestProperty("Content-type", "application/xml");
         http.setDoOutput(true);
         authenticate(http);

         OutputStream output = http.getOutputStream();
         try
         {
            output.write(("<app><name>" + newname + "</name></app>").getBytes());
            output.flush();
         }
         finally
         {
            output.close();
         }

         if (http.getResponseCode() != 200)
            throw fault(http);

         // Get updated info about application.
         Map<String, String> info =
            ((AppsInfo)Heroku.getInstance().getCommand("apps:info")).info(newname, false, workDir);

         String gitUrl = (String)info.get("gitUrl");

         RemoteListRequest listRequest = new RemoteListRequest(null, true);
         git = GitConnectionFactory.getInstance().getConnection(workDir, null);
         List<Remote> remoteList = git.remoteList(listRequest);
         for (Remote r : remoteList)
         {
            // Update remote.
            if (r.getUrl().startsWith("git@heroku.com:"))
            {
               String rname = extractAppName(r);
               if (rname != null && rname.equals(name))
               {
                  git.remoteUpdate(new RemoteUpdateRequest(r.getName(), null, false, new String[]{gitUrl},
                     new String[]{r.getUrl()}, null, null));
                  break;
               }
            }
         }
         return info;
      }
      catch (IOException ioe)
      {
         throw new CommandException(ioe.getMessage(), ioe);
      }
      catch (GitException gite)
      {
         throw new CommandException(gite.getMessage(), gite);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }
}
