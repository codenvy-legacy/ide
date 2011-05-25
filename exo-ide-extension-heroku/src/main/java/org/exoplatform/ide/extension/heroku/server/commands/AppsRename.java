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

import org.exoplatform.ide.extension.heroku.server.Arg;
import org.exoplatform.ide.extension.heroku.server.CommandException;
import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuCommand;
import org.exoplatform.ide.extension.heroku.server.HerokuException;
import org.exoplatform.ide.extension.heroku.server.Option;
import org.exoplatform.ide.extension.heroku.shared.HerokuApplicationInfo;
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
import java.util.Collections;
import java.util.List;

/**
 * Rename application. If command executed successfully method {@link #execute()} returns information about renamed
 * application. Minimal set of application attributes:
 * <ul>
 * <li>New name</li>
 * <li>New git URL of repository</li>
 * <li>New HTTP URL of application</li>
 * </ul>
 * <p>
 * Remote configuration updated.
 * </p>
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppsRename extends HerokuCommand
{
   /** New name for application. If <code>null</code> then method {@link #execute()} throws {@link CommandException}. */
   @Arg(index = 0)
   private String newname;

   /**
    * Current name of application. If <code>null</code> then try to determine application name from git configuration.
    * To be able determine application name <code>workDir</code> must not be <code>null</code> at least.
    */
   @Option(name = "--app")
   private String app;

   public AppsRename(File workDir)
   {
      super(workDir);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.HerokuCommand#execute()
    */
   @Override
   public Object execute() throws HerokuException, CommandException
   {
      if (newname == null && newname.isEmpty())
         throw new CommandException("New name may not be null or empty string. ");

      if (this.app == null)
      {
         String detectedApp = detectAppName();
         if (detectedApp == null || detectedApp.isEmpty())
            throw new CommandException("Application name is not defined. ");
         this.app = detectedApp;
      }

      HttpURLConnection http = null;
      GitConnection git = null;
      try
      {
         URL url = new URL(Heroku.HEROKU_API + "/apps/" + app);
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
         HerokuApplicationInfo info =
            (HerokuApplicationInfo)Heroku.getInstance().execute("apps:info",
               Collections.singletonMap("--app", newname), null, null);

         String gitUrl = info.getGitUrl();

         RemoteListRequest listRequest = new RemoteListRequest(null, true);
         git = GitConnectionFactory.getInstance().getConnection(workDir, null);
         List<Remote> remoteList = git.remoteList(listRequest);
         for (Remote r : remoteList)
         {
            // Update remote.
            if (r.getUrl().startsWith("git@heroku.com:"))
            {
               String rname = extractAppName(r);
               if (rname != null && rname.equals(app))
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
