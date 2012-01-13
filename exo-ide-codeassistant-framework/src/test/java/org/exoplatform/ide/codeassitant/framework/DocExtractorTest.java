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
package org.exoplatform.ide.codeassitant.framework;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;

import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.groovydoc.GroovyRootDoc;
import org.exoplatform.ide.codeassistant.framework.server.extractors.DocExtractor;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class DocExtractorTest extends TestCase
{

   private String javaHome;

   private static final String UUID_RANDOMUUID_COMENT =
      "* Static factory to retrieve a type 4 (pseudo randomly generated) UUID.";

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      javaHome = System.getProperty("java.src");
   }

   public void testExtractDocFromJavaSource() throws RecognitionException, TokenStreamException, IOException
   {
      Map<String, GroovyRootDoc> roots = DocExtractor.extract(javaHome, "java.util");
      assertTrue(roots.containsKey("java.util"));
      GroovyRootDoc doc = roots.get("java.util");
      assertNotNull(doc);
      GroovyClassDoc classDoc = getClassDoc(doc, "UUID");
      assertNotNull(classDoc);
      GroovyMethodDoc methodDoc = getMethodDoc(classDoc, "randomUUID");
      assertNotNull(methodDoc);
      assertTrue(methodDoc.getRawCommentText().contains(UUID_RANDOMUUID_COMENT));
   }

   private GroovyClassDoc getClassDoc(GroovyRootDoc doc, String className)
   {
      GroovyClassDoc[] classDocs = doc.classes();
      for (GroovyClassDoc classDoc : classDocs)
      {
         if (classDoc.name().equals(className))
         {
            return classDoc;
         }
      }
      return null;
   }

   private GroovyMethodDoc getMethodDoc(GroovyClassDoc doc, String method)
   {
      GroovyMethodDoc[] docs = doc.methods();
      for (GroovyMethodDoc methodDoc : docs)
      {
         if (methodDoc.name().equals(method))
         {
            return methodDoc;
         }
      }
      return null;
   }

}
