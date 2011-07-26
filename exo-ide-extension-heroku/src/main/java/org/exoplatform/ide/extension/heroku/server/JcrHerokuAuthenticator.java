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

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.security.IdentityConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JcrHerokuAuthenticator extends HerokuAuthenticator
{
   private RepositoryService repositoryService;
   private String workspace;
   private String config = "/ide-home/users/";

   public JcrHerokuAuthenticator(RepositoryService repositoryService, InitParams initParams)
   {
      this(repositoryService, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   protected JcrHerokuAuthenticator(RepositoryService repositoryService, String workspace, String config)
   {
      this.repositoryService = repositoryService;
      this.workspace = workspace;
      if (config != null)
      {
         if (!(config.startsWith("/")))
            throw new IllegalArgumentException("Invalid path " + config + ". Absolute path to config node required. ");
         this.config = config;
         if (!this.config.endsWith("/"))
            this.config += "/";
      }
   }

   private static String readValueParam(InitParams initParams, String paramName)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
            return vp.getValue();
      }
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.DefaultHerokuAuthenticator#readCredentials()
    */
   @Override
   protected HerokuCredentials readCredentials() throws IOException
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         // Login with current identity. ConversationState.getCurrent(). 
         session = repository.login(workspace);
         String keyPath = config + session.getUserID() + "/heroku/heroku-credentials";

         Item item = null;
         try
         {
            item = session.getItem(keyPath);
         }
         catch (PathNotFoundException pnfe)
         {
         }

         if (item == null)
            return null;

         Property property = ((Node)item).getNode("jcr:content").getProperty("jcr:data");
         BufferedReader credentialsReader = new BufferedReader(new InputStreamReader(property.getStream()));
         try
         {
            String email = credentialsReader.readLine();
            String apiKey = credentialsReader.readLine();
            return new HerokuCredentials(email, apiKey);
         }
         finally
         {
            credentialsReader.close();
         }
      }
      catch (RepositoryException re)
      {
         throw new RuntimeException(re.getMessage(), re);
      }
      finally
      {
         if (session != null)
            session.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.DefaultHerokuAuthenticator#writeCredentials(org.exoplatform.ide.extension.heroku.server.DefaultHerokuAuthenticator.HerokuCredentials)
    */
   @Override
   protected void writeCredentials(HerokuCredentials credentials) throws IOException
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         checkConfigNode(repository);
         // Login with current identity. ConversationState.getCurrent(). 
         session = repository.login(workspace);
         String user = session.getUserID();
         String herokuPath = config + user + "/heroku";

         Node heroku;
         try
         {
            heroku = (Node)session.getItem(herokuPath);
         }
         catch (PathNotFoundException pnfe)
         {
            org.exoplatform.ide.Utils.putFolders(session, herokuPath);
            heroku = (Node)session.getItem(herokuPath);
         }

         ExtendedNode fileNode;
         Node contentNode;
         try
         {

            fileNode = (ExtendedNode)heroku.getNode("heroku-credentials");
            contentNode = fileNode.getNode("jcr:content");
         }
         catch (PathNotFoundException pnfe)
         {
            fileNode = (ExtendedNode)heroku.addNode("heroku-credentials", "nt:file");
            contentNode = fileNode.addNode("jcr:content", "nt:resource");
         }

         contentNode.setProperty("jcr:mimeType", "text/plain");
         contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         contentNode.setProperty("jcr:data", credentials.getEmail() + "\n" + credentials.getApiKey());
         // Make file accessible for current user only.
         if (!fileNode.isNodeType("exo:privilegeable"))
            fileNode.addMixin("exo:privilegeable");
         fileNode.clearACL();
         fileNode.setPermission(user, PermissionType.ALL);
         fileNode.removePermission(IdentityConstants.ANY);

         session.save();
      }
      catch (RepositoryException re)
      {
         throw new RuntimeException(re.getMessage(), re);
      }
      finally
      {
         if (session != null)
            session.logout();
      }
   }

   private void checkConfigNode(ManageableRepository repository) throws RepositoryException
   {
      String _workspace = workspace;
      if (_workspace == null)
         _workspace = repository.getConfiguration().getDefaultWorkspaceName();

      Session sys = null;
      try
      {
         // Create node for users configuration under system session.
         sys = ((ManageableRepository)repository).getSystemSession(_workspace);
         if (!(sys.itemExists(config)))
         {
            org.exoplatform.ide.Utils.putFolders(sys, config);
            sys.save();
         }
      }
      finally
      {
         if (sys != null)
            sys.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.DefaultHerokuAuthenticator#removeCredentials()
    */
   @Override
   protected void removeCredentials()
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         // Login with current identity. ConversationState.getCurrent(). 
         session = repository.login(workspace);
         String user = session.getUserID();
         String keyPath = config + user + "/heroku/heroku-credentials";
         Item item = session.getItem(keyPath);
         item.remove();
         session.save();
      }
      catch (PathNotFoundException pnfe)
      {
      }
      catch (RepositoryException re)
      {
         throw new RuntimeException(re.getMessage(), re);
      }
      finally
      {
         if (session != null)
            session.logout();
      }
   }
}
