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
package com.codenvy.ide.ext.ssh.client.manage;

import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.codenvy.ide.ext.ssh.client.SshResources;
import com.codenvy.ide.ext.ssh.shared.KeyItem;
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
    public void setKeys(JsonArray<KeyItem> keys) {
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