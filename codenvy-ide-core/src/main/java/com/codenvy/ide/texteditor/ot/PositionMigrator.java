// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.texteditor.ot;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DocOp;
import com.codenvy.ide.dto.shared.DocOpFactory;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.text.store.DocumentModel;
import com.codenvy.ide.text.store.LineNumberAndColumn;
import com.codenvy.ide.text.store.TextChange;
import com.codenvy.ide.util.ListenerRegistrar;

/**
 * A mechanism that translates file positions to/from file position at
 * earlier point in time when this position migrator was created or reset.
 * The scope of PositionMigrator object is currently opened document.
 * On "start" position migrator starts tracking text changes.
 * Calling "reset" clears tracked docops and moves tracking start to current
 * moment.
 */
public class PositionMigrator {

    private final DocumentModel.TextListener textListener = new DocumentModel.TextListener() {
        @Override
        public void onTextChange(DocumentModel document, Array<TextChange> textChanges) {
            PositionMigrator.this.onTextChange(textChanges);
        }
    };

    private final DocOpFactory docOpFactory;
    private final Array<DocOp> appliedDocOps = Collections.createArray();
    private ListenerRegistrar.Remover textListenerRemover;

    public PositionMigrator(DocOpFactory docOpFactory) {
        this.docOpFactory = docOpFactory;
    }

    /**
     * Converts given position as it was at the time when tracking started,
     * to current position.
     *
     * @param lineNumber
     *         old position line number
     * @param column
     *         old position column
     * @return current position line number and column
     */
    public LineNumberAndColumn migrateToNow(int lineNumber, int column) {
        // TODO: Cache the result.
        DocOp docOp = composeCurrentDocOps();
        if (docOp == null) {
            return LineNumberAndColumn.from(lineNumber, column);
        }
        PositionTransformer positionTransformer =
                new PositionTransformer(lineNumber, column);
        positionTransformer.transform(docOp);
        return LineNumberAndColumn.from(positionTransformer.getLineNumber(),
                                        positionTransformer.getColumn());
    }

    public boolean haveChanges() {
        return composeCurrentDocOps() != null;
    }

    /**
     * Converts given position at current time to position as it was at the time
     * when tracking started.
     *
     * @param lineNumber
     *         current position line number
     * @param column
     *         current position column
     * @return old position line number and column
     */
    public LineNumberAndColumn migrateFromNow(int lineNumber, int column) {
        // TODO: Cache the result.
        DocOp docOp = composeCurrentDocOps();
        if (docOp == null) {
            return LineNumberAndColumn.from(lineNumber, column);
        }
        PositionTransformer positionTransformer =
                new PositionTransformer(lineNumber, column);
        positionTransformer.transform(Inverter.invert(docOpFactory, docOp));
        return LineNumberAndColumn.from(positionTransformer.getLineNumber(),
                                        positionTransformer.getColumn());
    }

    /**
     * Forgets about all currently recorded text changes and continues tracking
     * if started.
     */
    public void reset() {
        appliedDocOps.clear();
    }

    /**
     * Starts tracking text changes. Text changes collected so far are discarded.
     *
     * @param textListenerRegistrar
     *         listener registrar to use for listening text
     *         changes
     */
    public void start(ListenerRegistrar<DocumentModel.TextListener> textListenerRegistrar) {
        reset();
        stop();
        this.textListenerRemover = textListenerRegistrar.add(textListener);
    }

    /** Stops tracking text changes, keeping currently recorded text changes. */
    public void stop() {
        if (textListenerRemover != null) {
            textListenerRemover.remove();
            textListenerRemover = null;
        }
    }

    private void onTextChange(Array<TextChange> textChanges) {
        try {
            appliedDocOps.add(DocOpUtils.createFromTextChanges(docOpFactory, textChanges));
        } catch (Composer.ComposeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Composes currently collected doc ops into a single doc op and returns it.
     *
     * @return composed doc op or {@code null} if no doc ops were collected so far
     */
    private DocOp composeCurrentDocOps() {
        if (appliedDocOps.size() < 2) {
            return appliedDocOps.size() > 0 ? appliedDocOps.get(0) : null;
        }
        try {
            DocOp docOp = Composer.compose(docOpFactory, appliedDocOps.asIterable());
            appliedDocOps.clear();
            appliedDocOps.add(docOp);
            return docOp;
        } catch (Composer.ComposeException e) {
            throw new RuntimeException(e);
        }
    }
}
