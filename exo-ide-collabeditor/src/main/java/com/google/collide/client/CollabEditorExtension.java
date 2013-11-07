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
package com.google.collide.client;

import elemental.dom.Node;

import com.codenvy.ide.client.util.ClientImplementationsInjector;
import com.codenvy.ide.client.util.Elements;
import com.codenvy.ide.notification.NotificationManager;
import com.codenvy.ide.users.UsersModel;
import com.google.collide.client.bootstrap.BootstrapSession;
import com.google.collide.client.collaboration.CollaborationManager;
import com.google.collide.client.collaboration.DocOpsSavedNotifier;
import com.google.collide.client.collaboration.IncomingDocOpDemultiplexer;
import com.google.collide.client.collaboration.NotificationController;
import com.google.collide.client.disable.DisableEnableCollaborationControl;
import com.google.collide.client.document.DocumentManager;
import com.google.collide.client.document.DocumentManagerCleaner;
import com.google.collide.client.status.StatusPresenter;
import com.google.collide.codemirror2.CodeMirror2;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.DOM;

import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.events.ReplyHandler;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  7/18/12 evgen $
 */
public class CollabEditorExtension extends Extension implements ConnectionOpenedHandler {

    private static CollabEditorExtension instance;

    private AppContext context;

    private DocumentManager documentManager;

    public static final CollabEditorLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(
            CollabEditorLocalizationConstant.class);

    private CollaborationManager collaborationManager;

    private UsersModel usersModel;
    private DisableEnableCollaborationControl control;
    private DocumentManagerCleaner documentManagerCleaner;

    public static CollabEditorExtension get() {
        return instance;
    }

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        instance = this;
        ClientImplementationsInjector.inject();

        context = AppContext.create();
        documentManager = DocumentManager.create(context);

        IDE.messageBus().setOnOpenHandler(this);
        control = new DisableEnableCollaborationControl(context.getResources());
        IDE.getInstance().addControl(control);
        documentManagerCleaner = new DocumentManagerCleaner();
    }

    public AppContext getContext() {
        return context;
    }

    public DocumentManager getManager() {
        return documentManager;
    }

    public CollaborationManager getCollaborationManager() {
        return collaborationManager;
    }

    public UsersModel getUsersModel() {
        return usersModel;
    }

    /**
     *
     */
    private void init() {
        com.google.collide.client.Resources resources = context.getResources();
        com.google.gwt.user.client.Element div = DOM.createDiv();
        div.setId(AppContext.GWT_ROOT);
        Elements.getBody().appendChild((Node)div);
//
//    // TODO: Figure out why when we use the + operator to concat,
//    // these Strings don't at compile time converge to a single String literal.
//    // In theory they should. For now we use a StringBuilder.
//
//    // Make sure you call getText() on your CssResource!
        StringBuilder styleBuilder = new StringBuilder();
        styleBuilder.append(resources.appCss().getText());
        styleBuilder.append(resources.baseCss().getText());
//    styleBuilder.append(resources.workspaceHeaderCss().getText());
//    styleBuilder.append(resources.editorToolBarCss().getText());
        styleBuilder.append(resources.defaultSimpleListCss().getText());
//    styleBuilder.append(resources.workspaceShellCss().getText());
        styleBuilder.append(resources.workspaceEditorCss().getText());
        styleBuilder.append(resources.workspaceEditorBufferCss().getText());
        styleBuilder.append(resources.workspaceEditorCursorCss().getText());
//    styleBuilder.append(resources.workspaceEditorConsoleViewCss().getText());
//    styleBuilder.append(resources.workspaceEditorDebuggingModelCss().getText());
//    styleBuilder.append(resources.workspaceEditorDebuggingSidebarCss().getText());
//    styleBuilder.append(resources.workspaceEditorDebuggingSidebarBreakpointsPaneCss().getText());
//    styleBuilder.append(resources.workspaceEditorDebuggingSidebarCallStackPaneCss().getText());
//    styleBuilder.append(resources.workspaceEditorDebuggingSidebarControlsPaneCss().getText());
//    styleBuilder.append(resources.workspaceEditorDebuggingSidebarHeaderCss().getText());
//    styleBuilder.append(resources.workspaceEditorDebuggingSidebarNoApiPaneCss().getText());
//    styleBuilder.append(resources.workspaceEditorDebuggingSidebarScopeVariablesPaneCss().getText());
//    styleBuilder.append(resources.workspaceEditorDomInspectorCss().getText());
//    styleBuilder.append(
//        resources.workspaceEditorDebuggingSidebarWatchExpressionsPaneCss().getText());
//    styleBuilder.append(resources.remoteObjectTreeCss().getText());
//    styleBuilder.append(resources.remoteObjectNodeRendererCss().getText());
//    styleBuilder.append(resources.editorDiffContainerCss().getText());
//    styleBuilder.append(resources.evaluationPopupControllerCss().getText());
//    styleBuilder.append(resources.goToDefinitionCss().getText());
//    styleBuilder.append(resources.treeCss().getText());
//    styleBuilder.append(resources.workspaceNavigationCss().getText());
//    styleBuilder.append(resources.workspaceNavigationFileTreeSectionCss().getText());
//    styleBuilder.append(resources.workspaceNavigationShareWorkspacePaneCss().getText());
//    styleBuilder.append(resources.workspaceNavigationToolBarCss().getText());
//    styleBuilder.append(resources.workspaceNavigationFileTreeNodeRendererCss().getText());
//    styleBuilder.append(resources.workspaceNavigationOutlineNodeRendererCss().getText());
        styleBuilder.append(resources.workspaceNavigationParticipantListCss().getText());
//    styleBuilder.append(resources.searchContainerCss().getText());
        styleBuilder.append(resources.statusPresenterCss().getText());
//    styleBuilder.append(resources.noFileSelectedPanelCss().getText());
//    styleBuilder.append(resources.diffRendererCss().getText());
//    styleBuilder.append(resources.deltaInfoBarCss().getText());
//    styleBuilder.append(resources.codePerspectiveCss().getText());
//    styleBuilder.append(resources.unauthorizedUserCss().getText());
        styleBuilder.append(resources.syntaxHighlighterRendererCss().getText());
        styleBuilder.append(resources.lineNumberRendererCss().getText());
//    styleBuilder.append(resources.uneditableDisplayCss().getText());
        styleBuilder.append(resources.editorSelectionLineRendererCss().getText());
//    styleBuilder.append(resources.fileHistoryCss().getText());
//    styleBuilder.append(resources.timelineCss().getText());
//    styleBuilder.append(resources.timelineNodeCss().getText());
        styleBuilder.append(resources.popupCss().getText());
        styleBuilder.append(resources.tooltipCss().getText());
//    styleBuilder.append(resources.sliderCss().getText());
        styleBuilder.append(resources.editableContentAreaCss().getText());
//    styleBuilder.append(resources.workspaceLocationBreadcrumbsCss().getText());
//    styleBuilder.append(resources.awesomeBoxCss().getText());
//    styleBuilder.append(resources.awesomeBoxSectionCss().getText());
//    styleBuilder.append(resources.centerPanelCss().getText());
        styleBuilder.append(resources.autocompleteComponentCss().getText());
//    styleBuilder.append(resources.runButtonTargetPopupCss().getText());
//    styleBuilder.append(resources.popupBlockedInstructionalPopupCss().getText());
//    styleBuilder.append(resources.dropdownWidgetsCss().getText());
        styleBuilder.append(resources.parenMatchHighlighterCss().getText());
//    styleBuilder.append(resources.awesomeBoxHostCss().getText());
//    styleBuilder.append(resources.awesomeBoxComponentCss().getText());
        styleBuilder.append(resources.coachmarkCss().getText());
//    styleBuilder.append(resources.sidebarListCss().getText());
//
//    /*
//     * workspaceNavigationSectionCss, animationController, and
//     * resizeControllerCss must come last because they overwrite the CSS
//     * properties from previous CSS rules.
//     */
//    styleBuilder.append(resources.workspaceNavigationSectionCss().getText());
//    styleBuilder.append(resources.resizeControllerCss().getText());
//
        styleBuilder.append(resources.searchMatchRendererCss().getText());
        styleBuilder.append(resources.notificationCss().getText());
        StyleInjector.inject(styleBuilder.toString());
        Elements.injectJs(CodeMirror2.getJs());

        // Status Presenter
        StatusPresenter statusPresenter = StatusPresenter.create(context.getResources());
        Elements.getBody().appendChild(statusPresenter.getView().getElement());
        context.getStatusManager().setHandler(statusPresenter);

    }

    @Override
    public void onConnectionOpened() {
        if (collaborationManager != null) {
            return;
        }
        init();
        IDE.messageBus().send(Utils.getWorkspaceName() + "/collab_editor/participants/add", "{}", new ReplyHandler() {
            @Override
            public void onReply(String message) {
                JSONObject object = JSONParser.parseLenient(message).isObject();
                BootstrapSession.getBootstrapSession().setUserId(object.get("userId").isString().stringValue());
                BootstrapSession.getBootstrapSession().setActiveClientId(object.get("activeClientId").isString().stringValue());
                context.initializeCollaboration();
                //                  ParticipantModel participantModel = ParticipantModel.create(context.getFrontendApi(),
                // context.getMessageFilter());
                IncomingDocOpDemultiplexer docOpRecipient = IncomingDocOpDemultiplexer.create(context.getMessageFilter());
                usersModel = new UsersModel(context.getFrontendApi(), context.getMessageFilter());
                collaborationManager = CollaborationManager.create(context, documentManager, docOpRecipient, usersModel, control);
                new NotificationController(NotificationManager.get(), collaborationManager, context.getMessageFilter(),
                                           usersModel, IDE.eventBus(), context.getResources().baseCss());

                DocOpsSavedNotifier docOpSavedNotifier = new DocOpsSavedNotifier(documentManager, collaborationManager);
                //                     bus.close();
            }
        });
    }
}
