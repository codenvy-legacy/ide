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
import com.codenvy.ide.factory.client.copy.CopySpec10;
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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

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
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class GreetingUserPresenter implements 
        InitialConfigurationReceivedHandler, ProjectOpenedHandler, IDELoadCompleteHandler {

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

    /**
     * Creates presenter instance
     */
    public GreetingUserPresenter() {
        IDE.addHandler(InitialConfigurationReceivedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(IDELoadCompleteEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.configuration.InitialConfigurationReceivedHandler#onInitialConfigurationReceived(org.exoplatform.ide.client.framework.configuration.InitialConfigurationReceivedEvent)
     */
    @Override
    public void onInitialConfigurationReceived(InitialConfigurationReceivedEvent event) {
        initialConfiguration = event.getInitialConfiguration();
    }
    
    /**
     * Returns URL of the HTML file which should be displayed in Greeting view.
     * 
     * @param key
     * @return
     */
    public static native String getGreetingPanelContentURL(String key) /*-{
        try {
            return $wnd.GREETING_PANE_CONTENT[key];
        } catch (err) {
            return null;
        }
    }-*/;
    
    private void goToURL(String path) {
        UrlBuilder builder = new UrlBuilder();
        String url = builder.setProtocol(Location.getProtocol()).setHost(Location.getHost()).setPath(path).buildString();
        Window.Location.replace(url);
    }
    
    /**
     * Adds "Create account" and "Login" buttons on toolbar.
     */
    private void addButtonsForNoneAuthenticatedUser() {
        UniButton createAccountButton = new UniButton("Create free account", Type.SUCCESS, Size.SMALL);
        IDE.fireEvent(new AddToolbarItemsEvent(createAccountButton, true));
        createAccountButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createAccount();
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

    private void createAccount() {
        try {
            VirtualFileSystem.getInstance()
            .getChildren(VirtualFileSystem.getInstance().getInfo().getRoot(),
                         ItemType.PROJECT,
                         new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>())) {
                @Override
                protected void onSuccess(List<Item> result) {
                    List<String> projectIds = new ArrayList<String>();
                    for (Item project : result) {
                        projectIds.add(project.getId() + ':' + project.getName());
                    }
                    if (!projectIds.isEmpty()) {
                        Item firstItem = result.get(0);
                        String projectsDownloadUrl = firstItem.getLinkByRelation(Link.REL_DOWNLOAD_ZIP).getHref();
                        projectsDownloadUrl = projectsDownloadUrl.substring(0, projectsDownloadUrl.length() - firstItem.getId().length());
                        StringBuilder projectIDS = new StringBuilder();
                        for (String projectId : projectIds) {
                            projectIDS.append(projectId).append(';');
                        }
                        goToURL("/site/create-account?" + CopySpec10.DOWNLOAD_URL + "=" + projectsDownloadUrl + "&" + CopySpec10.PROJECT_ID + "=" + projectIDS.toString());
                    }
                }
                
                @Override
                protected void onFailure(Throwable exception) {
                    Window.alert(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            Window.alert(e.getMessage());
        }
    }
    
    private native void loginToIDE() /*-{
            if ($wnd.location.search !== "") {
                $wnd.location.search += "&login";
            } else {
                $wnd.location.search += "?login";
            }        
    }-*/;
    
    /**
     * Adds "Copy to my workspace" button on toolbar.
     */
    private void addCopyToMyWorkspaceButton() {        
        UniButton copyToMyWorkspaceButton = new UniButton("Copy to my workspace", new Image(FactoryClientBundle.INSTANCE.copyToWorkspaceIcon()), Type.PRIMARY, Size.SMALL);
        IDE.fireEvent(new AddToolbarItemsEvent(copyToMyWorkspaceButton, true));
        copyToMyWorkspaceButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new CopyProjectEvent());
            }
        });
    }
    
    /**
     * Returns URL to the greeting page.
     * 
     * @param projectSpecified
     * @return
     */
    private String getGreetingPageURL() {
        String key = "anonymous";

        boolean workspaceTemporary = initialConfiguration.getCurrentWorkspace() == null ? false 
            : initialConfiguration.getCurrentWorkspace().isTemporary();
        
        if (IDE.user.isTemporary() || "__anonim".equals(IDE.user.getName())) {
            if (workspaceTemporary) {
                key = "anonymous-workspace-temporary";
                addButtonsForNoneAuthenticatedUser();
            } else {
                key = "anonymous";
            }
        } else {
            if (workspaceTemporary) {
                key = "authenticated-workspace-temporary";
                addCopyToMyWorkspaceButton();
            } else {
                key = "authenticated";
            }
        }

        if (project != null) {
            String projectType = project.getProjectType().toLowerCase();
            while (projectType.indexOf("/") >= 0) {
                projectType = projectType.replace('/', '-');
            }

            while (projectType.indexOf(" ") >= 0) {
                projectType = projectType.replace(' ', '-');
            }

            String url = getGreetingPanelContentURL(key + "-" + projectType);
            if (url != null && !url.trim().isEmpty()) {
                return url;
            }
        }
        
        final String greetingContentURL = getGreetingPanelContentURL(key);
        if (greetingContentURL == null || greetingContentURL.trim().isEmpty()) {
            return null;
        }
        
        return greetingContentURL;
    }
    
    /**
     * 
     */
    private void loadGreeting() {
        welcomeOnceAppeared = true;
        
        final String greetingPageURL = getGreetingPageURL();
        if (greetingPageURL == null) {
            return;
        }
        
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
            loadGreeting();
        }
    };
    
    private void scheduleLoadGreeting() {
        if (welcomeOnceAppeared) {
            return;
        }
        
        scheduleGreetingTimer.cancel();
        scheduleGreetingTimer.schedule(1500);
    }
    
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
                    continue;
                }
            }

            this.@com.codenvy.ide.factory.client.greeting.GreetingUserPresenter::showGreeting(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(title, icon, greetingContentURL, notification);
        } catch (e) {
            this.@com.codenvy.ide.factory.client.greeting.GreetingUserPresenter::loadGreetingError(Ljava/lang/String;)(e.message);
        }        
    }-*/;    
    
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