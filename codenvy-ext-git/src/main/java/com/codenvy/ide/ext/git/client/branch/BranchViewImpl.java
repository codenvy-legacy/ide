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
package com.codenvy.ide.ext.git.client.branch;

import elemental.dom.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link BranchView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class BranchViewImpl extends Window implements BranchView {
    interface BranchViewImplUiBinder extends UiBinder<Widget, BranchViewImpl> {
    }

    private static BranchViewImplUiBinder ourUiBinder = GWT.create(BranchViewImplUiBinder.class);

    Button                                btnClose;
    Button                                btnRename;
    Button                                btnDelete;
    Button                                btnCreate;
    Button                                btnCheckout;
    @UiField
    ScrollPanel               branchesPanel;
    @UiField(provided = true)
    final   GitResources            res;
    @UiField(provided = true)
    final   GitLocalizationConstant locale;
    private SimpleList<Branch>      branches;
    private ActionDelegate          delegate;

    /**
     * Create presenter.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected BranchViewImpl(GitResources resources, GitLocalizationConstant locale, com.codenvy.ide.Resources coreRes) {
        this.res = resources;
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.branchTitle());
        this.setWidget(widget);

        TableElement breakPointsElement = Elements.createTableElement();
        breakPointsElement.setAttribute("style", "width: 100%");
        SimpleList.ListEventDelegate<Branch> listBranchesDelegate = new SimpleList.ListEventDelegate<Branch>() {
            public void onListItemClicked(Element itemElement, Branch itemData) {
                branches.getSelectionModel().setSelectedItem(itemData);
                delegate.onBranchSelected(itemData);
            }

            public void onListItemDoubleClicked(Element listItemBase, Branch itemData) {
            }
        };
        SimpleList.ListItemRenderer<Branch> listBranchesRenderer = new SimpleList.ListItemRenderer<Branch>() {
            @Override
            public void render(Element itemElement, Branch itemData) {
                TableCellElement label = Elements.createTDElement();

                SafeHtmlBuilder sb = new SafeHtmlBuilder();

                sb.appendHtmlConstant("<table><tr><td>");
                sb.appendHtmlConstant("<div id=\"" + UIObject.DEBUG_ID_PREFIX + "git-branches-" + itemData.getDisplayName() + "\">");
                sb.appendEscaped(itemData.getDisplayName());
                sb.appendHtmlConstant("</td>");

                if (itemData.isActive()) {
                    ImageResource icon = res.currentBranch();
                    sb.appendHtmlConstant("<td><img src=\"" + icon.getSafeUri().asString() + "\"></td>");
                }

                sb.appendHtmlConstant("</tr></table>");

                label.setInnerHTML(sb.toSafeHtml().asString());

                itemElement.appendChild(label);
            }

            @Override
            public Element createElement() {
                return Elements.createTRElement();
            }
        };
        branches = SimpleList
                .create((SimpleList.View)breakPointsElement, coreRes.defaultSimpleListCss(), listBranchesRenderer, listBranchesDelegate);
        this.branchesPanel.add(branches);
        
        createButtons();
    }
    
    private void createButtons() {
        btnClose = createButton(locale.buttonClose(), "git-branches-close", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });
        getFooter().add(btnClose);

        btnRename = createButton(locale.buttonRename(), "git-branches-rename", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onRenameClicked();
            }
        });
        getFooter().add(btnRename);

        btnDelete = createButton(locale.buttonDelete(), "git-branches-delete", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onDeleteClicked();
            }
        });
        getFooter().add(btnDelete);

        btnCreate = createButton(locale.buttonCreate(), "git-branches-create", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCreateClicked();
            }
        });
        getFooter().add(btnCreate);

        btnCheckout = createButton(locale.buttonCheckout(), "git-branches-checkout", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCheckoutClicked();
            }
        });
        getFooter().add(btnCheckout);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setBranches(@NotNull Array<Branch> branches) {
        this.branches.render(branches);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableDeleteButton(boolean enabled) {
        btnDelete.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableCheckoutButton(boolean enabled) {
        btnCheckout.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableRenameButton(boolean enabled) {
        btnRename.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}