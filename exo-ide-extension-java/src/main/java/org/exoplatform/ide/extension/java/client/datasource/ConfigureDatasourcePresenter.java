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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.java.client.datasource.service.DatasourceClientService;
import org.exoplatform.ide.extension.java.shared.DataSourceOptions;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class ConfigureDatasourcePresenter implements ConfigureDatasourceHandler, ViewClosedHandler,
        VfsChangedHandler, ConfigurationReceivedSuccessfullyHandler,
        ProjectOpenedHandler, ProjectClosedHandler, TreeRefreshedHandler {

    public interface Display extends IsView {
        
        ListGridItem<DataSourceOptions> datasourceListGrid();
        
        void updateDatasourceListGrid();
        
        DataSourceOptions getSelectedDatasource();
        
        void editDatasource(DataSourceOptions datasource);
        
        void setDatasourceChangedHandler(DatasourceChangedHandler handler);
        
        HasClickHandlers addButton();
        
        HasClickHandlers removeButton();
        
        HasClickHandlers okButton();
        
        HasClickHandlers cancelButton();
        
        void setRemoveButtonEnabled(boolean enabled);
        
        void setOkButtonEnabled(boolean enabled);
        
    }

    private Display display;

    private VirtualFileSystemInfo vfsInfo;
    
    private IDEConfiguration ideConfiguration;
    
    private IDEProject project;
    
    private List<DataSourceOptions> datasourceList;
    
    public ConfigureDatasourcePresenter() {
        IDE.getInstance().addControl(new ConfigureDatasourceControl());
        IDE.addHandler(ConfigureDatasourceEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);

        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
        
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        
        IDE.addHandler(TreeRefreshedEvent.TYPE, this);
    }

    @Override
    public void onConfigureDatasource(ConfigureDatasourceEvent event) {
        if (display != null) {
            return;
        }
        
        DatasourceClientService.getInstance().getAll(vfsInfo.getId(), project.getId(), new AsyncCallback<List<DataSourceOptions>>() {
            @Override
            public void onFailure(Throwable caught) {
                IDE.fireEvent(new ExceptionThrownEvent(caught, "Error loading Datasource list"));
            }

            @Override
            public void onSuccess(List<DataSourceOptions> result) {
                datasourceList = result;
                createView();                
            }            
        });        
    }
    
    private void createView() {
        display = GWT.create(Display.class);
        bindDisplay();
        display.datasourceListGrid().setValue(datasourceList);
        IDE.getInstance().openView(display.asView());
    }

    private void bindDisplay() {
        display.setOkButtonEnabled(false);
        display.setRemoveButtonEnabled(false);
        
        display.addButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                newConfiguration();
            }
        });
        
        display.removeButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                removeConfiguration();
            }
        });
        
        display.okButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                saveAndClose();
            }
        });
        
        display.cancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
        
        display.datasourceListGrid().addSelectionHandler(new SelectionHandler<DataSourceOptions>() {
            @Override
            public void onSelection(SelectionEvent<DataSourceOptions> event) {
                DataSourceOptions ds = event.getSelectedItem();
                if (ds != null) {
                    display.editDatasource(ds);
                    display.setRemoveButtonEnabled(true);
                } else {
                    display.setRemoveButtonEnabled(false);
                }
            }
        });
        
        display.setDatasourceChangedHandler(new DatasourceChangedHandler() {
            @Override
            public void onDatasourceChanged(DataSourceOptions datasource) {
                display.setOkButtonEnabled(true);                
            }

            @Override
            public void onDatasourceNameChanged(DataSourceOptions datasource) {
                display.setOkButtonEnabled(true);
                display.updateDatasourceListGrid();
            }
        });
    }
        
    private void newConfiguration() {
        DatasourceClientService.getInstance().newConfiguration(vfsInfo.getId(), project.getId(), new AsyncCallback<DataSourceOptions>() {
            @Override
            public void onFailure(Throwable caught) {
                IDE.fireEvent(new ExceptionThrownEvent(caught, "Error creating Datasource"));
            }

            @Override
            public void onSuccess(final DataSourceOptions datasource) {
                Dialogs.getInstance().askForValue("IDE", "Datasource Name:", datasource.getName(), new StringValueReceivedHandler() {
                    @Override
                    public void stringValueReceived(String value) {
                        if (value == null || value.trim().isEmpty()) {
                            return;
                        }
                        
                        datasource.setName(value);
                        datasourceList.add(datasource);
                        display.datasourceListGrid().setValue(datasourceList);
                        display.datasourceListGrid().selectItem(datasource);
                        display.setOkButtonEnabled(true);
                    }
                });
            }
        });
    }
    
    private void removeConfiguration() {
        DataSourceOptions selectedDatasource = display.getSelectedDatasource();
        if (selectedDatasource == null) {
            return;
        }
        
        datasourceList.remove(selectedDatasource);
        display.datasourceListGrid().setValue(datasourceList);
        display.setRemoveButtonEnabled(false);
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (datasourceList != null && !datasourceList.isEmpty()) {
                    display.datasourceListGrid().selectItem(datasourceList.get(0));
                } else {
                    display.editDatasource(null);
                }
            }
        });
    }
    
    private void saveAndClose() {
        DatasourceClientService.getInstance().saveAll(vfsInfo.getId(), project.getId(), datasourceList, new AsyncCallback<List<DataSourceOptions>>() {
            @Override
            public void onFailure(Throwable caught) {
                IDE.fireEvent(new ExceptionThrownEvent(caught, "Error saving Datasource configuration"));
            }

            @Override
            public void onSuccess(List<DataSourceOptions> result) {
                IDE.fireEvent(new RefreshBrowserEvent(project));
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }
    
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
    }

    @Override
    public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event) {
        ideConfiguration = event.getConfiguration();
        new DatasourceClientService(ideConfiguration.getContext());
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = (IDEProject)event.getProject();
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        project = null;
    }

    @Override
    public void onTreeRefreshed(TreeRefreshedEvent event) {
    }

}
