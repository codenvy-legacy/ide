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
package org.exoplatform.ide.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * This is prototype (PoC) utility, that properly registers and Extension in Codenvy Project build.
 * Usecase: A new extension project is created in IDE itself, when it's about to be deployed and started
 * as dedicated Codenvy Web App with extension installed. This tool should patch the unpacked sources of 
 * IDE, copy project folder and set up the project.
 *
 * <ul>
 *   <li>Alter root module pom.xml:<ul>
 *      <li>add dependency description</li>
 *      <li>add module declaration</li>
 *   </ul></li>
 *   <li>Alter client module:<ul>
 *      <li>pom.xml:<ul>
 *         <li>add dependency</li>
 *      </ul></li>
 *      <li>IDE.gwt.xml<ul>
 *         <li>add inherits</li>
 *      </ul></li>
 *      <li>ExtensionManager.java<ul>
 *         <li>add constructor arg</li>
 *      </ul></li></ul></li>
 *</ul>
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class ProjectWithExtensionsInitializer
{

   public static String GEN_START = "<!-- START OF AUTO GENERATED BLOCK -->\n";

   public static String GEN_END = "<!-- END OF AUTO GENERATED BLOCK -->\n";

   // =======================================================================================================

   private final String mavenGroupId;

   private final String mavenArtifactId;

   private final String mavenModuleVersion;

   private final String gwtModuleFQN;

   private final String extensionClassFQN;

   private final File projectRoot;

   // =======================================================================================================

   public static String ROOT_POM = "pom.xml";

   public static String DEP_MANAGE_END_TAG = "</dependencyManagement>";

   public static String MODULE_CLEINT = "<module>exo-ide-client</module>";

   /**
    * Collect extension description and process project settings
    * 
    * @param args
    */
   public static void main(String args[])
   {
      // validate args
      if (args.length != 5)
      {
         System.out
            .println("Ooops, wrong usage. This tool requires 5 arguments to be able to register an extension properly.");
         System.out.println("- Maven Group ID of the extension (i.e. 'org.exoplatform.ide')");
         System.out.println("- Maven Artifact ID of the extension (i.e. 'ide-ext-tasks')");
         System.out.println("- Maven Module Version of the extension (i.e. '3.0')");
         System.out.println("- GWT Module FQN (i.e. 'org.exoplatform.ide.extension.tasks.Tasks')");
         System.out.println("- Extension Class FQN (i.e. 'org.exoplatform.ide.extension.tasks.TasksExtension')");
         return;
      }

      // read input args

      String mavenGroupId = args[0];
      String mavenArtifactId = args[1];
      String mavenModuleVersion = args[2];
      String gwtModuleFQN = args[3];
      String extensionClassFQN = args[4];
      ProjectWithExtensionsInitializer projectWithExtensionsInitializer =
         new ProjectWithExtensionsInitializer(mavenGroupId, mavenArtifactId, mavenModuleVersion, gwtModuleFQN,
            extensionClassFQN, new File("."));

      // try process
      try
      {
         projectWithExtensionsInitializer.setupProject();
      }
      catch (IOException e)
      {
         System.err.println("failed to setup the project.");
         e.printStackTrace(); // display on console
         System.exit(1); // abnormal exit, exception ocurred.
      }
   }

   /**
    * @param mavenGroupId
    * @param mavenArtifactId
    * @param mavenModuleVersion
    * @param gwtModuleFQN
    * @param extensionClassFQN
    * @param projectRoot
    */
   public ProjectWithExtensionsInitializer(String mavenGroupId, String mavenArtifactId, String mavenModuleVersion,
      String gwtModuleFQN, String extensionClassFQN, File projectRoot)
   {
      super();
      this.mavenGroupId = mavenGroupId;
      this.mavenArtifactId = mavenArtifactId;
      this.mavenModuleVersion = mavenModuleVersion;
      this.gwtModuleFQN = gwtModuleFQN;
      this.extensionClassFQN = extensionClassFQN;
      this.projectRoot = projectRoot;
   }

   /**
    * Setup project, to include give Extension into the build.
    * 
    * @throws IOException
    */
   public void setupProject() throws IOException
   {
      try
      {
         processRootPom();
      }
      catch (Exception e)
      {
         throw new IOException("Failed to process Root POM XML", e);
      }
      try
      {
         processClientPom();
      }
      catch (Exception e)
      {
         throw new IOException("Failed to process Client module POM XML", e);
      }
      try
      {
         processClientGwtModuleXML();
      }
      catch (Exception e)
      {
         throw new IOException("Failed to process Client GWT XML", e);
      }
      try
      {
         processExtensionManagerJava();
      }
      catch (Exception e)
      {
         throw new IOException("Failed to process Extension Manager JAVA Class", e);
      }
   }

   /**
    * <li>Alter root module pom.xml:<ul>
    *      <li>add dependency description</li>
    *      <li>add module declaration</li>    * 
    * @throws IOException
    */
   protected void processRootPom() throws IOException
   {
      // add dependency declaration
      // insert as the last one element
      File rootPom = new File(projectRoot, ROOT_POM);
      String rootPomContent = readFileContent(rootPom);
      String depString =
         GEN_START
            + String
               .format(
                  "<dependency>%n<groupId>%s</groupId>%n<artifactId>%s</artifactId>%n<version>%s</version>%n</dependency>%n",
                  mavenGroupId, mavenArtifactId, mavenModuleVersion) + GEN_END;
      rootPomContent = rootPomContent.replace(DEP_MANAGE_END_TAG, depString + DEP_MANAGE_END_TAG);

      // add module, befor client module declaration
      String moduleString = GEN_START + String.format("<module>%s</module>%n", mavenArtifactId) + GEN_END;
      rootPomContent = rootPomContent.replace(MODULE_CLEINT, moduleString + MODULE_CLEINT);
      // write content
      writeFileContent(rootPomContent, rootPom);
   }

   // =======================================================================================================

   public static String DEP_END_TAG = "</dependencies>";

   public static String CLIENT_POM = "exo-ide-client/pom.xml";

   /**
    * Insert dependency at the end of "dependencies" section.
    */
   protected void processClientPom() throws IOException
   {
      File pom = new File(projectRoot, CLIENT_POM);
      String pomContent = readFileContent(pom);

      String depString =
         GEN_START
            + String.format("<dependency>%n<groupId>%s</groupId>%n<artifactId>%s</artifactId>%n</dependency>%n",
               mavenGroupId, mavenArtifactId) + GEN_END;

      // assert
      if (pomContent.indexOf(DEP_END_TAG) < 0)
      {
         throw new IOException(String.format("File '%s' doesn't contain '%s'. Can't process file.", CLIENT_POM,
            DEP_END_TAG));
      }

      pomContent = pomContent.replace(DEP_END_TAG, depString + DEP_END_TAG);
      // write content
      writeFileContent(pomContent, pom);
   }

   // =======================================================================================================

   public static String IDE_GWT_MODULE = "exo-ide-client/src/main/resources/org/exoplatform/ide/IDE.gwt.xml";

   public static String IDE_GWT_ENTRY_TAG = "<entry-point class='org.exoplatform.ide.client.IDE' />";

   /**
    * Insert "inherits" tag before "entry-point".
    */
   protected void processClientGwtModuleXML() throws IOException
   {
      File gwtModuleFile = new File(projectRoot, IDE_GWT_MODULE);
      String gwtModuleContent = readFileContent(gwtModuleFile);

      String inheritsString = GEN_START + String.format("<inherits name='%s' />%n", gwtModuleFQN) + GEN_END;

      // assert
      if (gwtModuleContent.indexOf(IDE_GWT_ENTRY_TAG) < 0)
      {
         throw new IOException(String.format("File '%s' doesn't contain '%s'. Can't process file.", IDE_GWT_MODULE,
            IDE_GWT_ENTRY_TAG));
      }

      gwtModuleContent = gwtModuleContent.replace(IDE_GWT_ENTRY_TAG, inheritsString + IDE_GWT_ENTRY_TAG);
      // write content
      writeFileContent(gwtModuleContent, gwtModuleFile);
   }

   // =======================================================================================================

   public static String EXTENSION_MANAGER =
      "exo-ide-client/src/main/java/org/exoplatform/ide/client/ExtensionManager.java";

   public static String EXTENSION_MANAGER_CONSTRUCTOR_START = "public ExtensionManager(";

   public static String CONSTRUCTOR_INSERT_CODE = "// INSERT CODE HERE";

   private static final String IMPORT = "import";

   /**
    * Add import, constructor argument and add one more line of code to constructor.
    * 
    * @throws IOException
    */
   protected void processExtensionManagerJava() throws IOException
   {
      File extManagerJava = new File(projectRoot, EXTENSION_MANAGER);
      String extManagerContent = readFileContent(extManagerJava);

      // assert
      if (extManagerContent.indexOf(IMPORT) < 0)
      {
         throw new IOException(String.format("File '%s' doesn't contain '%s'. Can't process file.", EXTENSION_MANAGER,
            IMPORT));
      }
      if (extManagerContent.indexOf(EXTENSION_MANAGER_CONSTRUCTOR_START) < 0)
      {
         throw new IOException(String.format("File '%s' doesn't contain '%s'. Can't process file.", EXTENSION_MANAGER,
            EXTENSION_MANAGER_CONSTRUCTOR_START));
      }
      if (extManagerContent.indexOf(CONSTRUCTOR_INSERT_CODE) < 0)
      {
         throw new IOException(String.format("File '%s' doesn't contain '%s'. Can't process file.", EXTENSION_MANAGER,
            CONSTRUCTOR_INSERT_CODE));
      }

      StringBuilder extManagerBuilder = new StringBuilder(extManagerContent);

      // Add import : import org....ExtensionClass
      int importStartOffset = extManagerBuilder.indexOf(IMPORT);
      String importString = String.format("import %s; %n", extensionClassFQN);
      extManagerBuilder.insert(importStartOffset, importString);

      // Add constructor argument : ExtensionClass argName
      String[] fqnEntries = extensionClassFQN.split(Pattern.quote("."));
      String constructorArgName = fqnEntries[fqnEntries.length - 1].toLowerCase();
      String constructorArg = extensionClassFQN + " " + constructorArgName;
      int constructorArgOffset =
         extManagerBuilder.indexOf(EXTENSION_MANAGER_CONSTRUCTOR_START) + EXTENSION_MANAGER_CONSTRUCTOR_START.length();
      extManagerBuilder.insert(constructorArgOffset, constructorArg);

      // Add add arg to the list of extensions : this.extensions.add(argName);
      String addExtensionToListLine = String.format("%nthis.extensions.add(%s);%n", constructorArgName);
      int codePartOffset = extManagerBuilder.indexOf(CONSTRUCTOR_INSERT_CODE) + CONSTRUCTOR_INSERT_CODE.length();
      extManagerBuilder.insert(codePartOffset, addExtensionToListLine);

      // write content
      writeFileContent(extManagerBuilder.toString(), extManagerJava);

   }

   // =======================================================================================================

   /**
    * Read file 
    * 
    * @param file
    * @return
    * @throws IOException
    */
   public static String readFileContent(File file) throws IOException
   {
      InputStream in = new FileInputStream(file);
      byte[] b = new byte[(int)file.length()];
      int len = b.length;
      int total = 0;

      while (total < len)
      {
         int result = in.read(b, total, len - total);
         if (result == -1)
         {
            break;
         }
         total += result;
      }

      return new String(b, "UTF-8");
   }

   /**
    * Update file content
    * 
    * @param text
    * @param file
    * @throws IOException
    */
   public static void writeFileContent(String text, File file) throws IOException
   {
      BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
      out.write(text);
      out.close();
      return;
   }

}