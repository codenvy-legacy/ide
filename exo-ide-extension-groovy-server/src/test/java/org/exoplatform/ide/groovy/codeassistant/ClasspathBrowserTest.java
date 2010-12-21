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
package org.exoplatform.ide.groovy.codeassistant;

import org.exoplatform.ide.groovy.codeassistant.extractors.ClassNamesExtractor;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ClasspathBrowserTest extends TestCase
{
   private String javaHome;
   
   
   @Override
   protected void setUp() throws Exception
   {
      javaHome = System.getProperty("java.home");
      String fileSeparator = System.getProperty("file.separator");
      javaHome = javaHome.substring(0,javaHome.lastIndexOf(fileSeparator)+1) + "src.zip";
   }
   
   public void testgetClassesNamesFromJavaSrc() throws IOException
   {
      List<String> classes = ClassNamesExtractor.getClassesNamesFromJar(javaHome);
      assertTrue(classes.contains("org.w3c.dom.Document"));
      assertTrue(classes.contains("java.lang.String"));
   }
   
   public void testgetClassesNamesFromJavaSrcPkg() throws IOException
   {
      List<String> classes = ClassNamesExtractor.getClassesNamesFromJar(javaHome,"java.lang");
      assertTrue(classes.contains("java.lang.String"));
      assertTrue(classes.contains("java.lang.Boolean"));
      
   }
   
 

}
