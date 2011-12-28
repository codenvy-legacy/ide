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
package org.exoplatform.ide.codeassistant.storage.lucene;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Ignore
public class TestCodeAssistantStorageCreator
{
   private static final String FILE_SEPARATOR = System.getProperty("file.separator");

   private static final String PATH_TO_RT_JAR = System.getProperty("java.home") + FILE_SEPARATOR + "lib"
      + FILE_SEPARATOR + "rt.jar";

   @Test
   public void testClassStorageCreation() throws IOException, CodeAssistantException
   {
      List<String> jars = new ArrayList<String>();
      jars.add(PATH_TO_RT_JAR);
      String pathToIndex = "target/index";

      ClassesInfoStorageWriter.writeJarsToIndex(pathToIndex, jars);

      LuceneInfoStorage luceneInfoStorage = new LuceneInfoStorage(pathToIndex);
      LuceneCodeAssistantStorage storage = new LuceneCodeAssistantStorage(luceneInfoStorage);

      assertTrue(storage.getClasses("").size() > 0);
   }
}
