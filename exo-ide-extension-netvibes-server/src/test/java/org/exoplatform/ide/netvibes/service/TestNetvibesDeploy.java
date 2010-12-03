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
package org.exoplatform.ide.netvibes.service;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;

import javax.lang.model.element.Modifier;


/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 18, 2010 2:05:14 PM evgen $
 *
 */
public class TestNetvibesDeploy extends TestCase
{
   
   @Before
   public void setUp()
   {
      String containerConf = TestNetvibesDeploy.class.getResource("/conf/standalone/test-configuration.xml").toString();

      try
      {
         StandaloneContainer.addConfigurationURL(containerConf);
      }
      catch (MalformedURLException e)
      {
         fail();
         e.printStackTrace();
      }

   }
   
   @Test
   public void testDeploy()
   {
      NetvibesWidgetPreviewService service = new NetvibesWidgetPreviewService(null);
      File file = new File("src/test/java/org/exoplatform/ide/netvibes/service/test.xml");
      StringBuilder content = new StringBuilder();
      try
      {
              BufferedReader input = new BufferedReader(new FileReader(file));
              try
              {
                 String line = null;
     
                 while ((line = input.readLine()) != null)
                 {
                     content.append(line);
                     content.append('\n');
                  }
               }
               finally
               {
                  input.close();
               }
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
      
         
      
      System.out.println("TestNetvibesDeploy.testDeploy()"+content.toString());
      System.out.println(service.deployNetvibesWidget(content.toString(), "vitaly.parfonov@gmail.com", "1234qwer", "lofshagBicpoorelinha", "DoigFogIvtyejvavyinn"));
   }
   
   
   private String temp = 
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
     +"<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:widget=\"http://www.netvibes.com/ns/\">"
        +"<title type=\"text\"><![CDATA[Test eXo service]]></title>"
        +"<summary type=\"text\"><![CDATA[Test eXo service]]></summary>"
        +"<content type=\"text\"><![CDATA[Test eXo service Test eXo service Test eXo service Test eXo service]]></content>"
        +"<link rel=\"source\" href=\"http://downloads.exoplatform.org/ide/nv.html\"/>"
           +"<link rel=\"thumbnail\" href=\"http://downloads.exoplatform.org/ide/chrysler.logo.jpg\"/>"
              +"<widget:version>1.0</widget:version>"
              +"<widget:originalAuthor>vitaly.parfonov@gmail.com</widget:originalAuthor>"
              +"<category scheme=\"http://eco.netvibes.com/category\" term=\"2\" label=\"Business\"/>"
                 +"<category scheme=\"http://eco.netvibes.com/tag\" term=\"television\" label=\"Télévision\"/>"
                    +"<category scheme=\"http://eco.netvibes.com/tag\" term=\"media\" label=\"Média\"/>"
                       +"<category scheme=\"http://eco.netvibes.com/region\" term=\"fr\" label=\"France\"/>"
                          +"<category scheme=\"http://eco.netvibes.com/lang\" term=\"en_US\" label=\"English\"/>"
                             +"</entry>";
}
