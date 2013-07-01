/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.git.client.branch;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.client.GitClientResources;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link BranchView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class BranchViewImpl extends DialogBox implements BranchView {
    interface BranchViewImplUiBinder extends UiBinder<Widget, BranchViewImpl> {
    }

    private static BranchViewImplUiBinder ourUiBinder = GWT.create(BranchViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnClose;
    @UiField
    com.codenvy.ide.ui.Button btnRename;
    @UiField
    com.codenvy.ide.ui.Button btnDelete;
    @UiField
    com.codenvy.ide.ui.Button btnCreate;
    @UiField
    com.codenvy.ide.ui.Button btnCheckout;
    @UiField
    ScrollPanel               branchesPanel;
    @UiField(provided = true)
    final   GitClientResources      res;
    @UiField(provided = true)
    final   GitLocalizationConstant locale;
    @UiField(provided = true)
    final   Resources               coreRes;
    private SimpleList<Branch>      branches;
    private ActionDelegate          delegate;

    /**
     * Create presenter.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected BranchViewImpl(GitClientResources resources, GitLocalizationConstant locale, Resources coreRes) {
        this.res = resources;
        this.locale = locale;
        this.coreRes = coreRes;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(locale.branchTitle());
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
                sb.appendEscaped(itemData.getDisplayName());
                sb.appendHtmlConstant("</td>");

                if (itemData.active()) {
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
        branches = SimpleList.create((SimpleList.View)breakPointsElement, coreRes.defaultSimpleListCss(),
                                     listBranchesRenderer,
                                     listBranchesDelegate);
        this.branchesPanel.add(branches);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setBranches(@NotNull JsonArray<Branch> branches) {
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
        this.center();
        this.show();
    }

    @UiHandler("btnClose")
    public void onCloseClicked(ClickEvent event) {
        delegate.onCloseClicked();
    }

    @UiHandler("btnRename")
    public void onRenameClicked(ClickEvent event) {
        delegate.onRenameClicked();
    }

    @UiHandler("btnDelete")
    public void onDeleteClicked(ClickEvent event) {
        delegate.onDeleteClicked();
    }

    @UiHandler("btnCreate")
    public void onCreateClicked(ClickEvent event) {
        delegate.onCreateClicked();
    }

    @UiHandler("btnCheckout")
    public void onCheckoutClick(ClickEvent event) {
        delegate.onCheckoutClicked();
    }
}