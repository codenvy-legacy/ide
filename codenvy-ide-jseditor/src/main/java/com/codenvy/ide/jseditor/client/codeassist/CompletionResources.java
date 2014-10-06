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
package com.codenvy.ide.jseditor.client.codeassist;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * Resources for the completion popup.
 */
public interface CompletionResources extends ClientBundle {

    /** The {@link CssResource} instance. */
    @Source({"AutocompleteComponent.css", "com/codenvy/ide/api/ui/style.css"})
    CompletionCss completionCss();

    /** The {@link CssResource} interfaces. */
    public interface CompletionCss extends CssResource {
        String cappedProposalLabel();

        String proposalIcon();

        String proposalLabel();

        String proposalGroup();

        String infoPopup();

        String container();

        String items();

        String hint();

        int maxHeight();
    }
}
