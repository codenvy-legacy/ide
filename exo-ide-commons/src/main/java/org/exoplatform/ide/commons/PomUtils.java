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
package org.exoplatform.ide.commons;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: PomUtils.java Oct 18, 2012 vetal $
 *
 */
public class PomUtils
{

   /**
    * Parse given stream to <code>Pom</code>.
    * Will be useful for getting information about Maven artifact like groupId, artifactId and version   
    *
    * @param stream
    * @return
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    * @throws XPathExpressionException
    */
   public static Pom parse(InputStream stream) throws ParserConfigurationException, SAXException, IOException,
      XPathExpressionException
   {
      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = domFactory.newDocumentBuilder();
      Document doc = builder.parse(stream);
      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();
      return new Pom(
         getVersionId(xpath, doc),
         getGroupId(xpath, doc),
         getArtifactId(xpath, doc),
         getPackaging(xpath, doc),
         getModules(xpath, doc),
         getSourcePath(xpath, doc)
      );
   }

   /**
    * @param xpath
    * @param doc
    * @throws XPathExpressionException
    */
   private static String getGroupId(XPath xpath, Document doc) throws XPathExpressionException
   {
      String groupId = xpath.compile("/project/groupId/text()").evaluate(doc);
      if (groupId == null || groupId.isEmpty())
      {
         groupId = xpath.compile("/project/parent/groupId/text()").evaluate(doc);
      }
      return groupId;
   }

   /**
    * @throws XPathExpressionException
    */
   private static String getArtifactId(XPath xpath, Document doc) throws XPathExpressionException
   {
      return xpath.compile("/project/artifactId/text()").evaluate(doc);
   }

   /**
    * @throws XPathExpressionException
    */
   private static String getPackaging(XPath xpath, Document doc) throws XPathExpressionException
   {
      String packaging = xpath.compile("/project/packaging/text()").evaluate(doc);
      if (packaging == null || packaging.isEmpty())
      {
         packaging = "jar";//by default Maven set packaging to jar
      }
      return packaging;
   }


   /**
    * @throws XPathExpressionException
    */
   private static String getVersionId(XPath xpath, Document doc) throws XPathExpressionException
   {

      String version = xpath.compile("/project/version/text()").evaluate(doc);
      if (version == null || version.isEmpty())
      {
         version = xpath.compile("/project/parent/version/text()").evaluate(doc);
      }
      return version;
   }

   private static String getSourcePath(XPath xpath, Document doc) throws XPathExpressionException
   {
      String sourcePath = xpath.compile("/project/build/sourceDirectory/text()").evaluate(doc);
      if (sourcePath == null || sourcePath.isEmpty())
      {
         sourcePath = "src/main/java";
      }
      return sourcePath;
   }

   /**
    * @throws XPathExpressionException
    */
   private static List<String> getModules(XPath xpath, Document doc) throws XPathExpressionException
   {
      String[] rawModules = xpath.compile("/project/modules").evaluate(doc).split("\n");

      List<String> modules = new ArrayList<String>();
      if (rawModules.length > 0)
      {
         for (String module : rawModules)
         {
            if (!module.trim().isEmpty())
            {
               modules.add(module.trim());
            }
         }
      }

      return modules;
   }

   public static class Pom
   {
      private final String version;

      private final String groupId;

      private final String artifactId;

      private final String packaging;

      private final List<String> modules;

      private final String sourcePath;

      /**
       * @param version
       * @param groupId
       * @param artifactId
       * @param packaging
       * @param modules
       */
      public Pom(String version, String groupId, String artifactId, String packaging, List<String> modules, String sourcePath)
      {
         this.version = version;
         this.groupId = groupId;
         this.artifactId = artifactId;
         this.packaging = packaging;
         this.modules = modules;
         this.sourcePath = sourcePath;
      }

      /**
       * @param version
       * @param groupId
       * @param artifactId
       * @param modules
       */
      public Pom(String version, String groupId, String artifactId, List<String> modules, String sourcePath)
      {
         this.version = version;
         this.groupId = groupId;
         this.artifactId = artifactId;
         this.packaging = "jar";
         this.modules = modules;
         this.sourcePath = sourcePath;
      }

      /**
       * @return the version
       */
      public String getVersion()
      {
         return version;
      }

      /**
       * @return the groupId
       */
      public String getGroupId()
      {
         return groupId;
      }

      /**
       * @return the artifactId
       */
      public String getArtifactId()
      {
         return artifactId;
      }

      /**
       * @return the modules
       */
      public List<String> getModules()
      {
         return modules;
      }

      /**
       * @return the packaging
       */
      public String getPackaging()
      {
         return packaging;
      }

      public String getSourcePath()
      {
         return sourcePath;
      }

      /**
       * Return dependency String ready for inserting to the pom.xml
       * like: <dependency><groupId>com.mycom</groupId><artifactId>tools</artifactId><version>1.0</version></dependency>
       * @return the suggestDependency 
       */
      public String getSuggestDependency()
      {
         StringBuilder builder = new StringBuilder();
         builder.append("<dependency><groupId>").append(groupId).append("</groupId><artifactId>").append(artifactId)
            .append("</artifactId><version>").append(version).append("</version>");
         if (!packaging.equals("jar"))
         {
            builder.append("<type>").append(packaging).append("</type>");
         }
         builder.append("</dependency>");
         return builder.toString();
      }
   }

}
