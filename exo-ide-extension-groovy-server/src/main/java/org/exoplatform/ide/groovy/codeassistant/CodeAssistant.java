/*
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
 */
package org.exoplatform.ide.groovy.codeassistant;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.groovy.JcrUtils;
import org.exoplatform.ide.groovy.codeassistant.bean.ShortTypeInfo;
import org.exoplatform.ide.groovy.codeassistant.bean.TypeInfo;
import org.exoplatform.ide.groovy.codeassistant.extractors.GroovyClassNamesExtractor;
import org.exoplatform.ide.groovy.util.DependentResources;
import org.exoplatform.ide.groovy.util.GroovyScriptServiceUtil;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.impl.ObjectBuilder;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
/**
 * Service provide Autocomplete of source code is also known as code completion feature. 
 * In a source code editor autocomplete is greatly simplified by the regular structure 
 * of the programming languages. 
 * At current moment implemented the search class FQN,
 * by Simple Class Name and a prefix (the lead characters in the name of the package or class).
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/ide/code-assistant")
public class CodeAssistant
{

   private final RepositoryService repositoryService;

   private final ThreadLocalSessionProviderService sessionProviderService;

   private final String wsName;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(CodeAssistant.class);

   public CodeAssistant(String wsName, RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
      this.wsName = wsName;
   }

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * 
    * @param fqn the Full Qualified Name
    * @return {@link TypeInfo} 
    * @throws CodeAssistantException
    */
   @GET
   @Path("/class-description")
   @Produces(MediaType.APPLICATION_JSON)
   public TypeInfo getClassByFQN(@Context UriInfo uriInfo, @QueryParam("fqn") String fqn,
      @HeaderParam("location") String location) throws CodeAssistantException
   {
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:fqn='" + fqn + "'";
      try
      {
         Session session = JcrUtils.getSession(repositoryService, sessionProviderService, wsName);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();
         //TODO
         if (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            JsonParser jsonParser = new JsonParserImpl();
            JsonHandler jsonHandler = new JsonDefaultHandler();
            jsonParser.parse(node.getProperty("jcr:data").getStream(), jsonHandler);
            JsonValue jsonValue = jsonHandler.getJsonObject();
            TypeInfo typeInfo = ObjectBuilder.createObject(TypeInfo.class, jsonValue);
            return typeInfo;
         }
         else
         {
            if (location != null)
            {
               DependentResources dependentResources =
                  GroovyScriptServiceUtil.getDependentResource(location, uriInfo.getBaseUri().toASCIIString(),
                     repositoryService, sessionProviderService);
               if (dependentResources != null)
               {
                  TypeInfo classInfo =
                     new GroovyClassNamesExtractor(repositoryService, sessionProviderService).getClassInfo(fqn,
                        dependentResources);
                  if (classInfo == null)
                     throw new CodeAssistantException(HTTPStatus.NOT_FOUND, "Class info for " + fqn + " not found");
                  return classInfo;

               }
            }
            if (LOG.isDebugEnabled())
               LOG.error("Class info for " + fqn + " not found");
            throw new CodeAssistantException(HTTPStatus.NOT_FOUND, "Class info for " + fqn + " not found");
         }
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (JsonException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (MalformedURLException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (URISyntaxException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
   }

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * 
    * @param fqn the Full Qualified Name
    * @return {@link TypeInfo} 
    * @throws ClassNotFoundException
    */
   /**
    * Returns set of FQNs matched to Class name (means FQN end on {className})
    * Example :
    * if className = "String"
    * set must content
    * {
    *  java.lang.String
    *  java.lang.StringBuilder
    *  java.lang.StringBuffer
    *  java.lang.StringIndexOutOfBoundsException
    *  java.util.StringTokenizer
    *  ....
    * }
    * @param className the string for matching FQNs 
    * @return
    * @throws Exception 
    * */
   @GET
   @Path("/find")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ShortTypeInfo> findFQNsByClassName(@Context UriInfo uriInfo, @QueryParam("class") String className,
      @HeaderParam("location") String location) throws CodeAssistantException
   {
      //TODO add Groovy
      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:className='" + className + "'";
      try
      {
         if (location != null && !location.isEmpty())
         {
            DependentResources dependentResources =
               GroovyScriptServiceUtil.getDependentResource(location, uriInfo.getBaseUri().toASCIIString(),
                  repositoryService, sessionProviderService);
            if (dependentResources != null)
            {

               types =
                  new GroovyClassNamesExtractor(repositoryService, sessionProviderService).getClassNames(className,
                     dependentResources);

            }
         }

         Session session = JcrUtils.getSession(repositoryService, sessionProviderService, wsName);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();

         while (nodes.hasNext())
         {

            Node node = (Node)nodes.next();
            types.add(new ShortTypeInfo((int)node.getProperty("exoide:modifieres").getLong(), node.getProperty(
               "exoide:className").getString(), node.getProperty("exoide:fqn").getString(), node.getProperty(
               "exoide:type").getString()));
         }
         return types;
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (MalformedURLException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(HTTPStatus.BAD_REQUEST, e.getMessage());
      }
      catch (URISyntaxException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(HTTPStatus.BAD_REQUEST, e.getMessage());
      }

   }

   /**
    * Returns set of FQNs matched to prefix (means FQN begin on {prefix} or Class simple name)
    * Example :
    * if prefix = "java.util.c"
    * set must content:
    *  {
    *   java.util.Comparator<T>
    *   java.util.Calendar
    *   java.util.Collection<E>
    *   java.util.Collections
    *   java.util.ConcurrentModificationException
    *   java.util.Currency
    *   java.util.concurrent
    *   java.util.concurrent.atomic
    *   java.util.concurrent.locks
    *  }
    * 
    * @param prefix the string for matching FQNs
    * @param where the string that indicate where find (must be "className" or "fqn")
    */
   @GET
   @Path("/find-by-prefix/{prefix}")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ShortTypeInfo> findFQNsByPrefix(@Context UriInfo uriInfo, @PathParam("prefix") String prefix,
      @QueryParam("where") String where, @HeaderParam("location") String location) throws CodeAssistantException
   {
      List<ShortTypeInfo> groovyClass = null;
      ShortTypeInfo[] types = null;
      //by default search in className
      if (where == null || "".equals(where))
      {
         where = "className";
      }
      else if (!"className".equals(where) && !"fqn".equals(where))
      {
         throw new CodeAssistantException(HTTPStatus.BAD_REQUEST, "\"where\" parameter must be className or fqn");
      }
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:" + where + " LIKE '" + prefix + "%'";
      try
      {
         //find groovy classes only by name
         if (location != null && !location.isEmpty() && where.equals("className"))
         {

            DependentResources dependentResources =
               GroovyScriptServiceUtil.getDependentResource(location, uriInfo.getBaseUri().toASCIIString(),
                  repositoryService, sessionProviderService);
            if (dependentResources != null)
            {
               groovyClass =
                  new GroovyClassNamesExtractor(repositoryService, sessionProviderService).getClassNames(prefix,
                     dependentResources);

            }
         }

         Session session = JcrUtils.getSession(repositoryService, sessionProviderService, wsName);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();
         //TODO
         //TODO
         types = new ShortTypeInfo[(int)nodes.getSize()];
         int i = 0;
         while (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            types[i++] =
               new ShortTypeInfo((int)0L, node.getProperty("exoide:className").getString(), node.getProperty(
                  "exoide:fqn").getString(), node.getProperty("exoide:type").getString());
         }

      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (MalformedURLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (URISyntaxException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      List<ShortTypeInfo> result = new ArrayList<ShortTypeInfo>();

      if (types != null)
      {
         result.addAll(Arrays.asList(types));
      }
      if (groovyClass != null)
      {
         result.addAll(groovyClass);
      }

      return result;

   }

   /**
    * Find all classes or annotations or interfaces
    *   
    * @param type the string that represent one of Java class type (i.e. CLASS, INTERFACE, ANNOTATION) 
    * @param prefix optional parameter that matching first letter of type name
    * @return Returns set of FQNs matched to class type
    * @throws CodeAssistantException
    */
   @GET
   @Path("/find-by-type/{type}")
   @Produces(MediaType.APPLICATION_JSON)
   public ShortTypeInfo[] findByType(@PathParam("type") String type, @QueryParam("prefix") String prefix)
      throws CodeAssistantException
   {
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:type = '" + type.toUpperCase() + "'";
      if (prefix != null && !prefix.isEmpty())
      {
         sql += " AND exoide:className LIKE '" + prefix + "%'";
      }

      try
      {
         Session session = JcrUtils.getSession(repositoryService, sessionProviderService, wsName);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();

         ShortTypeInfo[] types = new ShortTypeInfo[(int)nodes.getSize()];
         int i = 0;
         while (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            types[i++] =
               new ShortTypeInfo((int)0L, node.getProperty("exoide:className").getString(), node.getProperty(
                  "exoide:fqn").getString(), node.getProperty("exoide:type").getString());
         }
         return types;
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }

   }

   @GET
   @Path("/class-doc")
   @Produces("text/html")
   public String getClassDoc(@QueryParam("fqn") String fqn) throws CodeAssistantException
   {
      String sql = "SELECT * FROM exoide:javaDoc WHERE exoide:fqn='" + fqn + "'";
      try
      {
         Session session = JcrUtils.getSession(repositoryService, sessionProviderService, wsName);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();
         //TODO
         String doc = new String();
         if (nodes.getSize() == 0)
            throw new CodeAssistantException(HTTPStatus.NOT_FOUND, "Not found");
         while (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            doc = node.getProperty("jcr:data").getString();
         }
         doc = doc.replaceAll("[ ]+\\*", "");
         // need to return valid HTML
         return "<html><head></head><body style=\"font-family: monospace;font-size: 12px;\">" + doc + "</body></html>";
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
      }
   }

}
