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
package org.exoplatform.ide.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.application.ApplicationStateSnapshotListener;
import org.exoplatform.ide.client.application.ControlsRegistration;
import org.exoplatform.ide.client.application.EditControlsFormatter;
import org.exoplatform.ide.client.application.IDEFileTypeRegistry;
import org.exoplatform.ide.client.application.IDEForm;
import org.exoplatform.ide.client.application.IDEPresenter;
import org.exoplatform.ide.client.application.MainMenuControlsFormatter;
import org.exoplatform.ide.client.application.NewItemControlsFormatter;
import org.exoplatform.ide.client.application.PaaSMenuControlsFormatter;
import org.exoplatform.ide.client.application.ViewControlsFormatter;
import org.exoplatform.ide.client.authentication.LoginPresenter;
import org.exoplatform.ide.client.debug.IDEDebug;
import org.exoplatform.ide.client.dialogs.AskForValueDialog;
import org.exoplatform.ide.client.dialogs.IDEDialogs;
import org.exoplatform.ide.client.documentation.DocumentationPresenter;
import org.exoplatform.ide.client.edit.TextEditModule;
import org.exoplatform.ide.client.editor.EditorController;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedEvent;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosingEvent;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileTypeRegistry;
import org.exoplatform.ide.client.framework.outline.OutlineItemCreator;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.ui.ClearFocusForm;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.impl.ViewHighlightManager;
import org.exoplatform.ide.client.framework.util.IDEAutoBeanFactory;
import org.exoplatform.ide.client.messages.IdeEditorLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeErrorsLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeLocalizationMessages;
import org.exoplatform.ide.client.messages.IdeNavigationLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeOperationLocalizationConstant;
import org.exoplatform.ide.client.messages.IdePermissionsLocalizationConstant;
import org.exoplatform.ide.client.messages.IdePreferencesLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeTemplateLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeUploadLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeVersionsLocalizationConstant;
import org.exoplatform.ide.client.navigation.NavigationModule;
import org.exoplatform.ide.client.outline.OutlineItemCreatorFactory;
import org.exoplatform.ide.client.outline.OutlinePresenter;
import org.exoplatform.ide.client.outline.OutlineStateListener;
import org.exoplatform.ide.client.output.OutputPresenter;
import org.exoplatform.ide.client.preferences.PreferencesModule;
import org.exoplatform.ide.client.preview.PreviewHTMLPresenter;
import org.exoplatform.ide.client.project.ProjectSupportingModule;
import org.exoplatform.ide.client.project.prepare.ProjectPreparePresenter;
import org.exoplatform.ide.client.properties.PropertiesPresenter;
import org.exoplatform.ide.client.selenium.SeleniumTestsHelper;
import org.exoplatform.ide.client.theme.ThemeManager;
import org.exoplatform.ide.client.websocket.WebSocketHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class IDE extends org.exoplatform.ide.client.framework.module.IDE {

    private ControlsRegistration controlsRegistration;

    private List<PaaS> registeredPaaS;

    private IDEPresenter presenter;

    /** The generator of an {@link AutoBean}. */
    public static final IDEAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(IDEAutoBeanFactory.class);

    /** Initialize constants for UI */
    public static final IdeLocalizationConstant IDE_LOCALIZATION_CONSTANT = GWT.create(IdeLocalizationConstant.class);

    public static final IdeTemplateLocalizationConstant TEMPLATE_CONSTANT = GWT
            .create(IdeTemplateLocalizationConstant.class);

    public static final IdePreferencesLocalizationConstant PREFERENCES_CONSTANT = GWT
            .create(IdePreferencesLocalizationConstant.class);

    public static final IdeVersionsLocalizationConstant VERSIONS_CONSTANT = GWT
            .create(IdeVersionsLocalizationConstant.class);

    public static final IdeUploadLocalizationConstant UPLOAD_CONSTANT = GWT.create(IdeUploadLocalizationConstant.class);

    public static final IdePermissionsLocalizationConstant PERMISSIONS_CONSTANT = GWT
            .create(IdePermissionsLocalizationConstant.class);

    public static final IdeNavigationLocalizationConstant NAVIGATION_CONSTANT = GWT
            .create(IdeNavigationLocalizationConstant.class);

    public static final IdeEditorLocalizationConstant EDITOR_CONSTANT = GWT.create(IdeEditorLocalizationConstant.class);

    public static final IdeErrorsLocalizationConstant ERRORS_CONSTANT = GWT.create(IdeErrorsLocalizationConstant.class);

    public static final IdeOperationLocalizationConstant OPERATION_CONSTANT = GWT
            .create(IdeOperationLocalizationConstant.class);

    public static final IdeLocalizationMessages IDE_LOCALIZATION_MESSAGES = GWT.create(IdeLocalizationMessages.class);

    private FileTypeRegistry fileTypeRegistry = new IDEFileTypeRegistry();
    private UserSession userSession;

    public IDE() {
        // Remember browser's window.alert(...) function
        Alert.init();

        // Create the list of available icons.
        IDEIconSet.init();
        addWindowCloseHandler();
        addWindowClosingHandler();

        new IDEDialogs();
        new AskForValueDialog();

      /*
       * Initialize SeleniumTestsHelper. It creates HTML DIV elements and logs to them IDE current application state. ( opened
       * files, active file, current project, etc. )
       */
        new SeleniumTestsHelper();

        new ExceptionThrownEventHandler();

        // new CookieManager(eventBus);
        // new HistoryManager(eventBus, context); // commented to fix the bug with javascript error in IE8 (WBT-321)

        controlsRegistration = new ControlsRegistration();
        controlsRegistration.addControlsFormatter(new NewItemControlsFormatter());
        controlsRegistration.addControlsFormatter(new ViewControlsFormatter());
        controlsRegistration.addControlsFormatter(new EditControlsFormatter());

        IDEForm ideForm = new IDEForm();
        presenter = new IDEPresenter(ideForm, controlsRegistration);

        new EditorController();

        new LoginPresenter();

        new ViewHighlightManager(IDE.eventBus());

        new ApplicationStateSnapshotListener();

        // MODULES INITIALIZATION
        new NavigationModule();
        new ProjectSupportingModule();
        new TextEditModule();

        new PropertiesPresenter();
        new OutputPresenter();
        
        new OutlinePresenter();
        new OutlineStateListener();
        
        new PreviewHTMLPresenter();
        new DocumentationPresenter();

        new PreferencesModule();

        new WebSocketHandler();
        userSession = new UserSession();

        new IDEDebug();
        new ProjectPreparePresenter();

        new ThemeManager();

        // initialize extensions
        for (Extension ext : getExtensions()) {
            ext.initialize();
        }
        controlsRegistration.addControlsFormatter(new MainMenuControlsFormatter());
        controlsRegistration.addControlsFormatter(new PaaSMenuControlsFormatter());
        controlsRegistration.formatControls();


      /*
       * Find a method to disable selection of text and elements on the page ( exclude text fields ).
       */
        // TODO disableTextSelectInternal(RootLayoutPanel.get().getElement(), true);
    }

    /**
     * @see org.exoplatform.ide.client.framework.module.IDE#addControl(org.exoplatform.gwtframework.ui.client.command.Control,
     *      org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget)
     */
    @Override
    public void addControl(Control<?> control, Docking docking) {
        controlsRegistration.addControl(control, docking);
    }

    /** @see org.exoplatform.ide.client.framework.module.IDE#addControl(org.exoplatform.gwtframework.ui.client.command.Control) */
    @Override
    public void addControl(Control<?> control) {
        controlsRegistration.addControl(control, Docking.NONE);
    }

    /** @see org.exoplatform.ide.client.framework.module.IDE#closeView(java.lang.String) */
    @Override
    public void closeView(String viewId) {
        presenter.closeView(viewId);
    }

    @Override
    public void openView(View view) {
        ClearFocusForm.getInstance().clearFocus();
        presenter.openView(view);
    }

    /**
     * @see org.exoplatform.ide.client.framework.module.IDE#addOutlineItemCreator(java.lang.String,
     *      org.exoplatform.ide.client.framework.outline.ui.OutlineItemCreator)
     */
    @Override
    public void addOutlineItemCreator(String mimeType, OutlineItemCreator outlineItemCreator) {
        OutlineItemCreatorFactory.addOutlineItemCreator(mimeType, outlineItemCreator);
    }

    /** @see org.exoplatform.ide.client.framework.module.IDE#getOutlineItemCreator(java.lang.String) */
    @Override
    public OutlineItemCreator getOutlineItemCreator(String mimeType) {
        return OutlineItemCreatorFactory.getOutlineItemCreator(mimeType);
    }

    /** @see org.exoplatform.ide.client.framework.module.IDE#getControls() */
    @Override
    public List<Control> getControls() {
        return controlsRegistration.getRegisteredControls();
    }

    /**
     * @see org.exoplatform.ide.client.framework.module.IDE#addControlsFormatter(org.exoplatform.ide.client.framework.control
     *      .ControlsFormatter)
     */
    @Override
    public void addControlsFormatter(ControlsFormatter controlsFormatter) {
        controlsRegistration.addControlsFormatter(controlsFormatter);
    }

    public void addWindowCloseHandler() {
        Window.addCloseHandler(new CloseHandler<Window>() {
            @Override
            public void onClose(CloseEvent<Window> event) {
                userSession.close();
                fireEvent(new ApplicationClosedEvent());
            }
        });
    }

    public void addWindowClosingHandler() {
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            @Override
            public void onWindowClosing(Window.ClosingEvent event) {
                fireEvent(new ApplicationClosingEvent(event));
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.module.IDE#getPaaSes() */
    @Override
    public List<PaaS> getPaaSes() {
        if (registeredPaaS == null) {
            registeredPaaS = new ArrayList<PaaS>();
        }
        return registeredPaaS;
    }

    /** @see org.exoplatform.ide.client.framework.module.IDE#registerPaaS(org.exoplatform.ide.client.framework.paas.recent.PaaS) */
    @Override
    public void registerPaaS(PaaS paas) {
        getPaaSes().add(paas);
    }

    @Override
    public FileTypeRegistry getFileTypeRegistry() {
        return fileTypeRegistry;
    }
}
