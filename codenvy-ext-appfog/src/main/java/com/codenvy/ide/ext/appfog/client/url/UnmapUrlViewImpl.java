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
package com.codenvy.ide.ext.appfog.client.url;

import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.AppfogResources;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link UnmapUrlView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class UnmapUrlViewImpl extends DialogBox implements UnmapUrlView {
    interface UnmapUrlViewImplUiBinder extends UiBinder<Widget, UnmapUrlViewImpl> {
    }

    private static UnmapUrlViewImplUiBinder ourUiBinder = GWT.create(UnmapUrlViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnClose;
    @UiField
    com.codenvy.ide.ui.Button btnMap;
    @UiField
    TextBox                   mapUrl;
    @UiField(provided = true)
    CellTable<String> urlsTable = new CellTable<String>();
    @UiField(provided = true)
    final   AppfogResources            res;
    @UiField(provided = true)
    final   AppfogLocalizationConstant locale;
    private ActionDelegate             delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected UnmapUrlViewImpl(AppfogResources resources, AppfogLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        createUrlsTable();

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText("Application URLs");
        this.setWidget(widget);
    }

    /** Creates urls table. */
    private void createUrlsTable() {
        Column<String, String> buttonColumn = new Column<String, String>(new ButtonCell()) {
            @Override
            public String getValue(String object) {
                return locale.unmapButton();
            }
        };

        buttonColumn.setFieldUpdater(new FieldUpdater<String, String>() {
            @Override
            public void update(int index, String object, String value) {
                delegate.onUnMapUrlClicked(object);
            }
        });

        Column<String, SafeHtml> valueColumn = new Column<String, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final String url) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        return "<a target=\"_blank\" href=\"http://" + url + "\">" + url + "</a>";
                    }
                };
                return html;
            }
        };

        // Adds headers and size of column
        urlsTable.addColumn(valueColumn, locale.applicationUnmapUrlGridUrlField());
        urlsTable.setColumnWidth(valueColumn, "75%");
        urlsTable.addColumn(buttonColumn, locale.unmapUrlListGridColumnTitle());
        urlsTable.setColumnWidth(buttonColumn, "25%");

        // don't show loading indicator
        urlsTable.setLoadingIndicator(null);
    }

    /** {@inheritDoc} */
    @Override
    public String getMapUrl() {
        return mapUrl.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setMapUrl(String url) {
        mapUrl.setText(url);
    }

    /** {@inheritDoc} */
    @Override
    public void setRegisteredUrls(JsonArray<String> urls) {
        List<String> urlsList = new ArrayList<String>();
        for (int i = 0; i < urls.size(); i++) {
            urlsList.add(urls.get(i));
        }
        urlsTable.setRowData(urlsList);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableMapUrlButton(boolean enable) {
        btnMap.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnClose")
    void onBtnCloseClick(ClickEvent event) {
        delegate.onCloseClicked();
    }

    @UiHandler("btnMap")
    void onBtnMapClick(ClickEvent event) {
        delegate.onMapUrlClicked();
    }

    @UiHandler("mapUrl")
    void onMapUrlKeyUp(KeyUpEvent event) {
        delegate.onMapUrlChanged();
    }
}