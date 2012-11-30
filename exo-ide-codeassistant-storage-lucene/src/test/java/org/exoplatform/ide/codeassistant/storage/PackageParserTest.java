/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.storage;

import org.junit.Test;

import java.io.FileInputStream;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 2:59:49 PM Mar 5, 2012 evgen $
 * 
 */
public class PackageParserTest
{
   private static final String FILE_SEPARATOR = System.getProperty("file.separator");

   private static final String PATH_TO_RT_JAR = System.getProperty("java.home") + FILE_SEPARATOR + "lib"
      + FILE_SEPARATOR + "rt.jar";
   
   @Test
   public void parseJar() throws Exception
   {
      Set<String> set = PackageParser.parse(new FileInputStream(PATH_TO_RT_JAR));
      assertThat(set).contains("java","java.lang", "java.util", "java.io");
   }
   
   @Test
   public void noClass() throws Exception
   {
      Set<String> set = PackageParser.parse(new FileInputStream(PATH_TO_RT_JAR));
      assertThat(set).excludes("java.lang.Object.java", "java.io.File");
   }
}
