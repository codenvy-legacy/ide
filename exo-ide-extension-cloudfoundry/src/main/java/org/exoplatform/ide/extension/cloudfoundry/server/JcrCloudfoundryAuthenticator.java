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
package org.exoplatform.ide.extension.cloudfoundry.server;

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
import java.io.Writer;
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
public class JcrCloudfoundryAuthenticator extends CloudfoundryAuthenticator
{
   private RepositoryService repositoryService;
   private String workspace;
   private String config = "/";

   public JcrCloudfoundryAuthenticator(RepositoryService repositoryService, InitParams initParams)
   {
      this(repositoryService, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   protected JcrCloudfoundryAuthenticator(RepositoryService repositoryService, String workspace, String config)
   {
      this.repositoryService = repositoryService;
      this.workspace = workspace;
      if (config != null)
      {
         if (!(config.startsWith("/")))
            throw new IllegalArgumentException("Invalid path " + config
               + ". Absolute path to configuration node required. ");
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
    * @see org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryAuthenticator#readCredentials()
    */
   @Override
   protected CloudfoundryCredentials readCredentials() throws IOException
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         // Login with current identity. ConversationState.getCurrent(). 
         session = repository.login(workspace);
         String user = session.getUserID();
         String tokenPath = config + user + "/cloud_foundry/vmc_token";

         Item item = null;
         try
         {
            item = session.getItem(tokenPath);
         }
         catch (PathNotFoundException pnfe)
         {
         }

         if (item == null)
            return null;

         Property property = ((Node)item).getNode("jcr:content").getProperty("jcr:data");
         BufferedReader r = new BufferedReader(new InputStreamReader(property.getStream()));
         CloudfoundryCredentials credentials;
         try
         {
            credentials = CloudfoundryCredentials.readFrom(r);
         }
         finally
         {
            r.close();
         }

         return credentials;
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
    * @see org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryAuthenticator
    *      #writeCredentials(org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryCredentials)
    */
   @Override
   protected void writeCredentials(CloudfoundryCredentials credentials) throws IOException
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         checkConfigNode(repository);
         session = repository.login(workspace);
         String user = session.getUserID();
         String userPath = config + user;
         
         Node userNode;
         try
         {
            userNode = (Node)session.getItem(userPath);
         }
         catch (PathNotFoundException pnfe)
         {
            userNode = ((Node)session.getItem(config)).addNode(user, "nt:folder");
         }

         Node cloudFoundry;
         try
         {
            cloudFoundry = userNode.getNode("cloud_foundry");
         }
         catch (PathNotFoundException pnfe)
         {
            cloudFoundry = userNode.addNode("cloud_foundry", "nt:folder");
         }
         
         ExtendedNode fileNode;
         Node contentNode;
         try
         {
            fileNode = (ExtendedNode)cloudFoundry.getNode("vmc_token");
            contentNode = fileNode.getNode("jcr:content");
         }
         catch (PathNotFoundException pnfe)
         {
            fileNode = (ExtendedNode)cloudFoundry.addNode("vmc_token", "nt:file");
            contentNode = fileNode.addNode("jcr:content", "nt:resource");
         }

         contentNode.setProperty("jcr:mimeType", "text/plain");
         contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         Writer w = new JsonHelper.FastStrWriter();
         credentials.writeTo(w);
         contentNode.setProperty("jcr:data", w.toString());
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
         Node configNode;
         try
         {
            configNode = (Node)sys.getItem(config);
         }
         catch (PathNotFoundException e)
         {
            String[] pathSegments = config.substring(1).split("/");
            configNode = sys.getRootNode();
            for (int i = 0; i < pathSegments.length; i++)
            {
               try
               {
                  configNode = configNode.getNode(pathSegments[i]);
               }
               catch (PathNotFoundException e1)
               {
                  configNode = configNode.addNode(pathSegments[i], "nt:folder");
               }
            }
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
    * @see org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryAuthenticator#removeCredentials()
    */
   @Override
   protected void removeCredentials()
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         session = repository.login(workspace);
         String user = session.getUserID();
         String tokenPath = config + user + "/cloud_foundry/vmc_token";
         Item item = session.getItem(tokenPath);
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
