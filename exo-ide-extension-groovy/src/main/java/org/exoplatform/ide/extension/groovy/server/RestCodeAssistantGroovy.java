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
package org.exoplatform.ide.extension.groovy.server;

import java.util.List;

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
import org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistantException;
import org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant;
import org.exoplatform.ide.codeassistant.framework.server.api.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.framework.server.api.TypeInfo;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

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
public class RestCodeAssistantGroovy
{

   //   private final RepositoryService repositoryService;
   //
   //   private final ThreadLocalSessionProviderService sessionProviderService;
   //
   //   private final String wsName;

   private final CodeAssistant codeAssistantStorage;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RestCodeAssistantGroovy.class);

   public RestCodeAssistantGroovy(CodeAssistant codeAssistantStorage)
   {
      this.codeAssistantStorage = codeAssistantStorage;
      //      this.repositoryService = repositoryService;
      //      this.sessionProviderService = sessionProviderService;
      //      this.wsName = wsName;
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
      TypeInfo info = codeAssistantStorage.getClassByFQN(fqn);
      if (info == null)
      {
         info = codeAssistantStorage.getClassByFQNFromProject(uriInfo.getBaseUri().toASCIIString(), fqn, location);
         if (info != null)
            return info;
      }
      else
      {
         return info;
      }

      if (LOG.isDebugEnabled())
         LOG.error("Class info for " + fqn + " not found");
      throw new CodeAssistantException(HTTPStatus.NOT_FOUND, "Class info for " + fqn + " not found");
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
      List<ShortTypeInfo> info = codeAssistantStorage.findFQNsByClassName(className);

      info.addAll(codeAssistantStorage.findFQNsByClassNameInProject(uriInfo.getBaseUri().toASCIIString(), className,
         location));

      return info;

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
      List<ShortTypeInfo> info = codeAssistantStorage.findFQNsByPrefix(prefix, where);
      
      if ("className".equals(where))
      {
         List<ShortTypeInfo> projectInfo =codeAssistantStorage.findFQNsByPrefixInProject(uriInfo.getBaseUri().toASCIIString(), prefix, location);
         if(projectInfo != null)
            info.addAll(projectInfo);
      }
      return info;
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
      return codeAssistantStorage.findByType(type, prefix);

   }

   @GET
   @Path("/class-doc")
   @Produces(MediaType.TEXT_HTML)
   public String getClassDoc(@QueryParam("fqn") String fqn) throws CodeAssistantException
   {
      return "<html><head></head><body style=\"font-family: monospace;font-size: 12px;\">"
         + codeAssistantStorage.getClassDoc(fqn) + "</body></html>";
   }
}
