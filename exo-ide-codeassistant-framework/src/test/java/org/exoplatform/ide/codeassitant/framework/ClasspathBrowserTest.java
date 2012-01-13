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

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.exoplatform.ide.codeassistant.framework.server.extractors.ClassNamesExtractor;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class ClasspathBrowserTest extends TestCase
{
   private String javaHome;

   @Override
   protected void setUp() throws Exception
   {
      javaHome = System.getProperty("java.src");
   }

   public void testgetClassesNamesFromJavaSrc() throws IOException
   {
      List<String> classes = ClassNamesExtractor.getSourceClassesFromJar(javaHome);
      assertTrue(classes.contains("org.w3c.dom.Document"));
      assertTrue(classes.contains("java.lang.String"));
   }

   public void testgetClassesNamesFromJavaSrcPkg() throws IOException
   {
      List<String> classes = ClassNamesExtractor.getSourceClassesFromJar(javaHome, "java.lang");
      assertTrue(classes.contains("java.lang.String"));
      assertTrue(classes.contains("java.lang.Boolean"));

   }

}
