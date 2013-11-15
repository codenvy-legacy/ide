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
package com.codenvy.ide.ext.ssh.client.manage;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.codenvy.ide.ext.ssh.client.SshResources;
import com.codenvy.ide.ext.ssh.dto.KeyItem;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link SshKeyManagerView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class SshKeyManagerViewImpl extends Composite implements SshKeyManagerView {
    interface SshKeyManagerViewImplUiBinder extends UiBinder<Widget, SshKeyManagerViewImpl> {
    }

    private static SshKeyManagerViewImplUiBinder ourUiBinder = GWT.create(SshKeyManagerViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnGenerate;
    @UiField
    com.codenvy.ide.ui.Button btnUpload;
    @UiField
    com.codenvy.ide.ui.Button btnGenerateGithubKey;
    @UiField(provided = true)
    CellTable<KeyItem>        keys;
    @UiField(provided = true)
    final   SshResources            res;
    @UiField(provided = true)
    final   SshLocalizationConstant locale;
    private ActionDelegate          delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected SshKeyManagerViewImpl(SshResources resources, SshLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        initSshKeyTable();

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /** Creates table what contains list of available ssh keys. */
    private void initSshKeyTable() {
        keys = new CellTable<KeyItem>();
        Column<KeyItem, String> hostColumn = new Column<KeyItem, String>(new TextCell()) {
            @Override
            public String getValue(KeyItem object) {
                return object.getHost();
            }
        };
        hostColumn.setSortable(true);

        Column<KeyItem, String> publicKeyColumn = new Column<KeyItem, String>(new ButtonCell()) {
            @Override
            public String getValue(KeyItem object) {
                if (object.getPublicKeyURL() != null) {
                    return "View";
                } else {
                    return "";
                }
            }
        };
        // Creates handler on button clicked
        publicKeyColumn.setFieldUpdater(new FieldUpdater<KeyItem, String>() {
            @Override
            public void update(int index, KeyItem object, String value) {
                delegate.onViewClicked(object);
            }
        });

        Column<KeyItem, String> deleteKeyColumn = new Column<KeyItem, String>(new ButtonCell()) {
            @Override
            public String getValue(KeyItem object) {
                return "Delete";
            }
        };
        // Creates handler on button clicked
        deleteKeyColumn.setFieldUpdater(new FieldUpdater<KeyItem, String>() {
            @Override
            public void update(int index, KeyItem object, String value) {
                delegate.onDeleteClicked(object);
            }
        });

        keys.addColumn(hostColumn, "Host");
        keys.addColumn(publicKeyColumn, "Public Key");
        keys.addColumn(deleteKeyColumn, "Delete");
        keys.setColumnWidth(hostColumn, 50, Style.Unit.PCT);
        keys.setColumnWidth(publicKeyColumn, 30, Style.Unit.PX);
        keys.setColumnWidth(deleteKeyColumn, 30, Style.Unit.PX);

        // don't show loading indicator
        keys.setLoadingIndicator(null);
    }

    /** {@inheritDoc} */
    @Override
    public void setKeys(@NotNull JsonArray<KeyItem> keys) {
        // Wraps JsonArray in java.util.List
        List<KeyItem> appList = new ArrayList<KeyItem>();
        for (int i = 0; i < keys.size(); i++) {
            appList.add(keys.get(i));
        }
        this.keys.setRowData(appList);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnGenerate")
    public void onGenerateClicked(ClickEvent event) {
        delegate.onGenerateClicked();
    }

    @UiHandler("btnUpload")
    public void onUpdateClicked(ClickEvent event) {
        delegate.onUploadClicked();
    }

    @UiHandler("btnGenerateGithubKey")
    public void onGenerateGithubKeyClicked(ClickEvent event) {
        delegate.onGenerateGithubKeyClicked();
    }
}