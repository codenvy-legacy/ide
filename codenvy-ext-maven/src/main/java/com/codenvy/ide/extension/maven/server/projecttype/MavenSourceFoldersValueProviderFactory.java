/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
import com.codenvy.ide.maven.tools.MavenUtils;

import org.apache.maven.model.Model;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ValueProviderFactory} implementation for 'builder.maven.source_folders' attribute.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MavenSourceFoldersValueProviderFactory implements ValueProviderFactory {

    @Override
    public String getName() {
        return "builder.maven.source_folders";
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                final List<String> list = new ArrayList<>();
                FileEntry buildDescriptor = (FileEntry)project.getBaseFolder().getChild("pom.xml");
                try {
                    if (buildDescriptor != null) {
                        list.addAll(getSourceFolders(buildDescriptor));
                    }
                    return list;
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public void setValues(List<String> strings) {
                // nothing to do
            }
        };
    }

    private List<String> getSourceFolders(FileEntry pomXml) throws IOException {
        final String defaultSourceDirectoryPath = "src/main/java";
        final String defaultTestSourceDirectoryPath = "src/test/java";

        Model model = MavenUtils.readModel(pomXml.getInputStream());
        List<String> list = MavenUtils.getSourceDirectories(model);
        if (list.isEmpty()) {
            list.add(defaultSourceDirectoryPath);
            list.add(defaultTestSourceDirectoryPath);
        }
        return list;
    }

}
