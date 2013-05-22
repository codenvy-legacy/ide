/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.client.datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.java.client.JavaClientBundle;
import org.exoplatform.ide.extension.java.shared.DataSourceOptions;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ConfigureDatasourceView extends ViewImpl implements 
    org.exoplatform.ide.extension.java.client.datasource.ConfigureDatasourcePresenter.Display {

    /*
     * Id of Select Workspace View
     */
    public static final String ID = "ideConfigureDatasourceView";

    /** Initial width of this view. */
    private static final int WIDTH = 650;

    /** Initial height of this view. */
    private static final int HEIGHT = 350;

    private static final String TITLE = "Configure Datasource";
        
    private static ConfigureDatasourceViewUiBinder uiBinder = GWT.create(ConfigureDatasourceViewUiBinder.class);

    interface ConfigureDatasourceViewUiBinder extends UiBinder<Widget, ConfigureDatasourceView> {
    }
    
    @UiField
    IconButton addButton;
    
    @UiField
    IconButton removeButton;
    
    @UiField
    ImageButton okButton;
    
    @UiField
    ImageButton cancelButton;
    
    @UiField
    DatasourceListGrid datasourceListGrid;
    
    @UiField
    DatasourceOptionsEditor datasourceOptionsEditor;
    
    public ConfigureDatasourceView() {
        super(ID, "popup", TITLE, new Image(JavaClientBundle.INSTANCE.datasource()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
    }

    @Override
    public HasClickHandlers addButton() {
        return addButton;
    }

    @Override
    public HasClickHandlers removeButton() {
        return removeButton;
    }

    @Override
    public HasClickHandlers okButton() {
        return okButton;
    }

    @Override
    public HasClickHandlers cancelButton() {
        return cancelButton;
    }

    @Override
    public void setRemoveButtonEnabled(boolean enabled) {
        removeButton.setEnabled(enabled);
    }

    @Override
    public void setOkButtonEnabled(boolean enabled) {
        okButton.setEnabled(enabled);
    }

    @Override
    public ListGridItem<DataSourceOptions> datasourceListGrid() {
        return datasourceListGrid;
    }

    @Override
    public void editDatasource(DataSourceOptions datasource) {
        datasourceOptionsEditor.setDatasource(datasource);
    }

    @Override
    public void setDatasourceChangedHandler(DatasourceChangedHandler handler) {
        datasourceOptionsEditor.setDatasourceChangedHandler(handler);
    }

    @Override
    public void updateDatasourceListGrid() {
        datasourceListGrid.redraw();
    }

    @Override
    public DataSourceOptions getSelectedDatasource() {
        List<DataSourceOptions> selectedItems = datasourceListGrid.getSelectedItems();
        if (selectedItems == null || selectedItems.isEmpty()) {
            return null;
        }
        
        return selectedItems.get(0);
    }

}
