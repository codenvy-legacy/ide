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

import com.codenvy.ide.api.projecttree.generic.FileNode;

import java.util.Collections;
import java.util.List;

/**
 * {@link FileTypeIdentifier} that tries to recognize special filenames.
 * 
 * @author "Mickaël Leduque"
 */
public class FileNameFileTypeIdentifier implements FileTypeIdentifier {


    @Override
    public List<String> identifyType(final FileNode file) {
        final String filename = file.getName();
        if ("Rakefile".equals(filename) || "Gemfile".equals(filename)) {
            return Collections.singletonList("text/x-ruby");
        }
        if (contains(new String[]{"Makefile", "makefile", "GNUmakefile"}, filename)) {
            return Collections.singletonList("text/x-makefile");
        }
        if (contains(new String[]{"SConstruct", "Sconstruct", "sconstruct"}, filename)) {
            return Collections.singletonList("text/x-python");
        }
        if (filename != null && "vagrantfile".equals(filename.toUpperCase())) {
            return Collections.singletonList("text/x-ruby");
        }

        // not a known file name
        return null;
    }

    private static boolean contains(final String[] reference, final String searched) {
        for (final String value : reference) {
            if (value != null && value.equals(searched)) {
                return true;
            } else if (value == null && searched == null) {
                return true;
            }
        }
        return false;
    }
}
