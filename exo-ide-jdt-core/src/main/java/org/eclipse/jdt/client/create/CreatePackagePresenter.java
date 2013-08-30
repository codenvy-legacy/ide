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
package org.eclipse.jdt.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

import org.eclipse.jdt.client.core.JavaConventions;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.event.CreatePackageEvent;
import org.eclipse.jdt.client.event.CreatePackageHandler;
import org.eclipse.jdt.client.event.PackageCreatedEvent;
import org.eclipse.jdt.client.packaging.model.JavaProject;
import org.eclipse.jdt.client.packaging.model.SourceDirectory;
import org.eclipse.jdt.client.runtime.IStatus;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CreatePackagePresenter implements ViewClosedHandler, ItemsSelectedHandler, CreatePackageHandler {

    interface Display extends IsView {

        HasValue<String> getPackageNameField();

        void focusInPackageNameField();

        HasClickHandlers getOkButton();

        HasClickHandlers getCancelButton();

        HasText getErrorLabel();

        HasText getWarningLabel();

        void setOkButtonEnabled(boolean enabled);
    }

    private VirtualFileSystem vfs;

    private Display display;

    private Item selectedItem;

    /**
     * @param eventBus
     * @param vfs
     */
    public CreatePackagePresenter(VirtualFileSystem vfs) {
        this.vfs = vfs;
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(CreatePackageEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() == 1) {
            selectedItem = event.getSelectedItems().get(0);
        } else {
            selectedItem = null;
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.eclipse.jdt.client.event.CreatePackageHandler#onCreatePackage(org.eclipse.jdt.client.event.CreatePackageEvent) */
    @Override
    public void onCreatePackage(CreatePackageEvent event) {
        if (selectedItem == null || display != null) {
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();
    }

    /**
     *
     */
    private void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doCreate();
            }
        });

        display.getPackageNameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                validate(event.getValue());
            }
        });

        ((HasKeyPressHandlers)display.getPackageNameField()).addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    doCreate();
                }
            }
        });

        showSelectedPackageName();
        display.focusInPackageNameField();
        display.setOkButtonEnabled(false);
    }

    private void showSelectedPackageName() {
        if (selectedItem == null) {
            return;
        }

        ProjectModel project = ((ItemContext)selectedItem).getProject();
        if (!(project instanceof JavaProject)) {
            return;
        }

        JavaProject javaProject = (JavaProject)project;
        SourceDirectory sourceDirectory = null;
        for (SourceDirectory sd : javaProject.getSourceDirectories()) {
            if (selectedItem.getPath().startsWith(sd.getPath())) {
                sourceDirectory = sd;
                break;
            }
        }

        if (sourceDirectory == null) {
            return;
        }

        String packageName = selectedItem.getPath().substring(sourceDirectory.getPath().length());
        packageName = packageName.replaceAll("/", "\\.");
        if (packageName.startsWith(".")) {
            packageName = packageName.substring(1);
        }

        display.getPackageNameField().setValue(packageName);
    }

    /** @param value */
    private void validate(String value) {
        IStatus status =
                JavaConventions.validatePackageName(value, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
                                                    JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
        switch (status.getSeverity()) {
            case IStatus.WARNING:
                display.getWarningLabel().setText(status.getMessage());
                display.getErrorLabel().setText("");
                display.setOkButtonEnabled(true);
                break;
            case IStatus.OK:
                display.getErrorLabel().setText("");
                display.getWarningLabel().setText("");
                display.setOkButtonEnabled(true);
                break;

            default:
                display.setOkButtonEnabled(false);
                display.getWarningLabel().setText("");
                display.getErrorLabel().setText(status.getMessage());
                break;
        }
    }

    /**
     *
     */
    protected void doCreate() {
        if (display.getPackageNameField().getValue() == null || display.getPackageNameField().getValue().isEmpty()) {
            return;
        }

        JavaProject javaProject = (JavaProject)((ItemContext)selectedItem).getProject();
        SourceDirectory sourceDirectory = null;
        for (SourceDirectory sd : javaProject.getSourceDirectories()) {
            if (selectedItem.getPath().startsWith(sd.getPath())) {
                sourceDirectory = sd;
                break;
            }
        }

        if (sourceDirectory == null) {
            return;
        }

        String p = display.getPackageNameField().getValue();
        p = p.replaceAll("\\.", "/");

        final FolderModel resourceDirectoryFolder = sourceDirectory;
        final String packageName = p;
        final FolderModel newFolder = new FolderModel(packageName, resourceDirectoryFolder);

        try {
            vfs.createFolder(resourceDirectoryFolder, new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(newFolder)) {
                @Override
                protected void onSuccess(FolderModel result) {
                    IDE.getInstance().closeView(display.asView().getId());
                    IDE.fireEvent(new RefreshBrowserEvent(resourceDirectoryFolder, newFolder));
                    IDE.fireEvent(new PackageCreatedEvent(packageName, resourceDirectoryFolder));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

}
