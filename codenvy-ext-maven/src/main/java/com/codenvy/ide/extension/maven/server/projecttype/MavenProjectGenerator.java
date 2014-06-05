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
package com.codenvy.ide.extension.maven.server.projecttype;

import com.codenvy.api.project.server.FolderEntry;

import java.io.IOException;

/**
 * @author Evgen Vidolob
 */
public class MavenProjectGenerator {



    public static void generateProjectStructure(FolderEntry baseFolder) throws IOException {
        FolderEntry src = baseFolder.createFolder("src");
        FolderEntry main = src.createFolder("main");
        FolderEntry mainJava = main.createFolder("java");
        FolderEntry test = src.createFolder("test");
        FolderEntry testJava = test.createFolder("java");
//        Model model = new Model();
//        model.setArtifactId(options.get("artifactId"));
//        model.setGroupId(options.get("groupId"));
//        model.setVersion(options.get("version"));
//        model.setModelVersion("4.0.0");
//        model.setPackaging("jar");
//        model.setName(baseFolder.getName());
//        File file = ((VirtualFileImpl)baseFolder.getVirtualFile()).getIoFile();
//        File pom = new File(file, "pom.xml");
//        MavenUtils.writeModel(model, pom);
//
//        String aPackage = options.get("package");
//        if(!aPackage.isEmpty()){
//            String name = aPackage.replaceAll("\\.", "/");
//            mainJava.createFolder(name);
//            testJava.createFolder(name);
//        }
//        FolderEntry folder = (FolderEntry)baseFolder.getChild(".codenvy");
//        String properties =  "{\"type\":\"" +Constants.MAVEN_ID +"\",\"properties\":[{\"name\":\"builder.name\",\"value\":[\"maven\"]},{\"name\":\"language\",\"value\":[\"java\"]}]}";
//        AbstractVirtualFileEntry child = folder.getChild("project");
//        ((FileEntry)child).updateContent(properties.getBytes());
    }

}
