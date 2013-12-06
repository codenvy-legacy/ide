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

import com.codenvy.ide.ext.java.messages.Change;
import com.codenvy.ide.ext.java.messages.ProposalAppliedMessage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.RegionImpl;
import com.codenvy.ide.texteditor.api.codeassistant.Completion;
import com.codenvy.ide.texteditor.api.codeassistant.CompletionProposal;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CompletionProposalImpl implements CompletionProposal {

    private String           id;
    private String           display;
    private Image            image;
    private boolean          autoInsertable;
    private JavaParserWorker worker;

    public CompletionProposalImpl(String id, String display, Image image, boolean autoInsertable, JavaParserWorker worker) {
        this.id = id;
        this.display = display;
        this.image = image;
        this.autoInsertable = autoInsertable;
        this.worker = worker;
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
    public char[] getTriggerCharacters() {
        return new char[0];
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAutoInsertable() {
        return autoInsertable;
    }

    @Override
    public void getCompletion(final CompletionCallback callback) {
        worker.applyCAProposal(id, new JavaParserWorker.ApplyCallback() {
            @Override
            public void onApply(final ProposalAppliedMessage message) {
                callback.onCompletion(new CompletionImpl(message.changes(), message.selectionRegion()));
            }
        });
    }

    private class CompletionImpl implements Completion {

        private final Array<Change>                        changes;
        private final com.codenvy.ide.ext.java.messages.Region region;

        private CompletionImpl(Array<Change> changes, com.codenvy.ide.ext.java.messages.Region region) {
            this.changes = changes;
            this.region = region;
        }

        /** {@inheritDoc} */
        @Override
        public void apply(Document document) {
            try {
                for (Change change : changes.asIterable()) {
                    document.replace(change.offset(), change.length(), change.text());
                }
            } catch (BadLocationException e) {
                Log.error(CompletionProposalImpl.class, e);
            }
        }

        /** {@inheritDoc} */
        @Override
        public Region getSelection(Document document) {
            if (region == null) return null;
            else return new RegionImpl(region.getOffset(), region.getLength());
        }
    }
}
