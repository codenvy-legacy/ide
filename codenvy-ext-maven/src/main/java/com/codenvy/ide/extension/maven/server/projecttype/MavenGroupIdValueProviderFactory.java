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

import com.codenvy.api.project.server.FileEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ValueProvider;
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
public class MavenGroupIdValueProviderFactory implements ValueProviderFactory {
    @Override
    public String getName() {
        return "maven.groupId";
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
                        list.add(model.getGroupId());
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
                return list;
            }

            @Override
            public void setValues(List<String> value) {
                if(value.isEmpty()){
                    throw new IllegalStateException("Maven GroupId can't be empty.");
                }
                if(value.size() > 1){
                    throw new IllegalStateException("Maven GroupId must be only one value.");
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
                    model.setGroupId(value.get(0));
                    File file = ((VirtualFileImpl)project.getBaseFolder().getVirtualFile()).getIoFile();
                    MavenUtils.writeModel(model, new File(file, "pom.xml"));
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}
