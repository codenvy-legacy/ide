/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.util;

import com.codenvy.ide.api.extension.ExtensionGinModule;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class looks for all the Gin Modules annotated with ExtensionGinModule annotation
 * and adds all them to IDEInjector class.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class IDEInjectorGenerator {

    /** Annotation to look for */
    protected static final String GIN_MODULE_ANNOTATION = "@ExtensionGinModule";

    /**
     * Path of the ouput class, it definitely should already exits. To ensure proper config.
     * File content will be overriden.
     */
    protected static final String IDE_INJECTOR_PATH =
            "com/codenvy/ide/client/inject/IDEInjector.java";

    /** Set containing all the FQNs of GinModules */
    public static final Set<String> extensionsFqn = new HashSet<String>();
    public static final String      CLIENT_MODULE = "com.codenvy.ide.client.inject.IDEClientModule";

    /**
     * Entry point. --rootDir is the optional parameter.
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            String rootDirPath = ".";
            // try to read argument
            if (args.length == 1) {
                if (args[0].startsWith(GeneratorUtils.ROOT_DIR_PARAMETER)) {
                    rootDirPath = args[0].substring(GeneratorUtils.ROOT_DIR_PARAMETER.length());
                } else {
                    System.err.print("Wrong usage. There is only one allowed argument : "
                                     + GeneratorUtils.ROOT_DIR_PARAMETER);
                    System.exit(1);
                }
            }
            File rootFolder = new File(rootDirPath);
            System.out.println(" ------------------------------------------------------------------------ ");
            System.out.println(String.format("Searching for GinModules in %s", rootFolder.getAbsolutePath()));
            System.out.println(" ------------------------------------------------------------------------ ");
            // find all Extension FQNs
            findGinModules(rootFolder);
            generateExtensionManager(rootFolder);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            // error
            System.exit(1);
        }

    }

    /**
     * Generate to source of the Class
     *
     * @param rootFolder
     */
    public static void generateExtensionManager(File rootFolder) throws IOException {
        File extManager = new File(rootFolder, IDE_INJECTOR_PATH);
//        if (!extManager.exists()) {
//            throw new IOException(String.format("File \"%s\" not found. Utility seems to be started in wrong folder",
//                                                IDE_INJECTOR_PATH));
//        }

        StringBuilder builder = new StringBuilder();
        // declare packagename
        builder.append("package " + "com.codenvy.ide.client.inject;\n\n");

        // declare imports
        builder.append("import com.codenvy.ide.client.BootstrapController;\n");
        builder.append("import com.google.gwt.inject.client.GinModules;\n");
        builder.append("import com.google.gwt.inject.client.Ginjector;\n");
        builder.append("\n");

        // declare class Javadoc
        builder.append("/**\n");
        builder.append(" * THIS CLASS WILL BE OVERRIDEN BY MAVEN BUILD. DON'T EDIT CLASS, IT WILL HAVE NO EFFECT.\n");
        builder.append(" * \n");
        builder.append(" * Interface for GIN Injector, that provides access to the top level\n");
        builder.append(" * application components. Implementation of Injector is generated\n");
        builder.append(" * on compile time.\n");
        builder.append(" */\n");
        builder.append("@GinModules({\n");
        // generate the list of modules includes
        generateListOfModules(builder);
        // close GinModules declaration
        builder.append("})\n");

        // declare class definition
        builder.append("public interface IDEInjector extends Ginjector\n");
        builder.append("{\n");
        builder.append("\n");
        // define method
        builder.append(GeneratorUtils.TAB + "/**\n");
        builder.append(GeneratorUtils.TAB + " * @return the instance of BootstrapController\n");
        builder.append(GeneratorUtils.TAB + " */\n");
        builder.append(GeneratorUtils.TAB + "BootstrapController getBootstrapController();\n");
        // close class definition
        builder.append("\n");
        builder.append("}\n");

        // flush content
        FileUtils.writeStringToFile(extManager, builder.toString());
    }

    /**
     * Generate codeblock with all the GinModules
     *
     * @param builder
     */
    public static void generateListOfModules(StringBuilder builder) {
        // Generate the list of GinModules declarations
        Iterator<String> entryIterator = extensionsFqn.iterator();
        while (entryIterator.hasNext()) {
            // <FullFQN, ClassName>
            String ginModuleFQN = entryIterator.next();
            String hasComma = entryIterator.hasNext() ? "," : "";
            // add ModuleDeclaration
            builder.append(GeneratorUtils.TAB + ginModuleFQN + ".class " + hasComma + "\n");
        }
    }

    /**
     * Find all the Java files that have ExtensionGinModule annotation
     *
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static void findGinModules(File rootFolder) throws IOException {
//        // list all Java Files
//        String[] extensions = {"java"};
//        Collection<File> listFiles = FileUtils.listFiles(rootFolder, extensions, true);
//        for (File file : listFiles) {
//            String fileContent = FileUtils.readFileToString(file);
//            // check file has annotation ExtensionGinModule
//            if (fileContent.contains(GIN_MODULE_ANNOTATION)) {
//                // read package name and class name
//                String className = file.getName().split("\\.")[0];
//                String packageName = GeneratorUtils.getClassFQN(file.getAbsolutePath(), fileContent);
//                // exclude "com.codenvy.ide.util"
//                if (!packageName.startsWith(GeneratorUtils.COM_CODENVY_IDE_UTIL)) {
//                    String fullFqn = packageName + "." + className;
//                    if (!extensionsFqn.contains(fullFqn)) {
//                        extensionsFqn.add(fullFqn);
//                        System.out.println(String.format("New Gin Module Found: %s.%s", packageName, className));
//                    }
//                } else {
//                    // skip this class, cause it is an utility, not the actual extension.
//                    //                  System.out.println(String.format("Skipping class %s.%s as it is utility, not the extension",
//                    //                     packageName, className));
//                }
//            }
//        }
//        System.out.println(String.format("Found: %d Gin Modules", extensionsFqn.size()));
        Reflections reflection = new Reflections(new TypeAnnotationsScanner());
        Set<Class<?>> classes = reflection.getTypesAnnotatedWith(ExtensionGinModule.class);
        for (Class clazz : classes) {
            extensionsFqn.add(clazz.getCanonicalName());
            System.out.println(String.format("New Gin Module Found: %s", clazz.getCanonicalName()));
        }
        extensionsFqn.add(CLIENT_MODULE);
        System.out.println(String.format("New Gin Module Found: %s", CLIENT_MODULE));
        System.out.println(String.format("Found: %d Gin Modules", extensionsFqn.size()));
    }

}
