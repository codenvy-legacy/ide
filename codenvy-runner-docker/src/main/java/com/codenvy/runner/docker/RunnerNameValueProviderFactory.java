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

import com.codenvy.api.project.server.FolderEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ValueProvider;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author andrew00x
 */
@Singleton
public class RunnerNameValueProviderFactory implements ValueProviderFactory {
    @Override
    public String getName() {
        return com.codenvy.api.runner.internal.Constants.RUNNER_CUSTOM_LAUNCHER;
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                final FolderEntry projectFolder = project.getBaseFolder();
                for (String fName : RunnerScriptValueProviderFactory.DOCKER_FILES) {
                    if (projectFolder.getChild(fName) != null) {
                        return Arrays.asList("docker");
                    }
                }
                return Collections.emptyList();
            }

            @Override
            public void setValues(List<String> value) {
                // noop
            }
        };
    }
}
