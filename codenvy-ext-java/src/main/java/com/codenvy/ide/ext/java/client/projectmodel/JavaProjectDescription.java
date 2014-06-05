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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringSet;
import com.codenvy.ide.ext.java.shared.Constants;
import com.codenvy.ide.api.resources.model.ProjectDescription;

/** @author Nikolay Zamosenchuk */
public class JavaProjectDescription extends ProjectDescription {

    /** @param project */
    public JavaProjectDescription(JavaProject project) {
        super(project);
    }

    /** @return set of Project's source folders or empty set */
    public StringSet getSourceFolders() {
        final String builderName = project.getAttributeValue(Constants.BUILDER_NAME);
        if (builderName != null) {
            final String sourceFolders = Constants.BUILDER_SOURCE_FOLDERS.replace("${builder}", builderName);
            return asStringSet(sourceFolders);
        }
        return Collections.createStringSet();
    }
}
