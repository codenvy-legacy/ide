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
package com.codenvy.ide.jseditor.client.texteditor;

import java.util.List;

import com.codenvy.ide.api.texteditor.HandlesUndoRedo;
import com.codenvy.ide.jseditor.client.codeassist.CompletionProposal;
import com.codenvy.ide.jseditor.client.codeassist.CompletionsSource;
import com.codenvy.ide.jseditor.client.position.PositionConverter;
import com.google.gwt.user.client.ui.Composite;

public abstract class CompositeEditorWidget extends Composite implements EditorWidget {

    @Override
    public LineStyler getLineStyler() {
        return null;
    }

    @Override
    public void onResize() {
        // Does nothing by default
    }

    @Override
    public HandlesUndoRedo getUndoRedo() {
        return null;
    }


    @Override
    public PositionConverter getPositionConverter() {
        return null;
    }

    @Override
    public void showCompletionsProposals(final List<CompletionProposal> proposals) {
        // does nothing by default
    }

    @Override
    public void showCompletionProposals(final CompletionsSource completionsSource) {
        // does nothing by default
    }

    @Override
    public void showCompletionProposals() {
        // does nothing by default
    }
}
