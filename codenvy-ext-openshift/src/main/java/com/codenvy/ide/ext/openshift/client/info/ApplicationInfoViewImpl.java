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
 * The implementation of {@link ApplicationInfoView}.
 *
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

    /**
     * Create view.
     *
     * @param constant
     *         locale constants
     */
    @Inject
    protected ApplicationInfoViewImpl(OpenShiftLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.applicationInfoViewTitle());
        this.setWidget(widget);

        initPropertiesTable();
    }

    /** Simple cell which can display html code. */
    private class SimpleHtmlCell extends AbstractSafeHtmlCell<String> {
        /** Create simple cell which can contain html code. */
        public SimpleHtmlCell() {
            super(new SafeHtmlListRenderer());
        }

        /** {@inheritDoc} */
        @Override
        protected void render(com.google.gwt.cell.client.Cell.Context context, SafeHtml data, SafeHtmlBuilder sb) {
            sb.append(data);
        }
    }

    /** Renderer for {@link SimpleHtmlCell}. */
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

    /** Initialize properties table. */
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

        Column<ApplicationProperty, String> propertyValueColumn = new Column<ApplicationProperty, String>(new SimpleHtmlCell()) {
            @Override
            public String getValue(ApplicationProperty object) {
                return object.getPropertyValue();
            }
        };

        properties.setColumnWidth(propertyKeyColumn, 100, Style.Unit.PX);

        properties.addColumn(propertyKeyColumn, constant.applicationInfoViewPropertyNameColumn());
        properties.addColumn(propertyValueColumn, constant.applicationInfoViewPropertyValueColumn());
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationProperties(JsonArray<ApplicationProperty> properties) {
        List<ApplicationProperty> list = new ArrayList<ApplicationProperty>(properties.size());
        for (int i = 0; i < properties.size(); i++) {
            list.add(properties.get(i));
        }
        this.properties.setRowData(list);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Handler for close button.
     *
     * @param event
     */
    @UiHandler("btnClose")
    public void onCloseButtonClick(ClickEvent event) {
        delegate.onCloseClicked();
    }
}
