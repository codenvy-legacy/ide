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
package com.codenvy.runner.docker;

import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ValueProvider;
import com.codenvy.api.vfs.shared.dto.Project;
import com.codenvy.api.vfs.shared.dto.Property;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author andrew00x
 */
@Singleton
public class DockerfileUrlValueProviderFactory implements ValueProviderFactory {
    @Override
    public String getName() {
        return "dockerfile_url";
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                String dockerFileName = null;
                for (Property property : project.getProperties()) {
                    if ("docker_file".equals(property.getName()) && !property.getValue().isEmpty()) {
                        dockerFileName = property.getValue().get(0);
                        break;
                    }
                }
                if (dockerFileName == null) {
                    return Collections.emptyList();
                }
                final List<String> list = new ArrayList<>(1);
                final String projectUrl = project.getLinks().get(com.codenvy.api.vfs.shared.dto.Link.REL_SELF).getHref();
                list.add(projectUrl.replace("item/" + project.getId(), "contentbypath" + project.getPath() + '/' + dockerFileName));
                return list;
            }

            @Override
            public void setValues(List<String> value) {
                // Nothing to do.
            }
        };
    }
}
