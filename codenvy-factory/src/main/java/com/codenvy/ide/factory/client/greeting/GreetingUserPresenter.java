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
package com.codenvy.ide.factory.client.greeting;

import com.codenvy.ide.factory.client.FactoryClientBundle;
import com.codenvy.ide.factory.client.copy.CopyProjectEvent;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.command.ui.AddToolbarItemsEvent;
import org.exoplatform.gwtframework.ui.client.command.ui.UniButton;
import org.exoplatform.gwtframework.ui.client.command.ui.UniButton.Size;
import org.exoplatform.gwtframework.ui.client.command.ui.UniButton.Type;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.configuration.IDEInitialConfiguration;
import org.exoplatform.ide.client.framework.configuration.InitialConfigurationReceivedEvent;
import org.exoplatform.ide.client.framework.configuration.InitialConfigurationReceivedHandler;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter that show user in right panel greeting message.
 * @author Vitaliy Guluy
 */
public class GreetingUserPresenter implements InitialConfigurationReceivedHandler, ProjectOpenedHandler, IDELoadCompleteHandler {

    public interface GreetingDisplay extends IsView {
    }

    /**
     * Initial IDE configuration
     */
    private IDEInitialConfiguration initialConfiguration;

    /**
     * Current opened project
     */
    private ProjectModel project;

    private boolean welcomeOnceAppeared = false;

    /** Creates presenter instance */
    public GreetingUserPresenter() {
        IDE.addHandler(InitialConfigurationReceivedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(IDELoadCompleteEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onInitialConfigurationReceived(InitialConfigurationReceivedEvent event) {
        initialConfiguration = event.getInitialConfiguration();
    }

    /** Returns URL of the HTML file which should be displayed in Greeting view. */
    //@formatter:off
    public static native String getGreetingPanelContentURL(String key) /*-{
        try {
            if ($wnd.GREETING_PANE_CONTENT[key] && typeof $wnd.GREETING_PANE_CONTENT[key] != 'undefined') {
                return $wnd.GREETING_PANE_CONTENT[key];
            }
        } catch (err) {
            console.log(err.message);
        }

        return null;
    }-*/;
    //@formatter:on

    /** Adds "Create account" and "Login" buttons on toolbar. */
    private void addButtonsForNoneAuthenticatedUser() {
        UniButton createAccountButton = new UniButton("Create free account", Type.SUCCESS, Size.SMALL);
        IDE.fireEvent(new AddToolbarItemsEvent(createAccountButton, true));
        createAccountButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new CopyProjectEvent(true));
            }
        });

        UniButton loginButton = new UniButton("Login", Type.PRIMARY, Size.SMALL);
        IDE.fireEvent(new AddToolbarItemsEvent(loginButton, true));
        loginButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                loginToIDE();
            }
        });
    }

    //@formatter:off
    private native void loginToIDE() /*-{
            if ($wnd.location.search !== "") {
                $wnd.location.search += "&login";
            } else {
                $wnd.location.search += "?login";
            }
    }-*/;
    //@formatter:on

    /** Adds "Copy to my workspace" button on toolbar. */
    private void addCopyToMyWorkspaceButton() {
        UniButton copyToMyWorkspaceButton =
                new UniButton("Copy to my workspace", new Image(FactoryClientBundle.INSTANCE.copyToWorkspaceIcon()), Type.PRIMARY,
                              Size.SMALL);
        IDE.fireEvent(new AddToolbarItemsEvent(copyToMyWorkspaceButton, true));
        copyToMyWorkspaceButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new CopyProjectEvent());
            }
        });
    }

    /** Returns URL to the greeting page. */
    private void getGreetingPageURL(boolean workspacePrivate) {
        String key;

        boolean workspaceTemporary =
                initialConfiguration.getCurrentWorkspace() != null && initialConfiguration.getCurrentWorkspace().isTemporary();

        if (IDE.user.isTemporary() || "__anonim".equals(IDE.user.getUserId())) {
            if (workspaceTemporary) {
                key = workspacePrivate ? "anonymous-workspace-temporary-private" : "anonymous-workspace-temporary";
                addButtonsForNoneAuthenticatedUser();
            } else {
                key = "anonymous";
            }
        } else {
            if (workspaceTemporary) {
                key = workspacePrivate ? "authenticated-workspace-temporary-private" : "authenticated-workspace-temporary";
                addCopyToMyWorkspaceButton();
            } else {
                key = "authenticated";
            }
        }

        if (project != null) {
            String projectType = project.getProjectType().toLowerCase();
            while (projectType.contains("/")) {
                projectType = projectType.replace('/', '-');
            }

            while (projectType.contains(" ")) {
                projectType = projectType.replace(' ', '-');
            }

            String url = getGreetingPanelContentURL(key + "-" + projectType);
            if (url != null && !url.trim().isEmpty()) {
                createGreetingFrame(url);
                return;
            }
        }

        final String greetingContentURL = getGreetingPanelContentURL(key);
        if (greetingContentURL != null && !greetingContentURL.trim().isEmpty()) {
            createGreetingFrame(greetingContentURL);
        }
    }

    //TODO when Workspace API will be ready this method should be replaced with calling Workspace API to get Workspace information.
    private void getWorkspaceAccessibility() {
        try {
            AsyncRequestCallback<StringBuilder> callback =
                    new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                        @Override
                        protected void onSuccess(StringBuilder result) {
                            getGreetingPageURL(Boolean.valueOf(result.toString()));
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            getGreetingPageURL(false); //anyway continue work
                        }
                    };

            AsyncRequest.build(RequestBuilder.GET, Utils.getRestContext() + Utils.getWorkspaceName() + "/workspace/private").send(callback);
        } catch (RequestException e) {
            getGreetingPageURL(false); //anyway continue work
        }
    }

    private void createGreetingFrame(final String greetingPageURL) {
        welcomeOnceAppeared = true;

        final Frame frame = new Frame(greetingPageURL);
        Style style = frame.getElement().getStyle();

        style.setPosition(Position.ABSOLUTE);
        style.setLeft(-1000, Unit.PX);
        style.setTop(-1000, Unit.PX);
        style.setWidth(1, Unit.PX);
        style.setHeight(1, Unit.PX);
        style.setOverflow(Overflow.HIDDEN);

        frame.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                fetchGreetingParamsFromIFrame(IFrameElement.as(frame.getElement()), greetingPageURL);
                frame.removeFromParent();
            }
        });

        RootPanel.get().add(frame);
    }

    private Timer scheduleGreetingTimer = new Timer() {
        @Override
        public void run() {
            getWorkspaceAccessibility();
        }
    };

    private void scheduleLoadGreeting() {
        if (welcomeOnceAppeared) {
            return;
        }

        scheduleGreetingTimer.cancel();
        scheduleGreetingTimer.schedule(1500);
    }

    // @formatter:off
    private native void fetchGreetingParamsFromIFrame(Element element, String greetingContentURL)/*-{
        try {
            var frameDocument = element.contentWindow.document;
            var head = frameDocument.getElementsByTagName('head')[0];

            var title = null;
            var icon = null;
            var notification = null;

            var children = head.childNodes;
            for (var i = 0; i < children.length; i++){
                var child = children[i];

                if ( child.nodeType != 1) {
                    continue;
                }

                if ( "title" == child.nodeName.toLowerCase() ) {
                    title = child.innerHTML;
                    continue;
                }

                if ( "meta" == child.nodeName.toLowerCase() &&
                        child.getAttribute("name") != null &&
                        "icon" == child.getAttribute("name").toLowerCase() ) {
                    icon = child.getAttribute("url");
                    continue;
                }

                if ( "meta" == child.nodeName.toLowerCase() &&
                        child.getAttribute("name") != null &&
                        "notification" == child.getAttribute("name").toLowerCase()) {
                    notification = child.getAttribute("content");
                }
            }

            this.@com.codenvy.ide.factory.client.greeting.GreetingUserPresenter::showGreeting(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(title, icon, greetingContentURL, notification);
        } catch (e) {
            this.@com.codenvy.ide.factory.client.greeting.GreetingUserPresenter::loadGreetingError(Ljava/lang/String;)(e.message);
        }
    }-*/;
    // @formatter:on


    private void showGreeting(final String title, final String iconURL, final String greetingContentURL, final String notification) {
        Image icon = null;
        if (iconURL != null) {
            icon = new Image(iconURL);
            icon.setWidth("16px");
            icon.setHeight("16px");
        }

        GreetingDisplay display = new GreetingView(title, icon, greetingContentURL);
        IDE.getInstance().openView(display.asView());

        if (notification != null) {
            new Timer() {
                @Override
                public void run() {
                    new TooltipHint(notification);
                }
            }.schedule(1000);
        }
    }

    private void loadGreetingError(String message) {
        Dialogs.getInstance().showError("IDE", "Could not load greeting page");
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
        scheduleLoadGreeting();
    }

    @Override
    public void onIDELoadComplete(IDELoadCompleteEvent event) {
        scheduleLoadGreeting();
    }
}
