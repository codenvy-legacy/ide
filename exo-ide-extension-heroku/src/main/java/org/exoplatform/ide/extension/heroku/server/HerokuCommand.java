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
package org.exoplatform.ide.extension.heroku.server;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.git.shared.RemoteListRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Abstraction for all 'heroku commands'.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class HerokuCommand
{
   public static HerokuException fault(HttpURLConnection http) throws IOException
   {
      HerokuException error;
      InputStream errorStream = null;
      try
      {
         errorStream = http.getErrorStream();
         if (errorStream == null)
         {
            error = new HerokuException(http.getResponseCode(), null, null);
         }
         else
         {
            int length = http.getContentLength();

            if (length > 0)
            {
               byte[] b = new byte[length];
               errorStream.read(b);
               error = new HerokuException(http.getResponseCode(), new String(b), http.getContentType());
            }
            else if (length == 0)
            {
               error = new HerokuException(http.getResponseCode(), null, null);
            }
            else
            {
               // Unknown length of response.
               ByteArrayOutputStream bout = new ByteArrayOutputStream();
               byte[] b = new byte[1024];
               int point = -1;
               while ((point = errorStream.read(b)) != -1)
                  bout.write(b, 0, point);
               error =
                  new HerokuException(http.getResponseCode(), new String(bout.toByteArray()), http.getContentType());
            }
         }
      }
      finally
      {
         if (errorStream != null)
            errorStream.close();
      }
      return error;
   }

   /**
    * Git working directory. May be <code>null</code> if command executed out of git repository.
    */
   protected final File workDir;

   protected HerokuCommand()
   {
      this(null);
   }

   /**
    * @param workDir git working directory. May be <code>null</code> if command executed out of git repository
    */
   protected HerokuCommand(File workDir)
   {
      this.workDir = workDir;
   }

   /**
    * Execute command.
    * 
    * @return command execution result
    * @throws HerokuException if heroku server return unexpected or error status for request
    * @throws CommandException if any errors occurs when invoke command
    */
   public abstract Object execute() throws HerokuException, CommandException;

   protected void authenticate(HttpURLConnection http) throws IOException
   {
      /*HerokuAuthenticator herokuAuthenticator = new DefaultHerokuAuthenticator();*/
      HerokuAuthenticator herokuAuthenticator =
         (HerokuAuthenticator)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
            HerokuAuthenticator.class);
      herokuAuthenticator.authenticate(http);
   }

   /**
    * Extract heroku application name from git configuration. If {@link #workDir} is <code>null</code> or does not
    * contain <code>.git<code> sub-directory method always return <code>null</code>.
    * 
    * @return application name or <code>null</code> if name can't be determined since command invoked outside of git
    *         repository
    */
   protected String detectAppName()
   {
      if (workDir != null && new File(workDir, Constants.DOT_GIT).exists())
      {
         GitConnection git = null;
         try
         {
            git = GitConnectionFactory.getInstance().getConnection(workDir, null);
            RemoteListRequest request = new RemoteListRequest(null, true);
            List<Remote> remoteList = git.remoteList(request);
            String detectedApp = null;
            for (Remote r : remoteList)
            {
               if (r.getUrl().startsWith("git@heroku.com:"))
               {
                  if ((detectedApp = extractAppName(r)) != null)
                     break;
               }
            }
            return detectedApp;
         }
         catch (GitException ge)
         {
            throw new RuntimeException(ge.getMessage(), ge);
         }
         finally
         {
            if (git != null)
               git.close();
         }
      }
      return null;
   }

   protected String extractAppName(Remote gitRemote)
   {
      String name = null;
      try
      {
         name = new URIish(gitRemote.getUrl()).getHumanishName();
      }
      catch (URISyntaxException e)
      {
         // Invalid URL is not a problem for us, just say we can't determine name from wrong URL.
      }
      return name;
   }
}
