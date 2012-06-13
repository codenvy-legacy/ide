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
package org.exoplatform.ide.codeassistant.storage.lucene;

import org.exoplatform.ide.codeassistant.asm.JarParser;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.PackageParser;
import org.exoplatform.ide.codeassistant.storage.QDoxJavaDocExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneDataWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class for storing classes info and java docs info to lucene storage
 */
public class DataStorageWriter
{

   private final String pathToIndex;

   public DataStorageWriter(String pathToIndex)
   {
      this.pathToIndex = pathToIndex;
   }

   /**
    * Method add all classes from jars to lucene index
    * 
    * @param jars
    * @throws IOException
    * @throws SaveDataIndexException
    */
   public void writeBinaryJarsToIndex(Artifact[] artifacts) throws IOException, SaveDataIndexException
   {
      LuceneInfoStorage luceneInfoStorage = null;
      if (artifacts == null)
         return;
      try
      {
         luceneInfoStorage = new LuceneInfoStorage(pathToIndex);
         LuceneDataWriter writer = new LuceneDataWriter(luceneInfoStorage);

         for (Artifact artifact : artifacts)
         {
            Set<String> packages = new TreeSet<String>();
            File jarFile = new File(artifact.getPath());
            List<TypeInfo> typeInfos = JarParser.parse(jarFile);
            packages.addAll(PackageParser.parse(jarFile));
            writer.addTypeInfo(typeInfos, artifact.getArtifactString());
            writer.addPackages(packages, artifact.getArtifactString());
         }
      }
      finally
      {
         if (luceneInfoStorage != null)
         {
            luceneInfoStorage.closeIndexes();
         }
      }
   }

   /**
    * Method adds all java doc comments from source archives to lucene index
    * 
    * @param sourceJars
    * @throws IOException
    * @throws SaveDataIndexException
    */
   public void writeSourceJarsToIndex(Artifact[] artifacts) throws IOException, SaveDataIndexException
   {
      LuceneInfoStorage luceneInfoStorage = null;
      if (artifacts == null)
         return;
      try
      {
         luceneInfoStorage = new LuceneInfoStorage(pathToIndex);
         LuceneDataWriter writer = new LuceneDataWriter(luceneInfoStorage);

         QDoxJavaDocExtractor javaDocExtractor = new QDoxJavaDocExtractor();
         for (Artifact artifact : artifacts)
         {
            File jarFile = new File(artifact.getPath());
            InputStream zipStream = new FileInputStream(jarFile);
            try
            {
               Map<String, String> javaDocs = javaDocExtractor.extractZip(zipStream);
               writer.addJavaDocs(javaDocs, artifact.getArtifactString());
            }
            finally
            {
               zipStream.close();
            }
         }
      }
      finally
      {
         if (luceneInfoStorage != null)
         {
            luceneInfoStorage.closeIndexes();
         }
      }
   }
}
