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

import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.groovydoc.GroovyParameter;
import org.codehaus.groovy.groovydoc.GroovyRootDoc;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.groovy.GroovyScriptService;
import org.exoplatform.ide.groovy.codeassistant.DocStorage;
import org.exoplatform.ide.groovy.codeassistant.SaveDocException;
import org.exoplatform.ide.groovy.codeassistant.extractors.DocExtractor;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/ide/doc-storage")
public class DocStorageJcrImpl implements DocStorage
{
   private final RepositoryService repositoryService;

   private final SessionProviderService sessionProviderService;

   private final String wsName;

   private final static String[] defaultPkgs = {"java.lang", "java.util", "java.io", "java.math", "java.text"};

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(DocStorageJcrImpl.class);

   public DocStorageJcrImpl(String wsName, RepositoryService repositoryService,
      SessionProviderService sessionProviderService)
   {
      this(wsName, repositoryService, sessionProviderService, defaultPkgs);
   }

   public DocStorageJcrImpl(String wsName, RepositoryService repositoryService,
      SessionProviderService sessionProviderService, String[] pkgs)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
      this.wsName = wsName;
      try
      {
         addDocsFromJavaSrc(pkgs);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private void addDocsFromJavaSrc(String[] pkgs) throws SaveDocException
   {
      try
      {
         String javaHome = System.getProperty("java.home");
         String fileSeparator = System.getProperty("file.separator");
         javaHome = javaHome.substring(0, javaHome.lastIndexOf(fileSeparator) + 1) + "src.zip";
         SessionProvider sp = sessionProviderService.getSystemSessionProvider(null);
         Session session = sp.getSession(wsName, repositoryService.getDefaultRepository());
         for (int i = 0; i < pkgs.length; i++)
         {
            LOG.info(">>>>>>>>>>>>>>>> Load JavaDoc from " + pkgs[i]);
            Map<String, GroovyRootDoc> roots = DocExtractor.extract(javaHome, pkgs[i]);
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

         session.save();
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
      catch (IOException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new SaveDocException(HTTPStatus.BAD_REQUEST, e.getMessage());
      }
      catch (RecognitionException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
      }
      catch (TokenStreamException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
      }
   }

   /**
    * Extract javaDoc from java source from getting package and save it. 
    * 
    * @param javaSrcPath
    * @param packageName
    * @throws SaveDocException
    */
   @POST
   @Path("/jar-source")
   public void addDocs(@QueryParam("jar") String jar, @QueryParam("package") String packageName)
      throws SaveDocException
   {

      try
      {
         SessionProvider sp = sessionProviderService.getSessionProvider(null);
         Session session = sp.getSession(wsName, repositoryService.getDefaultRepository());

         Map<String, GroovyRootDoc> roots = DocExtractor.extract(jar, packageName);
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
      catch (IOException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new SaveDocException(HTTPStatus.BAD_REQUEST, e.getMessage());
      }
      catch (RecognitionException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
      }
      catch (TokenStreamException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
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
   private void putDoc(Session session, GroovyClassDoc doc, String fqn) throws RepositoryException, UnsupportedEncodingException

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
             LOG.warn("Tryed ad name with same name and parametrs.");  
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

}
