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

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.groovy.JcrUtils;
import org.exoplatform.ide.groovy.codeassistant.bean.JarEntry;
import org.exoplatform.ide.groovy.codeassistant.bean.TypeInfo;
import org.exoplatform.ide.groovy.codeassistant.extractors.ClassNamesExtractor;
import org.exoplatform.ide.groovy.codeassistant.extractors.TypeInfoExtractor;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
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

/**
 * Service provide save meta information about classes in storage.
 * Information save according to hierarchy  in packet.
 * For exapmle: 
 * for class org.exoplatform.ide.groovy.codeassistant.ClassInfoStorage
 * it will be
 * /org
 *  /org.exoplatform
 *   /org.exoplatform.ide
 *    /org.exoplatform.ide.groovy
 *     /org.exoplatform.ide.groovy.codeassistant
 *      /org.exoplatform.ide.groovy.codeassistant.ClassInfoStorage
 * 
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/ide/class-info-storage")
public class ClassInfoStrorage
{

   private ThreadLocalSessionProviderService sessionProviderService;

   private RepositoryService repositoryService;

   private final String wsName;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(ClassInfoStrorage.class);

   public ClassInfoStrorage(ThreadLocalSessionProviderService sessionProvider, RepositoryService repositoryService,
      String wsName, final List<JarEntry> jars, boolean runInThread)
   {
      this.sessionProviderService = sessionProvider;
      this.repositoryService = repositoryService;
      this.wsName = wsName;
      Runnable run = new Runnable()
      {

         @Override
         public void run()
         {
            try
            {
               addClassesOnStartUp(jars);
            }
//            catch (SaveClassInfoException e)
//            {
//               if (LOG.isDebugEnabled())
//                  e.printStackTrace();
            catch (Throwable e) {
               e.printStackTrace();
            }
         }
      };

      runTask(run, runInThread);
   }

   private void runTask(Runnable run, boolean runInThread)
   {
      if (runInThread)
      {
         new Thread(run, "ClassInfoStorage").start();
      }
      else
      {
         run.run();
      }
   }

   /**
    * Save information about classes in jar. Can be filtering by package name 
    * 
    * @param jarPath the path to jar
    * @param packageName the package name for filtering classes if set to null save
    *        info about all classes in jar 
    * @return true if save info successfully
    * @throws SaveClassInfoException
    */
   @POST
   @Path("/jar")
   public void addClassesFromJar(@QueryParam("jar-path") String jarPath, @QueryParam("package") String packageName)
      throws SaveClassInfoException
   {
      try
      {
         Thread thread = Thread.currentThread();
         ClassLoader classLoader = thread.getContextClassLoader();
         List<String> fqns = ClassNamesExtractor.getCompiledClassesFromJar(jarPath, packageName);
         for (String fqn : fqns)
         {
            putClass(classLoader, JcrUtils.getSession(repositoryService, sessionProviderService, wsName), fqn);
         }
      }
      catch (Exception e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());

      }
   }

   /**
    * Save information about class. 
    * 
    * @param fqn the Canonical Name of classes
    * @return
    * @throws SaveClassInfoException
    */
   @POST
   @Path("/class")
   public void addClass(@QueryParam("fqn") String fqn) throws SaveClassInfoException
   {
      try
      {
         Thread thread = Thread.currentThread();
         ClassLoader classLoader = thread.getContextClassLoader();
         putClass(classLoader, JcrUtils.getSession(repositoryService, sessionProviderService, wsName), fqn);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
   }

   /**
    * Save information about classes in source. Can be filtering by package name 
    * Can be used for save information about classes from jdk source (src.zip) 
    * 
    * @param javaSrcPath the path to jar
    * @param packageName the package name for filtering classes if set to null save
    *        info about all classes in jar 
    * @return true if save info successfully
    * @throws SaveClassInfoException
    */
   @POST
   @Path("/java")
   public void addClassesFromJavaSource(@QueryParam("java-source-path") String javaSrcPath,
      @QueryParam("package") String packageName) throws SaveClassInfoException
   {
      try
      {
         Thread thread = Thread.currentThread();
         ClassLoader classLoader = thread.getContextClassLoader();
         List<String> fqns = ClassNamesExtractor.getSourceClassesFromJar(javaSrcPath, packageName);
         for (String fqn : fqns)
         {
            putClass(classLoader, JcrUtils.getSession(repositoryService, sessionProviderService, wsName), fqn);
         }
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (IncompatibleClassChangeError e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (JsonException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (IOException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (ClassNotFoundException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
   }

   /**
    * {@inheritDoc}
    */

   //TODO:for prototype client side
   public void addClassesOnStartUp(List<JarEntry> jars) throws SaveClassInfoException
   {
      try
      {
         Thread thread = Thread.currentThread();
         ClassLoader classLoader = thread.getContextClassLoader();
         ManageableRepository repository = JcrUtils.getRepository(repositoryService);
         Session session = sessionProviderService.getSystemSessionProvider(null).getSession(wsName, repository);
         for (JarEntry entry : jars)
         {
            String path = entry.getJarPath();
            LOG.info("Load ClassInfo from jar -" + entry.getJarPath());
            
            List<String> fqns = new ArrayList<String>();
            if (entry.getIncludePkgs() == null || entry.getIncludePkgs().isEmpty())
            {               
               fqns.addAll(ClassNamesExtractor.getCompiledClassesFromJar(path));
            }
            else
            {
               for (String pkg : entry.getIncludePkgs())
               {
                  LOG.info("Load ClassInfo from - " + pkg);
                  fqns.addAll(ClassNamesExtractor.getCompiledClassesFromJar(path, pkg));
               }
            }

            for (String fqn : fqns)
            {
               putClass(classLoader, session, fqn);
            }
            
         }
         LOG.info("Class info load complete");
      }
      catch (RepositoryException e)
      {
         e.printStackTrace();
         
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (IOException e)
      {
         e.printStackTrace();
         
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (IncompatibleClassChangeError e)
      {
         e.printStackTrace();
         
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (JsonException e)
      {
         e.printStackTrace();
         
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         e.printStackTrace();
         
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (ClassNotFoundException e)
      {
         e.printStackTrace();
         
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO: need think about status
         throw new SaveClassInfoException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
   }

   private void putClass(ClassLoader classLoader, Session session, String fqn) throws RepositoryException,
      ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
      ConstraintViolationException, IncompatibleClassChangeError, ValueFormatException, JsonException,
      AccessDeniedException, InvalidItemStateException, ClassNotFoundException
   {
      Node base;
      if (!session.getRootNode().hasNode("classpath"))
      {
         base = session.getRootNode().addNode("classpath", "nt:folder");
      }
      base = session.getRootNode().getNode("classpath");

      String clazz = fqn;
      Class<?> cls = classLoader.loadClass(clazz);
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
      session.save();
   }
}
