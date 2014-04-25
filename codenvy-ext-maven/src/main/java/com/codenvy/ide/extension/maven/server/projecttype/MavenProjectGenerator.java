/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.extension.maven.server.projecttype;

import com.codenvy.api.project.server.AbstractVirtualFileEntry;
import com.codenvy.api.project.server.FileEntry;
import com.codenvy.api.project.server.FolderEntry;
import com.codenvy.api.project.server.ProjectGenerator;
import com.codenvy.ide.ext.java.shared.Constants;
import com.codenvy.ide.maven.tools.MavenUtils;
import com.codenvy.vfs.impl.fs.VirtualFileImpl;

import org.apache.maven.model.Model;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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
//        String properties =  "{\"type\":\"" +Constants.MAVEN_JAR_ID +"\",\"properties\":[{\"name\":\"builder.name\",\"value\":[\"maven\"]},{\"name\":\"language\",\"value\":[\"java\"]}]}";
//        AbstractVirtualFileEntry child = folder.getChild("project");
//        ((FileEntry)child).updateContent(properties.getBytes());
    }

}
