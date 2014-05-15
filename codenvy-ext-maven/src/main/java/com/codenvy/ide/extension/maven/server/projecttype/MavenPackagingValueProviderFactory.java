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

import com.codenvy.api.project.server.FileEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ValueProvider;
import com.codenvy.ide.extension.maven.shared.MavenAttributes;
import com.codenvy.ide.maven.tools.MavenUtils;
import com.codenvy.vfs.impl.fs.VirtualFileImpl;

import org.apache.maven.model.Model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgen Vidolob
 */
public class MavenPackagingValueProviderFactory implements ValueProviderFactory {
    @Override
    public String getName() {
        return MavenAttributes.MAVEN_PACKAGING;
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                List<String> list = new ArrayList<>();
                FileEntry pomFile = (FileEntry)project.getBaseFolder().getChild("pom.xml");
                if (pomFile != null) {
                    try {
                        Model model = MavenUtils.readModel(pomFile.getInputStream());
                        list.add(model.getPackaging());
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
                return list;
            }

            @Override
            public void setValues(List<String> value) {
                if(value.isEmpty()){
                    throw new IllegalStateException("Maven Packaging can't be empty.");
                }
                if(value.size() > 1){
                    throw new IllegalStateException("Maven Packaging must be only one value.");
                }
                FileEntry pomFile = (FileEntry)project.getBaseFolder().getChild("pom.xml");
                try {
                    Model model;
                    if (pomFile != null) {
                        model = MavenUtils.readModel(pomFile.getInputStream());
                    } else{
                        model= new Model();
                        model.setModelVersion("4.0.0");
                        MavenProjectGenerator.generateProjectStructure(project.getBaseFolder());
                    }
                    model.setPackaging(value.get(0));
                    File file = ((VirtualFileImpl)project.getBaseFolder().getVirtualFile()).getIoFile();
                    MavenUtils.writeModel(model, new File(file, "pom.xml"));
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}
