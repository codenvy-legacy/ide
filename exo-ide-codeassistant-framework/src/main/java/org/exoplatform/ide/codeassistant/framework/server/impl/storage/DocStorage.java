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
package org.exoplatform.ide.codeassistant.framework.server.impl.storage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.groovydoc.GroovyParameter;
import org.codehaus.groovy.groovydoc.GroovyRootDoc;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.codeassistant.framework.server.api.GroovyAutocompletionConfig;
import org.exoplatform.ide.codeassistant.framework.server.api.JarEntry;
import org.exoplatform.ide.codeassistant.framework.server.extractors.DocExtractor;
import org.exoplatform.ide.codeassistant.framework.server.utils.JcrUtils;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

/**
 *  Service provide save javadoc groovydoc in storage.
 *  
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
//@Path("/ide/doc-storage")
public class DocStorage implements Startable
{
   private final RepositoryService repositoryService;

   private final ThreadLocalSessionProviderService sessionProviderService;

   private String wsName;

   private List<JarEntry> jars;

   private boolean runInThread;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(DocStorage.class);

   public DocStorage(RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService,
      InitParams initParams)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
      if (initParams != null)
      {
         GroovyAutocompletionConfig config =
            (GroovyAutocompletionConfig)initParams.getObjectParam("docstrorage.configuration").getObject();
         this.wsName = config.getWsName();
         this.jars = config.getJars();
         this.runInThread = config.isRunInThread();
      }

   }

   /**
    * @see org.picocontainer.Startable#start()
    */
   @Override
   public void start()
   {
      Runnable run = new Runnable()
      {

         @Override
         public void run()
         {
            try
            {
               addJavaDocsOnStartUp(jars);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      };

      if (jars != null && wsName != null)
         runTask(run, runInThread);
   }

   /**
    * @see org.picocontainer.Startable#stop()
    */
   @Override
   public void stop()
   {
   }

   private void runTask(Runnable run, boolean runInThread)
   {
      if (runInThread)
      {
         new Thread(run, "DocStorage").start();
      }
      else
      {
         run.run();
      }
   }

   private void addJavaDocsOnStartUp(List<JarEntry> jars) throws SaveDocException
   {
      try
      {
         SessionProvider sp = sessionProviderService.getSystemSessionProvider(null);
         Session session = sp.getSession(wsName, JcrUtils.getRepository(repositoryService));
         for (JarEntry entry : jars)
         {
            String path = entry.getJarPath();

            FileFinder fileFinder = new FileFinder(path);

            for (String jarFile : fileFinder.getFileList())
            {
               try
               {
                  LOG.debug("Load JavaDoc from jar - " + jarFile);
                  if (entry.getIncludePkgs() == null || entry.getIncludePkgs().isEmpty())
                  {
                     Map<String, GroovyRootDoc> roots = DocExtractor.extract(jarFile);
                     Set<String> keys = roots.keySet();
                     for (String key : keys)
                     {
                        GroovyClassDoc[] docs = roots.get(key).classes();
                        for (GroovyClassDoc doc : docs)
                        {
                           putDoc(session, doc, key + "." + doc.name());
                        }
                     }

                  }
                  else
                  {
                     for (String pkgs : entry.getIncludePkgs())
                     {
                        LOG.debug("Load JavaDoc from - " + pkgs);
                        Map<String, GroovyRootDoc> roots = DocExtractor.extract(jarFile, pkgs);
                        Set<String> keys = roots.keySet();
                        for (String key : keys)
                        {
                           GroovyClassDoc[] docs = roots.get(key).classes();
                           for (GroovyClassDoc doc : docs)
                           {
                              putDoc(session, doc, key + "." + doc.name());
                           }
                        }
                     }
                  }

               }
               catch (Exception e)
               {
                  LOG.debug("Could not load JavaDoc from " + jarFile);
               }

            }

         }

         session.save();
         LOG.debug("Load javadoc complete");
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new SaveDocException(HTTPStatus.BAD_REQUEST, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new SaveDocException(HTTPStatus.BAD_REQUEST, e.getMessage());
      }
   }

   /**
    * Adding to storage java doc by classes
    * 
    * @param session
    * @param doc
    * @param fqn
    * @throws RepositoryException
    * @throws UnsupportedEncodingException 
    */
   private void putDoc(Session session, GroovyClassDoc doc, String fqn) throws RepositoryException,
      UnsupportedEncodingException

   {
      Node base;
      if (!session.getRootNode().hasNode("dev-doc"))
      {
         base = session.getRootNode().addNode("dev-doc", "nt:folder");
      }
      base = session.getRootNode().getNode("dev-doc");

      String clazz = fqn;
      Node child = base;
      String[] seg = fqn.split("\\.");
      String path = new String();
      for (int i = 0; i < seg.length; i++)
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
         Node cls = child.addNode(clazz, "nt:file");
         Node content = cls.addNode("jcr:content", "exoide:classDoc");
         content.setProperty("jcr:data", doc.commentText());
         content.setProperty("jcr:lastModified", Calendar.getInstance());
         content.setProperty("jcr:mimeType", "text/plain");
         content.setProperty("exoide:fqn", clazz);
         putMethodDoc(cls.getParent(), doc, clazz);
      }
      session.save();
   }

   /**
    * Put methods doc
    * 
    * @param cls
    * @param doc
    * @param clsFqn
    * @throws ItemExistsException
    * @throws PathNotFoundException
    * @throws NoSuchNodeTypeException
    * @throws LockException
    * @throws VersionException
    * @throws ConstraintViolationException
    * @throws RepositoryException
    * @throws UnsupportedEncodingException
    */
   private void putMethodDoc(Node cls, GroovyClassDoc doc, String clsFqn) throws ItemExistsException,
      PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException,
      RepositoryException, UnsupportedEncodingException
   {
      Node methodsDoc;
      if (!cls.hasNode("methods-doc"))
      {
         methodsDoc = cls.addNode("methods-doc", "nt:folder");
      }
      else
      {
         methodsDoc = cls.getNode("methods-doc");
      }
      GroovyMethodDoc[] docs = doc.methods();
      for (int i = 0; i < docs.length; i++)
      {
         String methName = docs[i].name() + URLEncoder.encode(array2string(docs[i].parameters()), "UTF-8");
         if (!methodsDoc.hasNode(methName))
         {
            Node method = methodsDoc.addNode(methName, "nt:file");
            Node content = method.addNode("jcr:content", "exoide:methodDoc");
            content.setProperty("jcr:data", docs[i].getRawCommentText());
            content.setProperty("jcr:lastModified", Calendar.getInstance());
            content.setProperty("jcr:mimeType", "text/plain");
            content.setProperty("exoide:fqn", clsFqn + "." + docs[i].name() + array2string(docs[i].parameters()));
         }
         else
         {
            //TODO: This can occurs if try add doc for method with same name same parametrs like this 
            // 1 : newInstance(Class<?> componentType, int length)
            // 2 : newInstance(Class<?> componentType, int... dimensions)
            // paramets different int != int... but Groovy doc parser don't resolve this situation 
            // in this case method signature was same
            // newInstance(Class, int)
            // newInstance(Class, int)

            if (LOG.isDebugEnabled())
               LOG.debug("Tryed ad name with same name and parameters.");
         }

      }
      cls.getSession().save();
   }

   /**
    * @param a
    * @return
    */
   private static String array2string(GroovyParameter[] a)
   {
      if (a == null)
         return "null";
      int iMax = a.length - 1;
      if (iMax == -1)
         return "()";

      StringBuilder b = new StringBuilder();
      b.append('(');
      for (int i = 0;; i++)
      {
         b.append(a[i].typeName());
         if (i == iMax)
            return b.append(')').toString();
         b.append(", ");
      }
   }

   public void addDocsFromSource(String pathToFile) throws SaveDocException
   {
      //TODO:
   }

   public String getDocStorageWorkspace()
   {
      return wsName;
   }
}
