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
package com.codenvy.ide.jseditor.client.filetype;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;

import java.util.List;

/**
 * {@link FileTypeIdentifier} that chains multiples ways to try to recognize file types.
 * 
 * @author "Mickaël Leduque"
 */
public class MultipleMethodFileIdentifier implements FileTypeIdentifier {
    private ProjectServiceClient projectServiceClient;

    @Inject
    public MultipleMethodFileIdentifier(ProjectServiceClient projectServiceClient) {
        this.projectServiceClient = projectServiceClient;
    }

    private final FileNameFileTypeIdentifier  fileNameFileTypeIdentifier  = new FileNameFileTypeIdentifier();
    private final ExtensionFileTypeIdentifier extensionFileTypeIdentifier = new ExtensionFileTypeIdentifier();
    private final FirstLineFileTypeIdentifier firstLineFileTypeIdentifier = new FirstLineFileTypeIdentifier(projectServiceClient);

    @Override
    public List<String> identifyType(final ItemReference file) {
        Log.debug(MultipleMethodFileIdentifier.class, "Try identification by file name.");
        final List<String> firstTry = this.fileNameFileTypeIdentifier.identifyType(file);
        if (firstTry != null && !firstTry.isEmpty()) {
            return firstTry;
        }
        Log.debug(MultipleMethodFileIdentifier.class, "Try identification by file name suffix.");
        final List<String> secondTry = this.extensionFileTypeIdentifier.identifyType(file);
        if (secondTry != null && !secondTry.isEmpty()) {
            return secondTry;
        }
        // try harder
        Log.debug(MultipleMethodFileIdentifier.class, "Try identification by looking at the content.");
        final List<String> thirdTry = this.firstLineFileTypeIdentifier.identifyType(file);
        if (thirdTry != null && !thirdTry.isEmpty()) {
            return thirdTry;
        }
        // other means may be added later
        Log.debug(MultipleMethodFileIdentifier.class, "No identification method gave an answer.");
        return null;
    }

}
