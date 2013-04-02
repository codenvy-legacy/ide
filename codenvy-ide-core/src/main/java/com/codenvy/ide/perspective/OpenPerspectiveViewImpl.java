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
package com.codenvy.ide.perspective;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.perspective.WorkspacePresenter.PerspectiveDescriptor;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.list.SimpleList.View;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;


/**
 * The implementation of {@link OpenPerspectiveView}.
 * Provides selecting perspective what want to open.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class OpenPerspectiveViewImpl extends DialogBox implements OpenPerspectiveView {
    private static ChangePerspectiveViewImplUiBinder uiBinder = GWT.create(ChangePerspectiveViewImplUiBinder.class);

    @UiField
    FlowPanel btnPanel;

    @UiField
    Button btnOpen;

    @UiField
    Button btnCancel;

    @UiField
    ScrollPanel listPanel;

    private ActionDelegate delegate;

    private SimpleList<PerspectiveDescriptor> list;

    private SimpleList.ListItemRenderer<PerspectiveDescriptor> listItemRenderer =
            new SimpleList.ListItemRenderer<PerspectiveDescriptor>() {
                @Override
                public void render(Element itemElement, PerspectiveDescriptor itemData) {
                    TableCellElement label = Elements.createTDElement();

                    SafeHtmlBuilder sb = new SafeHtmlBuilder();

                    // Add icon
                    sb.appendHtmlConstant("<table><tr><td>");
                    ImageResource icon = itemData.getIcon();
                    if (icon != null) {
                        sb.appendHtmlConstant("<img src=\"" + icon.getSafeUri().asString() + "\">");
                    }
                    sb.appendHtmlConstant("</td>");

                    // Add title
                    sb.appendHtmlConstant("<td>");
                    sb.appendEscaped(itemData.getTitle());
                    sb.appendHtmlConstant("</td></tr></table>");

                    label.setInnerHTML(sb.toSafeHtml().asString());

                    itemElement.appendChild(label);
                }

                @Override
                public Element createElement() {
                    return Elements.createTRElement();
                }
            };

    private SimpleList.ListEventDelegate<PerspectiveDescriptor> listDelegate =
            new SimpleList.ListEventDelegate<PerspectiveDescriptor>() {
                public void onListItemClicked(Element itemElement, PerspectiveDescriptor itemData) {
                    list.getSelectionModel().setSelectedItem(itemData);
                    delegate.selectedPerspective(itemData.getTitle());
                }

                public void onListItemDoubleClicked(Element listItemBase, PerspectiveDescriptor itemData) {
                }
            };

    interface ChangePerspectiveViewImplUiBinder extends UiBinder<Widget, OpenPerspectiveViewImpl> {
    }

    /** Create view. */
    public OpenPerspectiveViewImpl(JsonArray<PerspectiveDescriptor> perspectives, Resources resources) {
        Widget widget = uiBinder.createAndBindUi(this);

        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);

        this.listPanel.setStyleName(resources.coreCss().simpleListContainer());
        this.listPanel.add(list);

        this.setText("Open Perspective");
        this.setWidget(widget);

        list.render(perspectives);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setOpenButtonEnabled(boolean isEnabled) {
        btnOpen.setEnabled(isEnabled);
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

    @UiHandler("btnOpen")
    void onBtnOpenClick(ClickEvent event) {
        delegate.onOpenClicked();
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }
}