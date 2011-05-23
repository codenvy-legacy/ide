/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.exoplatform.ide.codeassistant.framework.server.impl.storage.FileFinder;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 23, 2011 evgen $
 *
 */
public class FileFinderTest
{

   private static String PATH = "src/test/resources/jsr311-api-1.0*.jar;";

   private static String WRONG_PATH = "src/test/resources/jars/jsr311-api-1.0*.jar;";

   @Test
   public void testFileFinder()
   {
      try
      {
         FileFinder finder = new FileFinder(PATH);
         List<String> fileList = finder.getFileList();
         assertEquals(2,fileList.size());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   @Test
   public void testFileFinderWrongPath()
   {
      try
      {
         FileFinder finder = new FileFinder(WRONG_PATH);
         List<String> fileList = finder.getFileList();
         assertEquals(0,fileList.size());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

}
