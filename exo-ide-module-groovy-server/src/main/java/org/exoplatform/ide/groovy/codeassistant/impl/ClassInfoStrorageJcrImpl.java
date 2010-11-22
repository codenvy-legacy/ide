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
import org.exoplatform.ide.groovy.codeassistant.ClassInfoStorage;
import org.exoplatform.ide.groovy.codeassistant.SaveClassInfoException;
import org.exoplatform.ide.groovy.codeassistant.bean.TypeInfo;
import org.exoplatform.ide.groovy.codeassistant.extractors.ClassNamesExtractor;
import org.exoplatform.ide.groovy.codeassistant.extractors.TypeInfoExtractor;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;

import java.util.Calendar;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/ide/class-info-storage")
public class ClassInfoStrorageJcrImpl implements ClassInfoStorage
{

   private SessionProviderService sessionProvider;

   private RepositoryService repositoryService;

   private final String wsName;

   public ClassInfoStrorageJcrImpl(SessionProviderService sessionProvider, RepositoryService repositoryService,
      String wsName)
   {
      this.sessionProvider = sessionProvider;
      this.repositoryService = repositoryService;
      this.wsName = wsName;
      System.out.println(" >>>>>>>>>>>>>>>> Load ClassInfo from java.lang <<<<<<<<<<<<<<<<<<<<<<<<<,");
      try
      {
         addClassesFromJavaLangSource();
      }
      catch (SaveClassInfoException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * {@inheritDoc}
    */
   @POST
   @Path("/jar")
   @RolesAllowed("administrators")
   public void addClassesFormJar(@QueryParam("jar-path") String jarPath, @QueryParam("package") String packageName)
      throws SaveClassInfoException
   {
      try
      {
         Thread thread = Thread.currentThread();
         ClassLoader classLoader = thread.getContextClassLoader();
         Repository repository = repositoryService.getDefaultRepository();
         Session session = repository.login(wsName);
         List<String> fqns = ClassNamesExtractor.getClassesNamesInJar(jarPath, packageName);
         for (String fqn : fqns)
         {
            putClass(classLoader, session, fqn);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
         
      }
   }

   /**
    * {@inheritDoc}
    */
   @POST
   @Path("/class")
   @RolesAllowed("administrators")
   public void addClass(@QueryParam("fqn") String fqn) throws SaveClassInfoException
   {
      try
      {
         Thread thread = Thread.currentThread();
         ClassLoader classLoader = thread.getContextClassLoader();
         Repository repository = repositoryService.getDefaultRepository();
         Session session = repository.login(wsName);
         putClass(classLoader, session, fqn);
         session.save();
      }
      catch (Exception e)
      {
         e.printStackTrace();
       //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
   }

  

   /**
    * {@inheritDoc}
    */
   @POST
   @Path("/java")
   @RolesAllowed("administrators")
   public void addClassesFromJavaSource(@QueryParam("java-source-path") String javaSrcPath,
      @QueryParam("package") String packageName) throws SaveClassInfoException
   {
      try
      {
         Thread thread = Thread.currentThread();
         ClassLoader classLoader = thread.getContextClassLoader();
         Repository repository = repositoryService.getDefaultRepository();
         Session session = repository.login(wsName);
         List<String> fqns = ClassNamesExtractor.getClassesNamesFromJavaSrc(javaSrcPath, packageName);
         for (String fqn : fqns)
         {
            putClass(classLoader, session, fqn);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
   }
   
   /**
    * {@inheritDoc}
    */
   
   //TODO:for prototype client side
   @POST
   @Path("/java-lang")
   @RolesAllowed("administrators")
   public void addClassesFromJavaLangSource() throws SaveClassInfoException
   {
      try
      {
         Thread thread = Thread.currentThread();
         ClassLoader classLoader = thread.getContextClassLoader();
         ManageableRepository repository = repositoryService.getDefaultRepository();
         Session session = sessionProvider.getSystemSessionProvider(null).getSession(wsName, repository);
         String javaHome = System.getProperty("java.home");
         String fileSeparator = System.getProperty("file.separator");
         javaHome = javaHome.substring(0, javaHome.lastIndexOf(fileSeparator) + 1) + "src.zip";
         List<String> fqns = ClassNamesExtractor.getClassesNamesFromJavaSrc(javaHome, "java.lang");
         for (String fqn : fqns)
         {
            putClass(classLoader, session, fqn);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
   }
   
   private void putClass(ClassLoader classLoader, Session session, String fqn) throws RepositoryException,
   ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
   ConstraintViolationException, IncompatibleClassChangeError, ValueFormatException, JsonException,
   AccessDeniedException, InvalidItemStateException
{
   Node base;
   if (!session.getRootNode().hasNode("classpath"))
   {
      base = session.getRootNode().addNode("classpath", "nt:folder");
   }
   base = session.getRootNode().getNode("classpath");
   try
   {
      String clazz = fqn;
      Class cls = classLoader.loadClass(clazz);
      TypeInfo cd = TypeInfoExtractor.extract(cls);
      Node child = base;
      String[] seg = fqn.split("\\.");
      String path = new String();
      for (int i = 0; i < seg.length - 1; i++)
      {
         path = path + seg[i];
         if (!child.hasNode(path))
         {
            child = child.addNode(path, "nt:folder");
         }
         else
         {
            child = child.getNode(path);
         }
         path = path + ".";
      }
      if (!child.hasNode(clazz))
      {
         child = child.addNode(clazz, "nt:file");
         child = child.addNode("jcr:content", "exoide:classDescription");
         JsonGeneratorImpl jsonGenerator = new JsonGeneratorImpl();
         child.setProperty("jcr:data", jsonGenerator.createJsonObject(cd).toString());
         child.setProperty("jcr:lastModified", Calendar.getInstance());
         child.setProperty("jcr:mimeType", "text/plain");
         child.setProperty("exoide:className", clazz.substring(clazz.lastIndexOf(".") + 1));
         child.setProperty("exoide:fqn", clazz);
         child.setProperty("exoide:type", cd.getType().toString());
         child.setProperty("exoide:modifieres", cd.getModifiers());
      }
   }
   catch (ClassNotFoundException e)
   {
      e.printStackTrace();
   }
   session.save();
}
}
