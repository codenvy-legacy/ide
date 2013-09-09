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
package org.exoplatform.ide.client.outline;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import org.exoplatform.gwtframework.ui.client.CellTreeResource;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OutlineView extends ViewImpl implements org.exoplatform.ide.client.outline.OutlinePresenter.Display {

    private static final String ID = "ideOutlineView";

    /** Initial width of this view */
    private static final int WIDTH = 250;

    /** Initial height of this view */
    private static final int HEIGHT = 450;

    private static OutlineViewUiBinder uiBinder = GWT.create(OutlineViewUiBinder.class);

    private CellTree.Resources res = GWT.create(CellTreeResource.class);

    private SingleSelectionModel<Object> selectionModel;

    private OutlineTreeViewModel outlineTreeViewModel;

    private EmptyTreeMessage loadingMessage = new EmptyTreeMessage(new Image(IDEImageBundle.INSTANCE.loader()),
                                                                   "Loading...");

    private EmptyTreeMessage emptyTreeMessage = new EmptyTreeMessage(null, "");

    interface OutlineViewUiBinder extends UiBinder<Widget, OutlineView> {
    }

    @UiField
    HTMLPanel outlineDisabledPanel;

    @UiField
    FlowPanel mainPanel;

    private boolean outlineAvailable = false;

    private CellTree outlineTree;

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.outlineTitle();

    public OutlineView() {
        super(ID, "information", TITLE, new Image(IDEImageBundle.INSTANCE.outline()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        selectionModel = new SingleSelectionModel<Object>();

        outlineTreeViewModel = new OutlineTreeViewModel(selectionModel);
        outlineTree = new CellTree(outlineTreeViewModel, null, res);
        outlineTree.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);

        // Keyboard is disabled because of the selection problem (when selecting programmatically), if
        // KeyboardSelectionPolicy.BOUND_TO_SELECTION is set
        // and because of the focus border, when use KeyboardSelectionPolicy.ENABLED.
        outlineTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

        outlineTree.getElement().setId("ideOutlineTreeGrid");
        outlineTree.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        mainPanel.add(outlineTree);
        outlineTreeViewModel.getDataProvider().getList().add(loadingMessage);
        outlineTree.setVisible(false);

        /**
         * when node opened we start fixing properly appear of scrollbar
         */
        outlineTree.addOpenHandler(new OpenHandler<TreeNode>() {
            @Override
            public void onOpen(OpenEvent<TreeNode> event) {
                cellTreeScrollBarFix();
            }
        });

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                outlineTree.getElement().getFirstChildElement().getStyle().setOverflow(Style.Overflow.VISIBLE);
            }
        });
    }

    /** This fix need for properly working scrollbar in outline view, solution is not good, but for this moment it works. */
    private void cellTreeScrollBarFix() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                NodeList<Element> elements = outlineTree.getElement().getElementsByTagName("div");
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

    @Override
    public void selectToken(TokenBeenImpl token) {
        if (token != null) {
            selectionModel.setSelected(token, true);
            openNode(token);
        }
    }

    public void openNode(TokenBeenImpl token) {
        TokenBeenImpl parent = token;
        TreeNode treeNode = outlineTree.getRootTreeNode();

        // Get the list of node's parents (need to open), sorted from the farthest parent.
        List<TokenBeenImpl> parents = new ArrayList<TokenBeenImpl>();
        while (parent.getParentToken() != null && parent.getParentToken().getName() != null
               && parent.getParentToken().getType() != null) {
            parent = parent.getParentToken();
            parents.add(0, parent);
        }

        // Open node's parents:
        for (TokenBeenImpl p : parents) {
            // Tree node may be null, if something went wrong on open operation:
            if (treeNode == null) {
                continue;
            }
            for (int i = 0; i < treeNode.getChildCount(); i++) {
                if (treeNode.getChildValue(i) instanceof TokenBeenImpl
                    && ((TokenBeenImpl)treeNode.getChildValue(i)).equals(p)) {
                    // Temporary solution to check null state tree node after open operation, we can access child TreeNode only as
                    // the result of the open operation:
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
    }

    @Override
    public void setOutlineAvailable(boolean available) {
        if (outlineAvailable == available) {
            return;
        }

        outlineAvailable = available;
        outlineDisabledPanel.setVisible(!available);
        outlineTree.setVisible(available);
    }

    @Override
    public void deselectAllTokens() {
        if (selectionModel.getSelectedObject() != null) {
            selectionModel.setSelected(selectionModel.getSelectedObject(), false);
        }
    }

    /** @see org.exoplatform.ide.client.outline.OutlinePresenter.Display#setValue(java.util.List) */
    @Override
    public void setValue(List<TokenBeenImpl> tokens) {
        outlineTreeViewModel.getDataProvider().getList().clear();
        if (tokens == null) {
            outlineTreeViewModel.getDataProvider().getList().add(loadingMessage);
        } else {
            if (tokens.isEmpty()) {
                outlineTreeViewModel.getDataProvider().getList().add(emptyTreeMessage);
            } else {
                for (TokenBeenImpl token : tokens) {
                    if (token.getName() != null && token.getType() != null) {
                        outlineTreeViewModel.getDataProvider().getList().add(token);
                    }
                }
            }
        }
        
        cellTreeScrollBarFix();
    }

    /** @see org.exoplatform.ide.client.outline.OutlinePresenter.Display#getSingleSelectionModel() */
    @Override
    public SingleSelectionModel<Object> getSingleSelectionModel() {
        return selectionModel;
    }

    /** @see org.exoplatform.ide.client.outline.OutlinePresenter.Display#focusInTree() */
    @Override
    public void focusInTree() {
        outlineTree.setFocus(true);
    }

}
