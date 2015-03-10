/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.jseditor.client.quickfix;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistCallback;
import org.eclipse.che.ide.jseditor.client.codeassist.CompletionProposal;
import org.eclipse.che.ide.jseditor.client.texteditor.TextEditor;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import elemental.dom.Element;
import elemental.html.ClientRect;

/** Implementation for {@link QuickAssistAssistant}. */
public class QuickAssistAssistantImpl implements QuickAssistAssistant {

    private final TextEditor textEditor;

    @Inject
    private QuickAssistWidgetFactory widgetFactory;

    /** The quick assist processor. */
    private QuickAssistProcessor quickAssistProcessor;

    @AssistedInject
    public QuickAssistAssistantImpl(@Assisted final TextEditor textEditor) {
        this.textEditor = textEditor;
    }

    @Override
    public void showPossibleQuickAssists(final int line, final Element anchorElement) {
        final ClientRect anchorRect = anchorElement.getBoundingClientRect();
        showPossibleQuickAssists(line, anchorRect.getRight(), anchorRect.getBottom());
    }

    @Override
    public void showPossibleQuickAssists(final int line, final float coordX, final float coordY) {
        computeQuickAssist(line, new CodeAssistCallback() {
            @Override
            public void proposalComputed(final List<CompletionProposal> proposals) {
                final QuickAssistWidget widget = widgetFactory.createWidget(textEditor);
                for (final CompletionProposal proposal: proposals) {
                    widget.addItem(proposal);
                }
                widget.show(coordX, coordY);
            }
        });
    }

    @Override
    public void computeQuickAssist(final int line, final CodeAssistCallback callback) {
        if (this.quickAssistProcessor != null) {
            final QuickAssistInvocationContext context = new QuickAssistInvocationContext(line, this.textEditor);
            this.quickAssistProcessor.computeQuickAssistProposals(context, callback);
        }
    }

    @Override
    public void setQuickAssistProcessor(final QuickAssistProcessor processor) {
        this.quickAssistProcessor = processor;
    }

    @Override
    public QuickAssistProcessor getQuickAssistProcessor() {
        return this.quickAssistProcessor;
    }

}
