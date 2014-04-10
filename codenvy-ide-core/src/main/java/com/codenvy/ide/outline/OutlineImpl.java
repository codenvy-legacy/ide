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
package com.codenvy.ide.outline;

import elemental.events.MouseEvent;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PropertyListener;
import com.codenvy.ide.text.TextUtilities;
import com.codenvy.ide.text.store.LineInfo;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.outline.CodeBlock;
import com.codenvy.ide.texteditor.api.outline.OutlineModel;
import com.codenvy.ide.texteditor.api.outline.OutlinePresenter;
import com.codenvy.ide.texteditor.selection.SelectionModel.CursorListener;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.Tree.Listener;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;


/**
 * Default implementation of {@link OutlinePresenter}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OutlineImpl implements OutlinePresenter {

    public interface OutlineView extends IsWidget {
        void renderTree();

        void rootChanged(CodeBlock newRoot);

        void setTreeEventHandler(Listener<CodeBlock> listener);

        void selectAndExpand(CodeBlock block);
    }

    private OutlineView view;

    private TextEditorViewImpl editor;

    private final OutlineModel model;

    private CodeBlockDataAdapter dataAdapter;

    private CodeBlock blockToSync;

    private boolean thisCursorMove;

    /**
     *
     */
    public OutlineImpl(Resources resources, OutlineModel model,
                       TextEditorPartView editor, TextEditorPartPresenter editorPresenter) {
        this.model = model;
        this.editor = (TextEditorViewImpl)editor;
        dataAdapter = new CodeBlockDataAdapter();
        view = new OutlineViewImpl(resources, dataAdapter, model.getRenderer());
        editorPresenter.addPropertyListener(new PropertyListener() {

            @Override
            public void propertyChanged(PartPresenter source, int propId) {
                if (EditorPartPresenter.PROP_INPUT == propId) {
                    bind();
                }
            }
        });
        model.setListener(new OutlineModel.OutlineModelListener() {

            @Override
            public void rootUpdated() {
                view.renderTree();
            }

            @Override
            public void rootChanged(CodeBlock newRoot) {
                view.rootChanged(newRoot);
            }

        });
    }

    /**
     *
     */
    private void bind() {
        view.setTreeEventHandler(new Listener<CodeBlock>() {

            @Override
            public void onNodeAction(TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeClosed(TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<CodeBlock> node, MouseEvent event) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<CodeBlock> node, MouseEvent event) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<CodeBlock> node) {
            }

            @Override
            public void onNodeSelected(TreeNodeElement<CodeBlock> node, SignalEvent event) {
                thisCursorMove = true;
                CodeBlock data = node.getData();
                editor.getSelection().setCursorPosition(data.getOffset());
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
            }

            @Override
            public void onRootDragDrop(MouseEvent event) {
            }
        });

        editor.getSelection().getCursorListenerRegistrar().add(new CursorListener() {

            @Override
            public void onCursorChange(LineInfo lineInfo, int column, boolean isExplicitChange) {
                if (thisCursorMove) {
                    thisCursorMove = false;
                    return;
                }
                if (model.getRoot() == null) {
                    return;
                }
                int number = lineInfo.number();
                final int offset = TextUtilities.getOffset(editor.getDocument(), number, column);
                blockToSync = null;
                Tree.iterateDfs(model.getRoot(), dataAdapter, new Tree.Visitor<CodeBlock>() {

                    @Override
                    public boolean shouldVisit(CodeBlock node) {
                        if (offset + 1 > node.getOffset() && offset - 1 < node.getOffset() + node.getLength()) {
                            return true;
                        } else
                            return false;
                    }

                    @Override
                    public void visit(CodeBlock node, boolean willVisitChildren) {
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

    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

}
