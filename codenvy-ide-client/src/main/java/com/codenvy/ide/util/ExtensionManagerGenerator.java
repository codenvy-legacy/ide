/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class ExtensionManagerGenerator
{

   /**
    * CLI Argument
    */
   private static final String ROOT_DIR_PARAMETER = "--rootDir=";

   /**
    * Current Package name, used to avoid miss-hits of Extension's lookup
    */
   private static final String COM_CODENVY_IDE_UTIL = "com.codenvy.ide.util";

   public static final String EXT_ANNOTATION = "@Extension";

   /**
    * Reg Exp that matches the "@Extension  ( ... )"
    */
   public static final Pattern EXT_PATTERN = Pattern.compile(".*@Extension\\s*\\(.*\\).*", Pattern.DOTALL);

   /**
    * Reg Exp that matches the package declaration
    */
   public static final Pattern PACKAGE_PATTERN = Pattern
      .compile(".*package\\s+([a-zA_Z_][\\.\\w]*);.*", Pattern.DOTALL);

   public static final String EXT_MANAGER_PATH =
      "codenvy-ide-client/src/main/java/com/codenvy/ide/client/ExtensionManager.java";

   public static final String TAB = "   ";

   public static final String TAB2 = TAB + TAB;

   /**
    * Map containing <FullFQN, ClassName>
    */
   public static final Map<String, String> extensionsFqn = new HashMap<String, String>();

   /**
    * Utility entry point
    * 
    * @param args
    */
   public static void main(String[] args)
   {
      try
      {
         String rootDirPath = ".";
         // try to read argument
         if (args.length == 1)
         {
            if (args[0].startsWith(ROOT_DIR_PARAMETER))
            {
               rootDirPath = args[0].substring(ROOT_DIR_PARAMETER.length());
            }
            else
            {
               System.err.print("Wrong usage. There is only one allowed argument : " + ROOT_DIR_PARAMETER);
               System.exit(1);
            }
         }
         System.out.println("Starting extension lookup from directory : '" + rootDirPath + "'");
         File rootFolder = new File(rootDirPath);
         // find all Extension FQNs
         findExtensions(rootFolder);
         generateExtensionManager(rootFolder);
      }
      catch (IOException e)
      {
         System.err.println(e.getMessage());
         // error
         System.exit(1);
      }
   }

   /**
    * @param rootFolder
    */
   public static void generateExtensionManager(File rootFolder) throws IOException
   {
      File extManager = new File(rootFolder, EXT_MANAGER_PATH);
      if (!extManager.exists())
      {
         throw new IOException(String.format("File \"%s\" not found. Utility seems to be started in wrong folder",
            EXT_MANAGER_PATH));
      }

      StringBuilder builder = new StringBuilder();
      builder.append("package " + "com.codenvy.ide.client;\n\n");
      generateImports(builder);
      generateClass(builder);
      // flush content
      FileUtils.writeStringToFile(extManager, builder.toString());
   }

   /**
    * @param builder
    */
   public static void generateClass(StringBuilder builder)
   {
      // generate class header
      builder.append("/**\n");
      builder.append(" * THIS CLASS WILL BE OVERRIDEN BY MAVEN BUILD. DON'T EDIT CLASS, IT WILL HAVE NO EFFECT.\n");
      builder.append(" */\n");
      builder.append("public class ExtensionManager extends AbstractExtensionManager\n");
      builder.append("{\n");
      // generate constructor

      builder.append(TAB + "@Inject\n");
      String firstComma = extensionsFqn.isEmpty() ? "" : ",";
      builder.append(TAB + "public ExtensionManager(ExtensionRegistry extensionRegistry" + firstComma + "\n");

      // paste args here
      Iterator<Entry<String, String>> entryIterator = extensionsFqn.entrySet().iterator();
      while (entryIterator.hasNext())
      {
         // <FullFQN, ClassName>
         Entry<String, String> extensionEntry = entryIterator.next();
         String hasComma = entryIterator.hasNext() ? "," : "";
         // add constructor argument like:
         // fullFQN classNameToLowerCase,
         String classFQN = String.format("Provider<%s>", extensionEntry.getKey());
         String variableName = extensionEntry.getValue().toLowerCase();
         builder.append(TAB2 + classFQN + " " + variableName + hasComma + "\n");
      }

      builder.append(TAB + ")\n");
      builder.append(TAB + "{\n");
      builder.append(TAB2 + "super(extensionRegistry);\n");

      // paste add here
      for (String extension : extensionsFqn.values())
      {
         String variableName = extension.toLowerCase();
         builder.append(TAB2 + "this.extensions.add(" + variableName + ");\n");
      }

      // close constructor
      builder.append(TAB + "}\n");
      // close class
      builder.append("}\n");

   }

   /**
    * Generate imports
    * 
    * @param builder
    */
   public static void generateImports(StringBuilder builder)
   {

      builder.append("import com.google.inject.Inject;\n");
      builder.append("import com.google.inject.Provider;\n");

      builder.append("import com.codenvy.ide.extension.ExtensionRegistry;\n");

      // add all Extenions into the import
      for (String fqn : extensionsFqn.keySet())
      {
         builder.append("import " + fqn + ";\n");
      }
   }

   /**
    * @throws IOException
    */
   @SuppressWarnings("unchecked")
   public static void findExtensions(File rootFolder) throws IOException
   {

      System.out.println(String.format("Searching for files in %s", rootFolder.getAbsolutePath()));
      // list all Java Files
      String[] extensions = {"java"};
      Collection<File> listFiles = FileUtils.listFiles(rootFolder, extensions, true);
      for (File file : listFiles)
      {
         // check file has annotation @Extension
         String fileContent = FileUtils.readFileToString(file);

         // quick filter is "@Extension" text exists, later, need to check with regexp
         if (fileContent.contains(EXT_ANNOTATION))
         {
            // for sure file contains Extension annotation, not just "@Extension" in the javadocs or whatever 
            if (EXT_PATTERN.matcher(fileContent).matches())
            {
               // read package name and class name
               String className = file.getName().split("\\.")[0];
               String packageName = getClassFQN(file.getAbsolutePath(), fileContent);
               if (!packageName.startsWith(COM_CODENVY_IDE_UTIL))
               {
                  extensionsFqn.put(packageName + "." + className, className);
                  System.out.println(String.format("New Extension Found: %s.%s", packageName, className));
               }
               else
               {
                  // skip this class, cause it is an utility, not the actual extension.
                  //                  System.out.println(String.format("Skipping class %s.%s as it is utility, not the extension",
                  //                     packageName, className));
               }
            }
         }
      }
      System.out.println(String.format("Found: %d extensions", extensionsFqn.size()));
   }

   /**
    * Extracts Package declaration from file
    * 
    * @param fileName
    * @param content
    * @return
    * @throws IOException
    */
   public static String getClassFQN(String fileName, String content) throws IOException
   {
      Matcher matcher = ExtensionManagerGenerator.PACKAGE_PATTERN.matcher(content);
      if (!matcher.matches())
      {
         throw new IOException(String.format("Class %s doesn't seem to be valid. Package declaration is missing.",
            fileName));
      }
      if (matcher.groupCount() != 1)
      {
         throw new IOException(String.format("Class %s doesn't seem to be valid. Package declaration is missing.",
            fileName));
      }
      return matcher.group(1);
   }
}
