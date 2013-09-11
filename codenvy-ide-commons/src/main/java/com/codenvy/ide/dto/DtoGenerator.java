// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.dto;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

/**
 * Simple source generator that takes in a jar of interface definitions and
 * generates client and server DTO impls.
 */
public class DtoGenerator {

    private static final String INVALID_PATH = "invalid path";

    private static final String SERVER = "server";

    private static final String CLIENT = "client";

    /** Flag: location of the packages that contains dto interfaces. */
    static String dto_packages = null;

    /** Flag: Name of the generated java class file that contains the DTOs. */
    static String gen_file_name = "DataObjects.java";

    /** Flag: The type of impls to be generated, either CLIENT or SERVER. */
    static String impl = CLIENT;

    /**
     * Flag: A pattern we can use to search an absolute path and find the start
     * of the package definition.")
     */
    static String package_base = "java.";

    /** @param args */
    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--dto_packages=")) {
                dto_packages = arg.substring("--dto_packages=".length());
            } else if (arg.startsWith("--gen_file_name=")) {
                gen_file_name = arg.substring("--gen_file_name=".length());
            } else if (arg.startsWith("--impl=")) {
                impl = arg.substring("--impl=".length());
            } else if (arg.startsWith("--package_base=")) {
                package_base = arg.substring("--package_base=".length());
            } else {
                System.err.println("Unknown flag: " + arg);
                System.exit(1);
            }
        }

        Set<URL> urls = parsePackagesParam(dto_packages);
        String outputFilePath = gen_file_name;

        // Extract the name of the output file that will contain all the DTOs and its package.
        int packageStart = outputFilePath.lastIndexOf(package_base) + package_base.length();
        int packageEnd = outputFilePath.lastIndexOf('/');
        String fileName = outputFilePath.substring(packageEnd + 1);
        String className = fileName.substring(0, fileName.indexOf(".java"));
        String packageName = outputFilePath.substring(packageStart, packageEnd).replace('/', '.');

        File outFile = new File(outputFilePath);

        try {
            DtoTemplate dtoTemplate = new DtoTemplate(packageName, className, getApiHash(dto_packages), impl.equals(SERVER));
            Reflections reflection =
                    new Reflections(new ConfigurationBuilder().setUrls(urls).setScanners(new TypeAnnotationsScanner()));
            List<Class<?>> classes = new ArrayList<>(reflection.getTypesAnnotatedWith(DTO.class));

            // We sort alphabetically to ensure deterministic order of routing types.
            Collections.sort(classes, new ClassesComparator());

            for (Class clazz : classes) {
                dtoTemplate.addInterface(clazz);
            }

            // Emit the generated file.
            Files.createDirectories(outFile.toPath().getParent());
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            writer.write(dtoTemplate.toString());
            writer.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getApiHash(String packageName) throws IOException {
        byte[] fileBytes = packageName.getBytes();
        HashCode hashCode = Hashing.sha1().hashBytes(fileBytes);
        return hashCode.toString();
    }

    private static Set<URL> parsePackagesParam(String packagesParam) {
        Set<URL> urls = new HashSet<>();
        String[] packages = packagesParam.split(",");
        for (String pack : packages) {
            urls.addAll(ClasspathHelper.forPackage(pack));
        }
        return urls;
    }

    private static class ClassesComparator implements Comparator<Class> {
        @Override
        public int compare(Class o1, Class o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}