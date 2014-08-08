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
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.Collections;
import java.util.List;

/**
 * {@link FileTypeIdentifier} that tries to recognize file by looking at the first line content.
 * 
 * @author "Mickaël Leduque"
 */
public class FirstLineFileTypeIdentifier implements FileTypeIdentifier {

    // format: <?xml
    /** Pattern to recognize xml that has an xml declaration. */
    private static final RegExp XML_PATTERN = RegExp.compile("^<\\?xml");

    // format: #!<loader> [options]\n
    /** Pattern to try to recognize scripts with a shebang. */
    private static final RegExp SHEBANG_PATTERN = RegExp.compile("^#!([^\\n\\s]+)\\s*([^\\n\\s]+)?.*\\n");
    private final ProjectServiceClient projectServiceClient;

    public FirstLineFileTypeIdentifier(ProjectServiceClient projectServiceClient) {
        this.projectServiceClient = projectServiceClient;
    }

    @Override
    public List<String> identifyType(final ItemReference file) {
//        projectServiceClient.getFileContent(file.getPath(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
//            @Override
//            protected void onSuccess(String content) {
//
//            }
//
//            @Override
//            protected void onFailure(Throwable throwable) {
//
//            }
//        });
        final String content = ""/*file.getContent()*/;
        if (isXml(content)) {
            Log.debug(FirstLineFileTypeIdentifier.class, "Identified file as XML.");
            return Collections.singletonList("application/xml");
        }
        final String shebangLoader = getShebang(content);
        if (shebangLoader != null) {
            final int lastSlash = shebangLoader.lastIndexOf('/');
            // need to consider \ as path separator for a feature that exists mostly on unixes ?
            final String basename = shebangLoader.substring(lastSlash + 1);
            final String shebangResult = matchShebang(basename);
            if (shebangResult != null) {
                return Collections.singletonList(shebangResult);
            }
        }

        return null;
    }

    private boolean isXml(final String content) {
        return XML_PATTERN.test(content);
    }

    private String getShebang(final String content) {
        final MatchResult matchResult = SHEBANG_PATTERN.exec(content);

        if (matchResult == null || matchResult.getGroup(1) == null || matchResult.getGroup(1).isEmpty()) {
            return null;
        }
        Log.debug(FirstLineFileTypeIdentifier.class, "File may be a script with a shebang.");
        String loader = matchResult.getGroup(1);

        // special case for /usr/bin/env
        if ("/usr/bin/env".equals(loader)) {
            Log.debug(FirstLineFileTypeIdentifier.class, "Shebang points to /usr/bin/env. Looking at the parameter.");
            // we must use the first option as hint
            if (matchResult.getGroup(2) == null || matchResult.getGroup(2).isEmpty()) {
                return null;
            }
            loader = matchResult.getGroup(2);
            Log.debug(FirstLineFileTypeIdentifier.class, "Shebang parameter kept: " + loader);
        } else {
            Log.debug(FirstLineFileTypeIdentifier.class, "Shebang loader kept: " + loader);
        }
        return loader;
    }

    private String matchShebang(final String shebangLoader) {
        // the shells that are related to sh - not csh !
        if ("sh".equals(shebangLoader)
            || "bash".equals(shebangLoader)
            || "dash".equals(shebangLoader)
            || "ksh".equals(shebangLoader)
            || "zsh".equals(shebangLoader)) {
            Log.debug(FirstLineFileTypeIdentifier.class, "File may be a bourne shell script or similar.");
            return "text/x-sh";
        }
        // python
        if (shebangLoader.startsWith("python")) {
            Log.debug(FirstLineFileTypeIdentifier.class, "File may be a python script.");
            return "text/x-python";
        }
        // perl
        if (shebangLoader.startsWith("perl")) {
            Log.debug(FirstLineFileTypeIdentifier.class, "File may be a perl script.");
            return "text/x-perl";
        }
        // ruby
        if (shebangLoader.startsWith("ruby")) {
            Log.debug(FirstLineFileTypeIdentifier.class, "File may be a ruby script.");
            return "text/x-ruby";
        }
        // are there any other script interpreters commonly used as shebang ?
        return null;
    }
}
