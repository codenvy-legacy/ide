/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.texteditor.api.codeassistant.CompletionProposal;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CompletionProposalImpl implements CompletionProposal {

    private String display;

    private Image image;

    private boolean autoInsertable;

    public CompletionProposalImpl(String display, Image image, boolean autoInsertable) {
        this.display = display;
        this.image = image;
        this.autoInsertable = autoInsertable;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(Document document) {
    }

    /** {@inheritDoc} */
    @Override
    public Region getSelection(Document document) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Widget getAdditionalProposalInfo() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayString() {
        return display;
    }

    /** {@inheritDoc} */
    @Override
    public Image getImage() {
        return image;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(Document document, char trigger, int offset) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValidFor(Document document, int offset) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public char[] getTriggerCharacters() {
        return new char[0];
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAutoInsertable() {
        return autoInsertable;
    }
}
