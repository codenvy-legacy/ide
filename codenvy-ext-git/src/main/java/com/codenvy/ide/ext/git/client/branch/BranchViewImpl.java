/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.client.branch;

import elemental.dom.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
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

    Button btnClose;
    Button btnRename;
    Button btnDelete;
    Button btnCreate;
    Button btnCheckout;
    @UiField
    ScrollPanel branchesPanel;
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
    protected BranchViewImpl(GitResources resources,
                             GitLocalizationConstant locale,
                             com.codenvy.ide.Resources coreRes) {
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
                Ask ask = new Ask(locale.branchDelete(), locale.branchDeleteAsk(branches.getSelectionModel().getSelectedItem().getName()),
                                  new AskHandler() {

                                      @Override
                                      public void onOk() {
                                          delegate.onDeleteClicked();
                                      }
                                  });
                ask.show();
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