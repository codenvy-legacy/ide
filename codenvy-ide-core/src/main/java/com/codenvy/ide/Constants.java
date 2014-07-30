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
package com.codenvy.ide;

/** @author Artem Zatsarynnyy */
public interface Constants {
    // project type ids
    final String NAMELESS_ID                  = "nameless";
    final String NAMELESS_CATEGORY            = "nameless";
    final String BLANK_CATEGORY               = "Blank";
    final String BLANK_ID                     = "blank";
    final String CODENVY_PLUGIN_ID            = "codenvy_extension";
    // project type names
    final String NAMELESS_NAME                = "nameless";
    final String BLANK_PROJECT_TYPE           = "Blank Project Type";
    final String CODENVY_PLUGIN_NAME          = "Codenvy Extension";
    final String CODENVY_CATEGORY             = "Codenvy Extension";
    // project attribute names
    final String LANGUAGE                     = "language";
    final String LANGUAGE_VERSION             = "language.version";
    final String FRAMEWORK                    = "framework";
    final String BUILDER_NAME                 = "builder.name";
    final String BUILDER_MAVEN_SOURCE_FOLDERS = "builder.maven.source_folders";
    final String RUNNER_NAME                  = "runner.name";
    final String RUNNER_ENV_ID                = "runner.env_id";
}
