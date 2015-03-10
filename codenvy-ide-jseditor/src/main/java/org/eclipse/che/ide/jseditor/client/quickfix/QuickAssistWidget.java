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

import javax.inject.Inject;

import org.eclipse.che.ide.jseditor.client.codeassist.Completion;
import org.eclipse.che.ide.jseditor.client.codeassist.CompletionProposal;
import org.eclipse.che.ide.jseditor.client.codeassist.CompletionResources;
import org.eclipse.che.ide.jseditor.client.popup.PopupResources;
import org.eclipse.che.ide.jseditor.client.popup.PopupWidget;
import org.eclipse.che.ide.jseditor.client.text.LinearRange;
import org.eclipse.che.ide.jseditor.client.texteditor.TextEditor;
import org.eclipse.che.ide.util.dom.Elements;
import org.eclipse.che.ide.util.loging.Log;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import elemental.dom.Element;
import elemental.dom.Node;
import elemental.events.CustomEvent;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.SpanElement;

/**
 * Widget for quick assist display.
 */
public class QuickAssistWidget extends PopupWidget<CompletionProposal> {

    /**
     * Custom event type.
     */
    private static final String CUSTOM_EVT_TYPE_VALIDATE = "itemvalidate";

    /** The related editor. */
    private final TextEditor textEditor;

    @Inject
    private CompletionResources completionResources;

    @AssistedInject
    public QuickAssistWidget(final PopupResources popupResources,
                             @Assisted final TextEditor textEditor) {
        super(popupResources);
        this.textEditor = textEditor;
    }

    public Element createItem(final CompletionProposal proposal) {
        final Element element = Elements.createLiElement(getItemStyle());
        final SpanElement icon = Elements.createSpanElement(completionResources.completionCss().proposalIcon());
        final SpanElement label = Elements.createSpanElement(completionResources.completionCss().proposalLabel());
        final SpanElement group = Elements.createSpanElement(completionResources.completionCss().proposalGroup());
        if (proposal.getIcon() != null && proposal.getIcon().getSVGImage() != null) {
            icon.appendChild((Node)proposal.getIcon().getSVGImage().getElement());
        } else if (proposal.getIcon() != null && proposal.getIcon().getImage() != null) {
            icon.appendChild((Node)proposal.getIcon().getImage().getElement());
        }
        label.setInnerHTML(proposal.getDisplayString());
        element.appendChild(icon);
        element.appendChild(label);
        element.appendChild(group);

        final EventListener validateListener = new EventListener() {
            @Override
            public void handleEvent(final Event evt) {
                proposal.getCompletion(new CompletionProposal.CompletionCallback() {
                    @Override
                    public void onCompletion(final Completion completion) {
                        try {
                            completion.apply(textEditor.getDocument());
                            final LinearRange selection = completion.getSelection(textEditor.getDocument());
                            if (selection != null) {
                                textEditor.getDocument().setSelectedRange(selection, true);
                            }
                        } catch (final Exception e) {
                            Log.error(getClass(), e);
                        }
                    }
                });
                hide();
            }
        };
        element.addEventListener(Event.DBLCLICK, validateListener, false);
        element.addEventListener(CUSTOM_EVT_TYPE_VALIDATE, validateListener, false);
        return element;
    }

    public Element getEmptyDisplay() {
        final Element noProposalMessage = Elements.createLiElement(getItemStyle());
        noProposalMessage.setTextContent("No proposals");
        return noProposalMessage;
    }

    @Override
    public void validateItem(final Element validatedItem) {
        validatedItem.dispatchEvent(createValidateEvent(CUSTOM_EVT_TYPE_VALIDATE));
        super.validateItem(validatedItem);
    }

    /* Overriden to give the popup the focus for keyboard control */
    @Override
    public boolean needsFocus() {
        return true;
    }

    private native CustomEvent createValidateEvent(String eventType) /*-{
        return new CustomEvent(eventType);
    }-*/;
}
