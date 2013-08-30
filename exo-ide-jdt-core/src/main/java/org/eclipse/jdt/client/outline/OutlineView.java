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
package org.eclipse.jdt.client.outline;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SingleSelectionModel;

import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.ImportDeclaration;
import org.exoplatform.gwtframework.ui.client.CellTreeResource;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * View for Java Outline tree.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 4:29:29 PM anya $
 */
public class OutlineView extends ViewImpl implements OutlinePresenter.Display {
    private static final String VIEW_ID = "exoJavaOutlineView";

    /** Scroll panel, which contains tree. */
    private ScrollPanel scrollPanel;

    private CellTree.Resources res = GWT.create(CellTreeResource.class);

    private CellTree cellTree;

    private OutlineTreeViewModel outlineTreeViewModel;

    private SingleSelectionModel<Object> selectionModel;

    private EmptyTreeMessage loadingMessage = new EmptyTreeMessage(new Image(JavaClientBundle.INSTANCE.loader()),
                                                                   "Loading...");

    private EmptyTreeMessage emptyTreeMessage = new EmptyTreeMessage(null, "");

    public OutlineView() {
        super(VIEW_ID, ViewType.INFORMATION, "Outline", new Image(JavaClientBundle.INSTANCE.outline()));
        selectionModel = new SingleSelectionModel<Object>();

        scrollPanel = new ScrollPanel();

        outlineTreeViewModel = new OutlineTreeViewModel(selectionModel);
        cellTree = new CellTree(outlineTreeViewModel, null, res);
        cellTree.getElement().setId("ideOutlineTreeGrid");
        cellTree.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        // Keyboard is disabled because of the selection problem (when selecting programmatically), if
        // KeyboardSelectionPolicy.BOUND_TO_SELECTION is set
        // and because of the focus border, when use KeyboardSelectionPolicy.ENABLED.
        cellTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
        outlineTreeViewModel.getDataProvider().getList().add(loadingMessage);

        scrollPanel.add(cellTree);
        add(scrollPanel);

        /**
         * when node opened we start fixing properly appear of scrollbar
         */
        cellTree.addOpenHandler(new OpenHandler<TreeNode>() {
            @Override
            public void onOpen(OpenEvent<TreeNode> event) {
                cellTreeScrollBarFix();
            }
        });

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                cellTree.getElement().getFirstChildElement().getStyle().setOverflow(Style.Overflow.VISIBLE);
            }
        });
    }

    /** This fix need for properly working scrollbar in outline view, solution is not good, but for this moment it works. */
    private void cellTreeScrollBarFix() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                NodeList<Element> elements = cellTree.getElement().getElementsByTagName("div");
                for (int i = 0; i < elements.getLength(); i++) {
                    Element el = elements.getItem(i);
                    if (el.hasAttribute("role") && el.getAttribute("role").equals("treeitem")) {
                        if (el.getChildCount() == 2) {
                            com.google.gwt.user.client.Element uel = el.getChild(1).cast();
                            uel.getStyle().clearOverflow(); // <- !!! this allow to show scrollbar
                        }
                    }
                }
            }
        });
    }

    /** @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#updateOutline(org.eclipse.jdt.client.core.dom.CompilationUnit) */
    @Override
    public void updateOutline(CompilationUnit cUnit) {
        outlineTreeViewModel.getDataProvider().getList().clear();
        if (cUnit == null) {
            outlineTreeViewModel.getDataProvider().getList().add(loadingMessage);
            return;
        }
        GetChildrenVisitor visitor = new GetChildrenVisitor();
        visitor.visit(cUnit);

        if (visitor.getNodes().isEmpty()) {
            outlineTreeViewModel.getDataProvider().getList().add(emptyTreeMessage);
        } else {
            outlineTreeViewModel.getDataProvider().getList().addAll(visitor.getNodes());
        }

        cellTreeScrollBarFix();
    }

    /** @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#getSingleSelectionModel() */
    @Override
    public SingleSelectionModel<Object> getSingleSelectionModel() {
        return selectionModel;
    }

    /** @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#selectNode(org.eclipse.jdt.client.core.dom.ASTNode) */
    @Override
    public void selectNode(ASTNode node) {
        selectionModel.setSelected(node, true);
        openNode(node);
    }

    /** @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#focusInTree() */
    @Override
    public void focusInTree() {
        cellTree.setFocus(true);
    }

    /** @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#getNodes() */
    @Override
    public List<Object> getNodes() {
        return outlineTreeViewModel.getDataProvider().getList();
    }

    /**
     * Make the node visible - open all its parent nodes.
     *
     * @param node
     *         node to open.
     */
    public void openNode(ASTNode node) {
        ASTNode parent = node;
        TreeNode treeNode = cellTree.getRootTreeNode();

        // Get the list of node's parents (need to open), sorted from the farthest parent.
        List<ASTNode> parents = new ArrayList<ASTNode>();
        while (parent.getParent() != null && !(parent.getParent() instanceof CompilationUnit)) {
            parent = parent.getParent();
            parents.add(0, parent);
        }

        // Open node's parents:
        for (ASTNode p : parents) {
            // Tree node may be null, if something went wrong on open operation:
            if (treeNode == null) {
                continue;
            }
            for (int i = 0; i < treeNode.getChildCount(); i++) {
                if (treeNode.getChildValue(i) instanceof ASTNode && ((ASTNode)treeNode.getChildValue(i)).equals(p)) {
                    // Temporary solution to check null state tree node after open operation, we can access child TreeNode only as the
                    // result of the open operation:
                    TreeNode tmp = treeNode.setChildOpen(i, true);

                    if (tmp == null) {
                        // Close node and try to open again:
                        treeNode.setChildOpen(i, false);
                        tmp = treeNode.setChildOpen(i, true);
                    }
                    treeNode = tmp;
                    break;
                }
            }
        }

        // Open Imports Group:
        if (node instanceof ImportDeclaration) {
            for (int i = 0; i < treeNode.getChildCount(); i++) {
                if (treeNode.getChildValue(i) instanceof ImportGroupNode) {
                    treeNode = treeNode.setChildOpen(i, true, false);
                    return;
                }
            }
        }
    }

    /** @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#getNodes(org.eclipse.jdt.client.core.dom.ASTNode) */
    @Override
    public List<Object> getNodes(ASTNode parent) {
        return outlineTreeViewModel.getChildren(parent);
    }
}
