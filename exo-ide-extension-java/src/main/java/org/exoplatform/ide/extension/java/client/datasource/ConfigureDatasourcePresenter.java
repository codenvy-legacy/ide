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
package org.exoplatform.ide.extension.java.client.datasource;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
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
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.extension.java.client.datasource.service.DatasourceClientService;
import org.exoplatform.ide.extension.java.shared.DataSourceOptions;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class ConfigureDatasourcePresenter implements ConfigureDatasourceHandler, ViewClosedHandler, 
        VfsChangedHandler, ConfigurationReceivedSuccessfullyHandler,
        ProjectOpenedHandler, ProjectClosedHandler, TreeRefreshedHandler, 
        EditorFileOpenedHandler, EditorFileClosedHandler,
        EditorActiveFileChangedHandler {

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
    
    private Map<String, FileModel> openedFiles;
    
    private List<DataSourceOptions> datasourceList;
    
    /** The map of the opened Java files identifiers and their editors. */
    private Map<String, Editor> openedEditors = new HashMap<String, Editor>();
    
    
    public ConfigureDatasourcePresenter() {
        IDE.getInstance().addControl(new ConfigureDatasourceControl());
        IDE.addHandler(ConfigureDatasourceEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);

        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
        
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        
        IDE.addHandler(TreeRefreshedEvent.TYPE, this);
        
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
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
                updateFiles = false;
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
        display.setOkButtonEnabled(true);
        
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
    
    private boolean updateFiles = false;
    
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
                updateFiles = true;
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
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
        openedEditors.put(event.getFile().getId(), event.getEditor());
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
        openedEditors.remove(event.getFile().getId());
    }
    
    @Override
    public void onTreeRefreshed(TreeRefreshedEvent event) {
        if (updateFiles) {
            updateFiles = false;
            updateFiles();
        }
    }

    private boolean needUpdate(FileModel file) {
        if (file.getPath().equals(project.getPath() + "/src/main/webapp/META-INF/context.xml")) {
            return true;
        }
        
        if (file.getPath().equals(project.getPath() + "/src/main/webapp/WEB-INF/web.xml")) {
            return true;
        }
        
        return false;
    }
    
    private void updateFiles() {
        for (Entry<String, FileModel> entry : openedFiles.entrySet()) {            
            
            final FileModel file = entry.getValue();
            
            if (!needUpdate(file)) {
                continue;
            }
            
            final Editor editor = openedEditors.get(file.getId());
            if (editor == null) {
                continue;
            }
            
            try {
                VirtualFileSystem.getInstance().getContent(
                       new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(file)) {
                           @Override
                           protected void onSuccess(final FileModel result) {
                               if (editor != activeEditor) {
                                   editorsToUpdateContent.put(editor, file.getContent());
                               } else {
                                   updateEditorContent(editor, result, result.getContent());
                               }
                           }

                           @Override
                           protected void onFailure(Throwable exception) {
                               IDE.fireEvent(new ExceptionThrownEvent(exception, "Error updating " + file.getPath()));
                           }
                       });
            } catch (RequestException exception) {
                IDE.fireEvent(new ExceptionThrownEvent(exception, "Error updating " + file.getPath()));
            }
            
        }
    }
    
    
    /** Active {@link FileModel file}. */
    private FileModel activeFile;

    /** Active editor. */
    private Editor activeEditor;

    private Map<Editor, String> editorsToUpdateContent = new HashMap<Editor, String>();
    
    
    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
        activeEditor = event.getEditor();
        
        if (activeFile == null || activeEditor == null) {
            return;
        }

        // TODO workaround of bug to updating content of editors in inactive tabs in CollabEditor
        final String content = editorsToUpdateContent.remove(activeEditor);
        if (content != null) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    updateEditorContent(activeEditor, activeFile, content);
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            IDE.fireEvent(new FileSavedEvent(activeFile, null));
                        }
                    });
                }
            });
        }

//        currentCompilationUnit = openedFilesToCompilationUnit.get(activeFile.getId());
    }
    
    /**
     * Updates {@link Editor} content.
     *
     * @param editor
     *         {@link Editor} to update
     * @param content
     *         new content
     * @param file
     *         editing {@link FileModel file}
     */
    private void updateEditorContent(final Editor editor, final FileModel file, String content) {
        
        System.out.println("ConfigureDatasourcePresenter.updateEditorContent()");
        
        final int cursorColumn = editor.getCursorColumn();
        final int cursorRow = editor.getCursorRow();
        editor.getDocument().set(file.getContent());

        // need some time to update editor's content
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                editor.setCursorPosition(cursorRow, cursorColumn);

                file.setContentChanged(false);
                IDE.fireEvent(new FileSavedEvent(file, null));
            }
        });
    }
    
    

}
