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

import org.chromattic.dataobject.NodeTypeFormat;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.junit.Before;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import javax.jcr.Node;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TestDataObjectService extends BaseTest
{
   
   /** . */
   private static final String dataObjectGroovy =
       "@org.chromattic.api.annotations.PrimaryType(name=\"nt:unstructured\")\n" +
       "class DataObject {\n" +
       "@org.chromattic.api.annotations.Property(name = \"a\") def String a;\n" +
       "}";
   
   private static final String ntd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
   "<!--Node type generation prototype-->" + 
   "<nodeTypes xmlns:jcr=\"http://www.jcp.org/jcr/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\">"+
     "<nodeType name=\"nt:unst\" isMixin=\"false\" hasOrderableChildNodes=\"false\">"+
       "<supertypes>"+
         "<supertype>nt:base</supertype>"+
         "<supertype>mix:referenceable</supertype>"+
       "</supertypes>"+
       "<propertyDefinitions>"+
         "<propertyDefinition name=\"a\" requiredType=\"String\" autoCreated=\"false\" mandatory=\"false\" onParentVersion=\"COPY\" protected=\"false\" multiple=\"false\">"+
           "<valueConstraints/>"+
         "</propertyDefinition>"+
       "</propertyDefinitions>"+
       "<childNodeDefinitions/>"+
     "</nodeType>"+
   "</nodeTypes>";

   
   @Before
   public void setUp() throws Exception {
      super.setUp();
      
      Node groovyRepo = root.addNode("dependencies", "nt:folder");
      Node test1 = groovyRepo.addNode("DataObject.groovy", "nt:file");
      test1 = test1.addNode("jcr:content", "nt:resource");
      test1.setProperty("jcr:mimeType", "script/groovy");
      test1.setProperty("jcr:lastModified", Calendar.getInstance());
      test1.setProperty("jcr:data", dataObjectGroovy);
      session.save();      
   }
   
   
   public void testNodeTypeGenration() throws Exception {
      String location = "/ide-vfs-webdav/" + "db1/ws/dependencies/DataObject.groovy";
      ContainerResponse cres =
         launcher.service("POST",
            "/ide/chromattic/generate-nodetype-definition?do-location=" + location + "&nodeTypeFormat=EXO", "", null, null,
            null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      String s = (String)cres.getEntity();
      System.out.println("Generated node types " + s);
    }
   
   public void testNodeTypeGenrationDOScriptNotFound() throws Exception {
      String location = "/ide-vfs-webdav/" + "db1/ws/dependencies/DataObjectNotFound.groovy";
      ContainerResponse cres =
         launcher.service("POST",
            "/ide/chromattic/generate-nodetype-definition?do-location=" + location + "&nodeTypeFormat=EXO", "", null, null,
            null, null);
      assertEquals(HTTPStatus.INTERNAL_ERROR, cres.getStatus());
      String s = (String)cres.getEntity();
      System.out.println("Generated node types " + s);
    }
   
   
   public void testNodeTypeGenrationLocationNotFound() throws Exception {
      ContainerResponse cres =
         launcher.service("POST",
            "/ide/chromattic/generate-nodetype-definition?nodeTypeFormat=EXO", "", null, null,
            null, null);
      assertEquals(HTTPStatus.INTERNAL_ERROR, cres.getStatus());
      String s = (String)cres.getEntity();
      System.out.println("Generated node types " + s);
    }
   
   public void testNodeTypeGenrationCND() throws Exception {
      String location = "/ide-vfs-webdav/" + "db1/ws/dependencies/DataObject.groovy";
      ContainerResponse cres =
         launcher.service("POST",
            "/ide/chromattic/generate-nodetype-definition?do-location=" + location + "&nodeTypeFormat=CND", "", null, null,
            null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      String s = (String)cres.getEntity();
      System.out.println("Generated node types " + s);
    }
   
   public void testNodeType() throws Exception {
//      String location = "/ide-vfs-webdav/" + "db1/ws/dependencies/DataObject.groovy";
      ContainerResponse cres =
         launcher.service("POST",
            "/ide/chromattic/register-nodetype/EXO/4", "", null, ntd.getBytes(),
            null, null);
      assertEquals(HTTPStatus.NO_CONTENT, cres.getStatus());
    
    }
   

}
