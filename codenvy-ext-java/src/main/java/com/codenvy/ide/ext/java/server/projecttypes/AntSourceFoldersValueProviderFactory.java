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
package com.codenvy.ide.ext.java.server.projecttypes;

import com.codenvy.api.project.server.FileEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ValueProvider;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.codenvy.api.project.server.ValueProviderFactory} implementation for 'builder.ant.source_folders' attribute.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class AntSourceFoldersValueProviderFactory implements ValueProviderFactory {

    @Override
    public String getName() {
        return "builder.ant.source_folders";
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                final List<String> list = new ArrayList<>();
                FileEntry buildDescriptor = (FileEntry)project.getBaseFolder().getChild("build.xml");
                if (buildDescriptor != null) {
                    list.addAll(getAntSourceFolders(buildDescriptor));
                }
                return list;
            }

            @Override
            public void setValues(List<String> strings) {
                // nothing to do
            }
        };
    }

    private List<String> getAntSourceFolders(FileEntry buildXml) {
        final String defaultSourceDirectoryPath = "src";
        List<String> list = new ArrayList<>(1);
        list.add(defaultSourceDirectoryPath);
        return list;
    }

}
