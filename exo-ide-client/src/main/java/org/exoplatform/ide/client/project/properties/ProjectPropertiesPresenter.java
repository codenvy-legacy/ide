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

package org.exoplatform.ide.client.project.properties;

import com.codenvy.ide.commons.shared.ProjectType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.project.api.PropertiesChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectPropertiesPresenter implements ShowProjectPropertiesHandler, ProjectOpenedHandler,
                                                   ProjectClosedHandler, ViewClosedHandler {

    public interface Display extends IsView {

        ListGridItem<Property> getPropertiesListGrid();

        // HasClickHandlers getAddButton();

        HasClickHandlers getEditButton();

        void setEditButtonEnabled(boolean enabled);

        HasClickHandlers getDeleteButton();

        void setDeleteButtonEnabled(boolean enabled);

        HasClickHandlers getOkButton();

        void setOkButtonEnabled(boolean enabled);

        HasClickHandlers getCancelButton();

    }

    private Display display;

    private ProjectModel currentProject;

    private Property selectedProperty;

    private EditPropertyPresenter editPropertyPresenter = new EditPropertyPresenter();

    private EditPropertyFixedValuesPresenter editPropertyFixedValuePresenter = new EditPropertyFixedValuesPresenter();

    public ProjectPropertiesPresenter() {
        IDE.getInstance().addControl(new ShowProjectPropertiesControl());

        IDE.addHandler(ShowProjectPropertiesEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    @Override
    public void onShowProjectProperties(ShowProjectPropertiesEvent event) {
        if (display != null || currentProject == null) {
            return;
        }

        loadProperties();
    }

    private void loadProperties() {
        try {
            String projectId = currentProject.getId();

            VirtualFileSystem.getInstance()
                             .getItemById(projectId,
                                          new AsyncRequestCallback<ItemWrapper>(
                                                  new ItemUnmarshaller(new ItemWrapper(new FileModel()))) {
                                              @Override
                                              protected void onSuccess(ItemWrapper result) {
                                                  if (!(result.getItem() instanceof ProjectModel)) {
                                                      Dialogs.getInstance().showError("Item " + result.getItem().getPath()
                                                                                      + " is not a project.");
                                                      return;
                                                  }
                                                  currentProject.setLinks(result.getItem().getLinks());
                                                  currentProject.getProperties().clear();
                                                  currentProject.getProperties().addAll(result.getItem().getProperties());

                                                  createDisplay();
                                              }

                                              @Override
                                              protected void onFailure(Throwable exception) {
                                                  IDE.fireEvent(new ExceptionThrownEvent(exception));
                                              }
                                          });

        } catch (Exception e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void createDisplay() {
        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();
        refreshProperties();
    }

    @SuppressWarnings("unchecked")
    private void bindDisplay() {
        display.setEditButtonEnabled(false);
        display.setDeleteButtonEnabled(false);
        display.setOkButtonEnabled(false);

        display.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                saveAndClose();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getPropertiesListGrid().addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                editSelectedProperty();
            }
        });

        display.getPropertiesListGrid().addSelectionHandler(new SelectionHandler() {
            @Override
            public void onSelection(SelectionEvent event) {
                onPropertySelected((Property)event.getSelectedItem());
            }
        });

        display.getEditButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                editSelectedProperty();
            }
        });

        display.getDeleteButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                deleteSelectedProperty();
            }
        });
    }

    private void refreshProperties() {
        List<Property> propertyList = new ArrayList<Property>();
        for (Property property : currentProject.getProperties()) {
            if (property.getValue() == null || property.getValue().isEmpty()
                || ProjectProperties.JREBEL_COUNT.value().equals(property.getName())) {
                continue;
            }

            propertyList.add(property);
        }

        display.getPropertiesListGrid().setValue(propertyList);
        display.getPropertiesListGrid().selectItem(selectedProperty);
    }

    private void editSelectedProperty() {
        if (selectedProperty.getName().equals(ProjectProperties.TYPE.value())) {
            List<String> projectTypes = new ArrayList<String>();
            for (ProjectType typ : ProjectType.values()) {
                projectTypes.add(typ.toString());
            }
            editPropertyFixedValuePresenter.editProperty(selectedProperty, currentProject.getProperties(), projectTypes,
                                                         propertyEditCompleteHandler);

        } else {
            editPropertyPresenter.editProperty(selectedProperty, currentProject.getProperties(), propertyEditCompleteHandler);
        }
    }

    private EditCompleteHandler propertyEditCompleteHandler = new EditCompleteHandler() {
        @Override
        public void onEditComplete() {
            display.setOkButtonEnabled(true);
            refreshProperties();
        }
    };

    private void deleteSelectedProperty() {
        if (selectedProperty.getName().equals(ProjectProperties.TYPE.value())
            || selectedProperty.getName().equals(ProjectProperties.MIME_TYPE.value())) {
            Dialogs.getInstance().showInfo("This property is required for your project and cannot be deleted.");
            return;
        }
        String name = PropertyUtil.getHumanReadableName(selectedProperty.getName());
        Dialogs.getInstance().ask("IDE", "Delete property <b>" + name + "</b>?", new BooleanValueReceivedHandler() {
            @Override
            public void booleanValueReceived(Boolean value) {
                if (value != null && value.booleanValue()) {
                    selectedProperty.setValue(null);
                    selectedProperty = null;

                    display.setEditButtonEnabled(false);
                    display.setDeleteButtonEnabled(false);
                    display.setOkButtonEnabled(true);
                    refreshProperties();
                }
            }
        });
    }

    private void onPropertySelected(Property property) {
        selectedProperty = property;
        if (!IDE.user.getRoles().contains("developer") && !IDE.user.getRoles().contains("admin")) {
            display.setEditButtonEnabled(false);
            display.setDeleteButtonEnabled(false);
            return;
        }

        if (selectedProperty.getName().equals(ProjectProperties.MIME_TYPE.value())
            || (selectedProperty.getName().equals(ProjectProperties.JREBEL.value()) && IDE.user.isTemporary())) {
            display.setEditButtonEnabled(false);
        } else {
            display.setEditButtonEnabled(true);
        }
        display.setDeleteButtonEnabled(true);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        currentProject = event.getProject();
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        currentProject = null;
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
            selectedProperty = null;
        }
    }

    private void saveAndClose() {
        try {
            VirtualFileSystem.getInstance().updateItem(currentProject, null, new AsyncRequestCallback<ItemWrapper>() {
                @Override
                protected void onSuccess(ItemWrapper result) {
                    IDE.getInstance().closeView(display.asView().getId());
                    IDE.fireEvent(new PropertiesChangedEvent(currentProject));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                    IDE.fireEvent(new PropertiesChangedEvent(currentProject));
                }
            });

        } catch (Exception e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
            IDE.fireEvent(new PropertiesChangedEvent(currentProject));

        }
    }
}
