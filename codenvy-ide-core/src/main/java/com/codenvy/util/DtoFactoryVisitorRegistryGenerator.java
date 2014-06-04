/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.util;

import com.codenvy.ide.dto.ClientDtoFactoryVisitor;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Generates {DtoFactoryVisitorRegistry} class source.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class DtoFactoryVisitorRegistryGenerator {

    /**
     * Path of the output class, it definitely should already exits. To ensure proper config.
     * File content will be overridden.
     */
    protected static final String              REGISTRY_PATH      =
            "com/codenvy/ide/client/DtoFactoryVisitorRegistry.java";
    /** Map containing <FullFQN, ClassName> */
    protected static final Map<String, String> dtoFactoryVisitors = new HashMap<String, String>();

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
            System.out.println(String.format("Searching for DTO"));
            System.out.println(" ------------------------------------------------------------------------ ");

            // find all DtoFactoryVisitors
            findDtoFactoryVisitors();
            generateExtensionManager(rootFolder);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            // error
            System.exit(1);
        }
    }

    /**
     * Find all the Java classes that have proper @ClientDtoFactoryVisitor annotation.
     *
     * @throws java.io.IOException
     */
    @SuppressWarnings("unchecked")
    private static void findDtoFactoryVisitors() throws IOException {
        Reflections reflection = new Reflections(new TypeAnnotationsScanner());
        Set<Class<?>> classes = reflection.getTypesAnnotatedWith(ClientDtoFactoryVisitor.class);
        int i = 0;
        for (Class clazz : classes) {
            dtoFactoryVisitors.put(clazz.getCanonicalName(), "provider_" + i++);
            System.out.println(String.format("New DtoFactoryVisitor found: %s", clazz.getCanonicalName()));
        }
        System.out.println(String.format("Found: %d DtoFactoryVisitor(s)", dtoFactoryVisitors.size()));
    }

    /**
     * Generate to source of the class.
     *
     * @param rootFolder
     */
    public static void generateExtensionManager(File rootFolder) throws IOException {
        File outFile = new File(rootFolder, REGISTRY_PATH);

        StringBuilder builder = new StringBuilder();
        builder.append("package " + "com.codenvy.ide.client;\n\n");
        generateImports(builder);
        generateClass(builder);

        // flush content
        FileUtils.writeStringToFile(outFile, builder.toString());
    }

    /**
     * Generate imports.
     *
     * @param builder
     */
    public static void generateImports(StringBuilder builder) {
        builder.append("import com.google.inject.Inject;\n");
        builder.append("import com.google.inject.Provider;\n");
        builder.append("import com.google.inject.Singleton;\n");

        builder.append("import com.codenvy.ide.collections.StringMap;\n");
        builder.append("import com.codenvy.ide.collections.Collections;\n");
    }

    /**
     * Generate class declarations.
     *
     * @param builder
     */
    public static void generateClass(StringBuilder builder) {
        // generate class header
        builder.append("/**\n");
        builder.append(" * THIS CLASS WILL BE OVERRIDDEN BY MAVEN BUILD. DON'T EDIT CLASS, IT WILL HAVE NO EFFECT.\n");
        builder.append(" */\n");
        builder.append("@Singleton\n");
        builder.append("@SuppressWarnings(\"rawtypes\")\n");
        builder.append("public class DtoFactoryVisitorRegistry\n");
        builder.append("{\n");
        builder.append("\n");

        // field
        builder.append(GeneratorUtils.TAB
                       +
                       "/** Contains the map will all the DtoFactoryVisitor Providers <FullClassFQN, Provider>. */\n");
        builder.append(GeneratorUtils.TAB
                       + "protected final StringMap<Provider> providers = Collections.createStringMap();\n\n");

        // generate constructor

        builder.append(GeneratorUtils.TAB + "/** Constructor that accepts all found DtoFactoryVisitor Providers. */\n");
        builder.append(GeneratorUtils.TAB + "@Inject\n");
        builder.append(GeneratorUtils.TAB + "public DtoFactoryVisitorRegistry(\n");

        // paste args here
        Iterator<Entry<String, String>> entryIterator = dtoFactoryVisitors.entrySet().iterator();
        while (entryIterator.hasNext()) {
            // <FullFQN, ClassName>
            Entry<String, String> entry = entryIterator.next();
            String hasComma = entryIterator.hasNext() ? "," : "";
            // add constructor argument like:
            // fullFQN classNameToLowerCase,
            String classFQN = String.format("Provider<%s>", entry.getKey());
            String variableName = entry.getValue().toLowerCase();
            builder.append(GeneratorUtils.TAB2 + classFQN + " " + variableName + hasComma + "\n");
        }

        builder.append(GeneratorUtils.TAB + ")\n");
        builder.append(GeneratorUtils.TAB + "{\n");

        // paste add here
        for (Entry<String, String> entries : dtoFactoryVisitors.entrySet()) {
            String fullFqn = entries.getKey();
            String variableName = entries.getValue().toLowerCase();

            String putStatement = String.format("this.providers.put(\"%s\", %s);%n", fullFqn, variableName);
            builder.append(GeneratorUtils.TAB2 + putStatement);
        }

        // close constructor
        builder.append(GeneratorUtils.TAB + "}\n\n");

        // generate getter
        builder.append(GeneratorUtils.TAB
                       +
                       "/** Returns  the map will all the DtoFactoryVisitor Providers <FullClassFQN, Provider>. */\n");
        builder.append(GeneratorUtils.TAB + "public StringMap<Provider> getDtoFactoryVisitors()\n");
        builder.append(GeneratorUtils.TAB + "{\n");
        builder.append(GeneratorUtils.TAB2 + "return providers;\n");
        builder.append(GeneratorUtils.TAB + "}\n");

        // close class
        builder.append("}\n");
    }
}
