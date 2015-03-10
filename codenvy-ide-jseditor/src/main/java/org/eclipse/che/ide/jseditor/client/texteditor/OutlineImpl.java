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
package org.eclipse.che.ide.jseditor.client.texteditor;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.texteditor.outline.CodeBlock;
import org.eclipse.che.ide.api.texteditor.outline.OutlineModel;
import org.eclipse.che.ide.api.texteditor.outline.OutlinePresenter;
import org.eclipse.che.ide.jseditor.client.document.Document;
import org.eclipse.che.ide.jseditor.client.text.TextPosition;
import org.eclipse.che.ide.outline.CodeBlockDataAdapter;
import org.eclipse.che.ide.outline.OutlineView;
import org.eclipse.che.ide.outline.OutlineViewImpl;
import org.eclipse.che.ide.texteditor.selection.CursorModelWithHandler;
import org.eclipse.che.ide.texteditor.selection.CursorModelWithHandler.CursorHandler;
import org.eclipse.che.ide.ui.tree.Tree;
import org.eclipse.che.ide.ui.tree.Tree.Listener;
import org.eclipse.che.ide.ui.tree.TreeNodeElement;
import org.eclipse.che.ide.util.input.SignalEvent;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;


/**
 * Implementation of {@link OutlinePresenter} for the embedded editors.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OutlineImpl implements OutlinePresenter {

    private final OutlineView view;

    private final OutlineModel model;
    private final CodeBlockDataAdapter dataAdapter;

    private CodeBlock blockToSync;

    private boolean thisCursorMove;

    public OutlineImpl(final Resources resources, final OutlineModel model) {
        this.model = model;
        this.dataAdapter = new CodeBlockDataAdapter();
        this.view = new OutlineViewImpl(resources, this.dataAdapter, this.model.getRenderer());

        this.model.setListener(new OutlineModel.OutlineModelListener() {

            @Override
            public void rootUpdated() {
                view.renderTree();
            }

            @Override
            public void rootChanged(final CodeBlock newRoot) {
                view.rootChanged(newRoot);
            }

        });
    }

    /**
     * Binds the Outline to the given cursor model.
     * @param cursorModel the cursor model
     * @param document the document
     */
    void bind(final CursorModelWithHandler cursorModel, final Document document) {
        if (cursorModel == null) {
            Log.debug(OutlineImpl.class, "No cursor model !!");
            return;
        }
        this.view.setTreeEventHandler(new Listener<CodeBlock>() {

            @Override
            public void onNodeAction(final TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeClosed(final TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeContextMenu(final int mouseX, final int mouseY, final TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeDragStart(final TreeNodeElement<CodeBlock> node, final MouseEvent event) {
            }

            @Override
            public void onNodeDragDrop(final TreeNodeElement<CodeBlock> node, final MouseEvent event) {
            }

            @Override
            public void onNodeExpanded(final TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeSelected(final TreeNodeElement<CodeBlock> node, final SignalEvent event) {
                thisCursorMove = true;
                final CodeBlock data = node.getData();
                final int offset = data.getOffset();
                if (cursorModel != null) {
                    cursorModel.setCursorPosition(offset);
                }
            }

            @Override
            public void onRootContextMenu(final int mouseX, final int mouseY) {
            }

            @Override
            public void onRootDragDrop(final MouseEvent event) {
            }

            @Override
            public void onKeyboard(final KeyboardEvent event) {
            }
        });

        if (document != null) {
            Log.debug(OutlineImpl.class, "Cursor model available, adding cursor handler");
            cursorModel.addCursorHandler(new CursorHandler() {

                @Override
                public void onCursorChange(final int line, final int column, final boolean isExplicitChange) {
                    if (thisCursorMove) {
                        thisCursorMove = false;
                        return;
                    }
                    if (model.getRoot() == null) {
                        return;
                    }
                    final TextPosition position = document.getCursorPosition();
                    final int offset = document.getIndexFromPosition(position);
                    blockToSync = null;
                    Tree.iterateDfs(model.getRoot(), dataAdapter, new Tree.Visitor<CodeBlock>() {

                        @Override
                        public boolean shouldVisit(final CodeBlock node) {
                            if (offset + 1 > node.getOffset() && offset - 1 < node.getOffset() + node.getLength()) {
                                return true;
                            } else {
                                return false;
                            }
                        }

                        @Override
                        public void visit(final CodeBlock node, final boolean willVisitChildren) {
                            blockToSync = node;
                        }

                    });
                    if (blockToSync != null) {
                        if (!CodeBlock.ROOT_TYPE.equals(blockToSync.getType())) {
                            view.selectAndExpand(blockToSync);
                            return;
                        }
                    }
                }
            });
        } else {
            Log.debug(OutlineImpl.class, "No document !!");
        }
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(view);
    }

}
