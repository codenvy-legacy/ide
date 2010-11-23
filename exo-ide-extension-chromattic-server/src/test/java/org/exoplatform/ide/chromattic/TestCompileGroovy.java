/**
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
 *
 */

package org.exoplatform.ide.chromattic;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TestCompileGroovy extends BaseTest
{
   
   @Before
   public void setUp() throws Exception {
      super.setUp();
      
      Node groovyRepo = root.addNode("groovyRepository", "nt:folder");
      
      Node org = groovyRepo.addNode("org", "nt:folder");
      Node exoplatform = org.addNode("exoplatform", "nt:folder");
      Node test = exoplatform.addNode("test", "nt:folder");
      Node groovy = test.addNode("groovy", "nt:folder");
      
      Node test1 = groovy.addNode("Test1.groovy", "nt:file");
      test1 = test1.addNode("jcr:content", "nt:resource");
      test1.setProperty("jcr:mimeType", "script/groovy");
      test1.setProperty("jcr:lastModified", Calendar.getInstance());
      test1.setProperty("jcr:data", Thread.currentThread().getContextClassLoader().getResourceAsStream("test1.groovy"));
      session.save();      
   }
   
   @Test
   public void testCompileGroovy() throws Exception {
      System.out.println("TestCompileGroovy.testCompileGroovy()");
      
      NodeIterator nodeIter = root.getNodes();
      while (nodeIter.hasNext()) {
         Node node = nodeIter.nextNode();
         System.out.println("node name > " + node.getPath());
      }

      String repository = "db1";
      String workspace = "ws";
      String groovyRepo = "groovyRepository";
      String path = "/groovyRepository/org/exoplatform/test/groovy/Test1.groovy";
      
      //Class []classes = GroovyScriptCompiler.compile(repository, workspace, groovyRepo, path);
      
//      JcrGroovyCompiler compiler = new JcrGroovyCompiler();
//
//      compiler.getGroovyClassLoader().setResourceLoader(new JcrGroovyResourceLoader(new java.net.URL[]{new java.net.URL("jcr://db1/ws#/groovyRepository")}));
//      
////      compiler.getGroovyClassLoader().setResourceLoader(new JcrGroovyResourceLoader(new java.net.URL[]{new java.net.URL("jcr://" + REPOSITORY + "/" + WORKSPACE + "/groovyRepo")}));
//      
//      UnifiedNodeReference ref = new UnifiedNodeReference(REPOSITORY, WORKSPACE, "/groovyRepository/org/exoplatform/test/groovy/Test1.groovy");
//      Class<?>[] classes = compiler.compile(ref);
//      assertEquals(1, classes.length);
//      GroovyObject groovyObject = (GroovyObject)classes[0].newInstance();
      
      //assertEquals("groovy compiler test", groovyObject.invokeMethod("getMessage", new Object[0]));      
   }
   
//   public void setUp() throws Exception
//   {
//      super.setUp();
//      Node groovyRepo = root.addNode("groovyRepo", "nt:folder");
//      Node org = groovyRepo.addNode("org", "nt:folder");
//      Node exo = org.addNode("exoplatform", "nt:folder");
//      Node a = exo.addNode("A.groovy", "nt:file");
//      a = a.addNode("jcr:content", "nt:resource");
//      a.setProperty("jcr:mimeType", "script/groovy");
//      a.setProperty("jcr:lastModified", Calendar.getInstance());
//      a.setProperty("jcr:data", //
//         "package org.exoplatform\n" + //
//            " class A { String message = 'groovy compiler test' }");
//
//      Node test = exo.addNode("test", "nt:folder");
//      Node b = test.addNode("B.groovy", "nt:file");
//      b = b.addNode("jcr:content", "nt:resource");
//      b.setProperty("jcr:mimeType", "script/groovy");
//      b.setProperty("jcr:lastModified", Calendar.getInstance());
//      b.setProperty("jcr:data", //
//         "package org.exoplatform.test\n" + //
//            " import org.exoplatform.A\n" + //
//            " class B extends A {}");
//      session.save();
//   }
//
//   public void testGroovyDependency() throws Exception
//   {
//      JcrGroovyCompiler compiler = new JcrGroovyCompiler();
//      compiler.getGroovyClassLoader().setResourceLoader(
//         new JcrGroovyResourceLoader(new java.net.URL[]{new java.net.URL("jcr://db1/ws#/groovyRepo")}));
//      Class<?>[] classes =
//         compiler.compile(new UnifiedNodeReference("db1", "ws", "/groovyRepo/org/exoplatform/test/B.groovy"));
//      assertEquals(1, classes.length);
//      GroovyObject go = (GroovyObject)classes[0].newInstance();
//      assertEquals("groovy compiler test", go.invokeMethod("getMessage", new Object[0]));
//   }

}
