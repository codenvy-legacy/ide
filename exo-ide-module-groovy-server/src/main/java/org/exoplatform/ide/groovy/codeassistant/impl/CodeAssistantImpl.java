/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.groovy.codeassistant.impl;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.groovy.codeassistant.CodeAssistant;
import org.exoplatform.ide.groovy.codeassistant.CodeAssistantException;
import org.exoplatform.ide.groovy.codeassistant.bean.ClassInfo;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.impl.ObjectBuilder;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/ide/code-assistant")
public class CodeAssistantImpl implements CodeAssistant
{

   private final RepositoryService repositoryService;

   private final SessionProviderService sessionProviderService;

   private JsonParser jsonParser;

   private JsonHandler jsonHandler;

   private final String wsName;

   public CodeAssistantImpl(String wsName, RepositoryService repositoryService,
      SessionProviderService sessionProviderService)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
      this.wsName = wsName;
      jsonParser = new JsonParserImpl();
      jsonHandler = new JsonDefaultHandler();
   }

   /**
    * {@inheritDoc}
    * @throws CodeAssistantException 
    */
   @GET
   @Path("/class-description")
   @Produces(MediaType.APPLICATION_JSON)
   public ClassInfo getClassByFQN(@QueryParam("fqn") String fqn) throws CodeAssistantException
   {
      SessionProvider sp = sessionProviderService.getSessionProvider(null);
      try
      {
         Session session = sp.getSession(wsName, repositoryService.getDefaultRepository());
         String relPath = new String();
         String[] seg = fqn.split("\\.");
         for (int i = 1; i < seg.length; i++)
         {
            String p = new String();
            for (int j = 0; j < i; j++)
            {
               p += seg[j] + ".";
            }
            p = p.substring(0, p.length() - 1);
            relPath += p + "/";
         }
         relPath += fqn;
         Node n = session.getRootNode().getNode("classpath").getNode(relPath);
         String json = n.getNode("jcr:content").getProperty("jcr:data").getString();
         return json2classInfo(json);
      }
      catch (RepositoryException e)
      {
         e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (JsonException e)
      {
         e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
   }

   /**
    * {@inheritDoc}
    */
   @GET
   @Path("/find")
   @Produces(MediaType.APPLICATION_JSON)
   public String[] findFQNsByClassName(@QueryParam("class") String className) throws CodeAssistantException
   {
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:className='" + className + "'";
      SessionProvider sp = sessionProviderService.getSessionProvider(null);
      
      try
      {
         Session session = sp.getSession(wsName, repositoryService.getDefaultRepository());
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();
         //TODO
         String[] fqns = new String[(int)nodes.getSize()];
         int i = 0;
         while (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            fqns[i++] = node.getParent().getName();
         }
         return fqns;
      }
      catch (RepositoryException e)
      {
         e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }

   }

   /**
    * {@inheritDoc}
    */
   @GET
   @Path("/find-by-prefix")
   @Produces(MediaType.APPLICATION_JSON)
   public String[] findFQNsByPrefix(@QueryParam("prefix") String prefix) throws CodeAssistantException
   {
      {
         String sql = "SELECT * FROM exoide:classDescription WHERE exoide:fqn LIKE '" + prefix + "%'";
         SessionProvider sp = sessionProviderService.getSessionProvider(null);
         try
         {
            Session session = sp.getSession(wsName, repositoryService.getDefaultRepository());
            Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
            QueryResult result = q.execute();
            NodeIterator nodes = result.getNodes();
            //TODO
            String[] fqns = new String[(int)nodes.getSize()];
            int i = 0;
            while (nodes.hasNext())
            {
               Node node = (Node)nodes.next();
               fqns[i++] = node.getParent().getName();
            }
            return fqns;
         }
         catch (RepositoryException e)
         {
            e.printStackTrace();
            //TODO:need fix status code
            throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
         }
         catch (RepositoryConfigurationException e)
         {
            e.printStackTrace();
            //TODO:need fix status code
            throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
         }
      }
   }

   private ClassInfo json2classInfo(String json) throws JsonException
   {
      ByteArrayInputStream stream = new ByteArrayInputStream(json.getBytes());
      jsonParser.parse(stream, jsonHandler);
      JsonValue jsonValue = jsonHandler.getJsonObject();
      ClassInfo cd = ObjectBuilder.createObject(ClassInfo.class, jsonValue);
      return cd;

   }

}
