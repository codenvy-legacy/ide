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
package com.codenvy.ide.ext.openshift.client.info;

import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.Button;
import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationInfoViewImpl extends DialogBox implements ApplicationInfoView {

    interface ApplicationInfoViewImplUiBinder extends UiBinder<Widget, ApplicationInfoViewImpl> {
    }

    private static ApplicationInfoViewImplUiBinder uiBinder = GWT.create(ApplicationInfoViewImplUiBinder.class);

    @UiField(provided = true)
    CellTable<ApplicationProperty> properties = new CellTable<ApplicationProperty>();

    @UiField
    Button btnClose;

    @UiField(provided = true)
    final OpenShiftLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    @Inject
    protected ApplicationInfoViewImpl(OpenShiftLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.applicationInfoViewTitle());
        this.setWidget(widget);

        initPropertiesTable();
    }

    private class ListLink extends AbstractSafeHtmlCell<String> {
        /** Create Link list. */
        public ListLink() {
            super(new SafeHtmlListRenderer());
        }

        /** {@inheritDoc} */
        @Override
        protected void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb) {
            sb.append(data);
        }
    }

    private class SafeHtmlListRenderer implements SafeHtmlRenderer<String> {
        /** {@inheritDoc} */
        @Override
        public SafeHtml render(String object) {
            return new SafeHtmlBuilder().appendHtmlConstant(object).toSafeHtml();
        }

        /** {@inheritDoc} */
        @Override
        public void render(String object, SafeHtmlBuilder builder) {
            builder.appendHtmlConstant(object);
        }
    }

    private void initPropertiesTable() {
        properties.setWidth("100%", true);
        properties.setAutoHeaderRefreshDisabled(true);
        properties.setAutoFooterRefreshDisabled(true);

        final SelectionModel<ApplicationProperty> selectionModel = new NoSelectionModel<ApplicationProperty>();

        properties.setSelectionModel(selectionModel);

        Column<ApplicationProperty, String> propertyKeyColumn = new TextColumn<ApplicationProperty>() {
            @Override
            public String getValue(ApplicationProperty object) {
                return object.getPropertyName();
            }
        };

        Column<ApplicationProperty, String> propertyValueColumn = new Column<ApplicationProperty, String>(new ListLink()) {
            @Override
            public String getValue(ApplicationProperty object) {
                return object.getPropertyValue();
            }
        };

        properties.setColumnWidth(propertyKeyColumn, 100, Style.Unit.PX);

        properties.addColumn(propertyKeyColumn, constant.applicationInfoViewPropertyNameColumn());
        properties.addColumn(propertyValueColumn, constant.applicationInfoViewPropertyValueColumn());
    }

    @Override
    public void setApplicationProperties(JsonArray<ApplicationProperty> properties) {
        List<ApplicationProperty> list = new ArrayList<ApplicationProperty>(properties.size());
        for (int i = 0; i < properties.size(); i++) {
            list.add(properties.get(i));
        }
        this.properties.setRowData(list);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnClose")
    public void onCloseButtonClick(ClickEvent event) {
        delegate.onCloseClicked();
    }
}
