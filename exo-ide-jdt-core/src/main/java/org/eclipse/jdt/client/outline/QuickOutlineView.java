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
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SingleSelectionModel;

import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display;
import org.exoplatform.gwtframework.ui.client.CellTreeResource;
import org.exoplatform.gwtframework.ui.client.component.TextInput;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class QuickOutlineView extends PopupPanel implements Display, ResizeHandler {

    private CellTree.Resources res = GWT.create(CellTreeResource.class);

    private DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);

    private TextInput filterTextInput;

    private CellTree outlineTree;

    private SingleSelectionModel<ASTNode> selectionModel = new SingleSelectionModel<ASTNode>();

    private HandlerRegistration resizeHandler;

    private QuickOutlineViewModel viewModel;

    /**
     *
     */
    public QuickOutlineView(UIObject target, CompilationUnit unit) {
        super(true, true);
        getElement().setId("ideQuickOutlinePopup");
        //      setAnimationEnabled(true);
        setGlassEnabled(true);
        resizeHandler = Window.addResizeHandler(this);
        addStyleName("gwt-DialogBox");
        getElement().getStyle().setBorderColor("#B6CCE8");
        filterTextInput = new TextInput();
        filterTextInput.setWidth("100%");
        panel.addNorth(filterTextInput, 24);
        viewModel = new QuickOutlineViewModel(unit, selectionModel);
        outlineTree = new CellTree(viewModel, null, res);
        outlineTree.getElement().setId("ideQuickOutlineTree");
        outlineTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

        setModal(false);
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(outlineTree);
        panel.add(scrollPanel);

        setSize("300px", "400px");
        setWidget(panel);
        setPopupPosition(target.getAbsoluteLeft() + target.getOffsetWidth() / 2 - 300,
                         target.getAbsoluteTop() + target.getOffsetHeight() / 4);

        show();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                filterTextInput.getElement().focus();
            }
        });
    }

    /**
     * Make the node visible - open all its parent nodes.
     *
     * @param node
     *         node to open.
     */
    public void openNode(ASTNode node) {
        ASTNode parent = node;
        TreeNode treeNode = outlineTree.getRootTreeNode();

        // Get the list of node's parents (need to open), sorted from the farthest parent.
        List<ASTNode> parents = new ArrayList<ASTNode>();
        while (parent.getParent() != null && !(parent.getParent() instanceof CompilationUnit)) {
            parent = parent.getParent();
            parents.add(0, parent);
        }

        if (parents.isEmpty()) {
            selectionModel.setSelected(node, true);
            for (int i = 0; i < treeNode.getChildCount(); i++) {
                ASTNode childValue = (ASTNode)treeNode.getChildValue(i);
                if (childValue.equals(node)) {
                    treeNode.setChildOpen(i, false);
                    treeNode.setChildOpen(i, true);
                    return;
                }

            }
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
                    selectionModel.setSelected(p, true);
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
    }

    /** @see org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display#getSingleSelectionModel() */
    @Override
    public SingleSelectionModel<ASTNode> getSingleSelectionModel() {
        return selectionModel;
    }

    /** @see org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display#selectNode(org.eclipse.jdt.client.core.dom.ASTNode) */
    @Override
    public void selectNode(ASTNode node) {
        //      selectionModel.setSelected(node, true);
        openNode(node);
    }

    /** @see org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display#addKeysHandler(com.google.gwt.event.dom.client.KeyDownHandler) */
    @Override
    public HandlerRegistration addKeysHandler(NativePreviewHandler handler) {
        return Event.addNativePreviewHandler(handler);
    }

    /** @see org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display#getPanel() */
    @Override
    public HasCloseHandlers<PopupPanel> getPanel() {
        return this;
    }

    /** @see org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display#close() */
    @Override
    public void close() {
        resizeHandler.removeHandler();
        hide(true);
    }

    /** @see org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display#setFocusInTree() */
    @Override
    public void setFocusInTree() {
        outlineTree.setFocus(true);
    }

    /** @see com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google.gwt.event.logical.shared.ResizeEvent) */
    @Override
    public void onResize(ResizeEvent event) {
        close();
    }

    /** @see org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display#addDoubleClickHandler(com.google.gwt.event.dom.client
     * .DoubleClickHandler) */
    @Override
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return outlineTree.addDomHandler(handler, DoubleClickEvent.getType());
    }

    /** @see org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display#getFilterInput() */
    @Override
    public HasValue<String> getFilterInput() {
        return filterTextInput;
    }

    /** @see org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display#getViewModel() */
    @Override
    public QuickOutlineViewModel getViewModel() {
        return viewModel;
    }

    /** @see org.eclipse.jdt.client.outline.QuickOutlinePresenter.Display#openAllNodes() */
    @Override
    public void openAllNodes() {

        openAllNodes(outlineTree.getRootTreeNode());

    }

    private void openAllNodes(TreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            node.setChildOpen(i, false);
            TreeNode setChildOpen = node.setChildOpen(i, true);
            if (setChildOpen != null)
                openAllNodes(setChildOpen);
        }
    }

}
