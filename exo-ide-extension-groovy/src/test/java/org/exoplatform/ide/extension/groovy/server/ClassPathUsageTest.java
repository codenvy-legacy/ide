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
package org.exoplatform.ide.extension.groovy.server;

import java.io.ByteArrayInputStream;

import org.everrest.core.impl.provider.json.JsonException;
import org.exoplatform.ide.codeassistant.framework.server.utils.DependentResources;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPath;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPathEntry;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyScriptServiceUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 28, 2010 $
 *
 */
public class ClassPathUsageTest 
{
   private static String REPOSITORY_NAME = "repository";
   
   private final String correctClassPathFile =
      "{\"entries\": [{\"kind\": \"file\",\"path\": \"dev-monit#/Test.groovy\"},"
         + "{\"kind\": \"dir\",\"path\": \"/dev-monit#/test/\"}]}";

   private final String emptyEntriesClassPathFile = "{\"entries\": []}";

   private final String wrongTypeClassPathFile =
      "{\"entries\": [{\"kind\": \"file\",\"path\": \"dev-monit#/Test.groovy\"},"
         + "{\"kind\": \"\",\"path\": \"dev-monit#/test1/\"},"
         + "{\"kind\": \"filee\",\"path\": \"dev-monit#/test2/\"},"
         + "{\"kind\": \"dir\",\"path\": \"dev-monit#/test3/\"}" + "]}";

   @Test
   public void classPathFileCorrect() throws JsonException
   {
      GroovyClassPath classPath =
         GroovyScriptServiceUtil.json2ClassPath(new ByteArrayInputStream(correctClassPathFile.getBytes()));
      Assert.assertEquals(classPath.getEntries().length, 2);
      GroovyClassPathEntry entry = classPath.getEntries()[0];
      Assert.assertEquals(entry.getKind(), "file");
      Assert.assertEquals(entry.getPath(), "dev-monit#/Test.groovy");
      entry = classPath.getEntries()[1];
      Assert.assertEquals(entry.getKind(), "dir");
      Assert.assertEquals(entry.getPath(), "/dev-monit#/test/");

      DependentResources dependentResources = new DependentResources(REPOSITORY_NAME, classPath);
      Assert.assertEquals(dependentResources.getFileSources().size(), 1);
      Assert.assertEquals(dependentResources.getFolderSources().size(), 1);
   }

   @Test
   public void classPathFileWrongType() throws JsonException
   {
      GroovyClassPath classPath =
         GroovyScriptServiceUtil.json2ClassPath(new ByteArrayInputStream(wrongTypeClassPathFile.getBytes()));
      Assert.assertEquals(classPath.getEntries().length, 4);

      DependentResources dependentResources = new DependentResources(REPOSITORY_NAME, classPath);
      Assert.assertEquals(dependentResources.getFileSources().size(), 1);
      Assert.assertEquals(dependentResources.getFileSources().get(0), "jcr://repository/dev-monit#/Test.groovy");

      Assert.assertEquals(dependentResources.getFolderSources().size(), 1);
      Assert.assertEquals(dependentResources.getFolderSources().get(0), "jcr://repository/dev-monit#/test3/");
   }

   @Test
   public void classPathFileEmptyEntries() throws JsonException
   {
      GroovyClassPath classPath =
         GroovyScriptServiceUtil.json2ClassPath(new ByteArrayInputStream(emptyEntriesClassPathFile.getBytes()));
      Assert.assertEquals(classPath.getEntries().length, 0);

      DependentResources dependentResources = new DependentResources(REPOSITORY_NAME, classPath);
      Assert.assertEquals(dependentResources.getFileSources().size(), 0);
      Assert.assertEquals(dependentResources.getFolderSources().size(), 0);
   }
   
   @Test
   public void emptyClassPathFile() throws JsonException
   {
      GroovyClassPath classPath =
         GroovyScriptServiceUtil.json2ClassPath(new ByteArrayInputStream("".getBytes()));
      Assert.assertNull(classPath);
   }
}
