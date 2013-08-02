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
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.gwtframework.ui.client.command.ui.AddToolbarItemsEvent;
import org.exoplatform.gwtframework.ui.client.command.ui.ToolbarShadowButton;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.configuration.IDEInitialConfiguration;
import org.exoplatform.ide.client.framework.configuration.InitialConfigurationReceivedEvent;
import org.exoplatform.ide.client.framework.configuration.InitialConfigurationReceivedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class GreetingUserPresenter implements 
        InitialConfigurationReceivedHandler,
        InitializeServicesHandler {

    public interface GreetingDisplay extends IsView {
    }

    /**
     * Initial IDE configuration
     */
    private IDEInitialConfiguration initialConfiguration;

    /**
     * Creates presenter instance
     */
    public GreetingUserPresenter() {
        IDE.addHandler(InitialConfigurationReceivedEvent.TYPE, this);
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
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
            return $wnd.greetingPaneContent[key];
        } catch (err) {
            return null;
        }
    }-*/;
    
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new Timer() {
            @Override
            public void run() {
                loadGreeting();
            }
        }.schedule(500);
    }
    
    private void goToURL(String path) {
        UrlBuilder builder = new UrlBuilder();
        String url = builder.setProtocol(Location.getProtocol()).setHost(Location.getHost()).setPath(path).buildString();
        Window.Location.replace(url);
    }
    
    /**
     * Adds "Create account" and "Login" buttons on toolbar.
     */
    private void addButtonsForNoneAuthenticatedUser() {
        ToolbarShadowButton createAccountButton = new ToolbarShadowButton(
               FactoryClientBundle.INSTANCE.createAccount(), FactoryClientBundle.INSTANCE.createAccountHover(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    goToURL("/create-account");
                }
            });        
        IDE.fireEvent(new AddToolbarItemsEvent(createAccountButton, true));
        
        ToolbarShadowButton loginButton = new ToolbarShadowButton(
               FactoryClientBundle.INSTANCE.login(), FactoryClientBundle.INSTANCE.loginHover(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    goToURL("/login");
                }
            });        
        IDE.fireEvent(new AddToolbarItemsEvent(loginButton, true));
    }
    
    /**
     * Adds "Copy to my workspace" button on toolbar.
     */
    private void addCopyToMyWorkspaceButton() {
        ToolbarShadowButton copyToMyWorkspaceButton = new ToolbarShadowButton(
               FactoryClientBundle.INSTANCE.copyToMyWorkspace(), FactoryClientBundle.INSTANCE.copyToMyWorkspaceHover(), new ClickHandler() {
                   @Override
                   public void onClick(ClickEvent event) {
                       IDE.fireEvent(new CopyProjectEvent());
                   }
               });        
        IDE.fireEvent(new AddToolbarItemsEvent(copyToMyWorkspaceButton, true));        
    }
    
    /**
     * 
     */
    private void loadGreeting() {
        String key = "anonymous";

        boolean workspaceTemporary = initialConfiguration.getCurrentWorkspace() == null ? false : initialConfiguration.getCurrentWorkspace().isTemporary();
        
        String userName = initialConfiguration.getUserInfo().getName();
        if (userName.equals("_anonim") || userName.equals("__anonim") || userName.startsWith("tmp-")) {
            if (workspaceTemporary) {
                key = "anonymous-workspace-temporary";
            } else {
                key = "anonymous";
            }

            addButtonsForNoneAuthenticatedUser();
        } else {
            if (workspaceTemporary) {
                key = "authenticated-workspace-temporary";
                addCopyToMyWorkspaceButton();
            } else {
                key = "authenticated";
            }
        }
        
        final String greetingContentURL = getGreetingPanelContentURL(key);
        if (greetingContentURL == null || greetingContentURL.trim().isEmpty()) {
            return;
        }
        
        final Frame frame = new Frame(greetingContentURL);
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
                fetchGreetingParamsFromIFrame(IFrameElement.as(frame.getElement()), greetingContentURL);
                frame.removeFromParent();
            }
        });
        
        RootPanel.get().add(frame);
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
        Image icon = new Image(iconURL);
        icon.setWidth("16px");
        icon.setHeight("16px");
        
        GreetingDisplay display = new GreetingView(title, icon, greetingContentURL);
        IDE.getInstance().openView(display.asView());
        
        new Timer() {
            @Override
            public void run() {
                new TooltipHint(notification);
            }
        }.schedule(1000);
    }
    
    private void loadGreetingError(String message) {
        Dialogs.getInstance().showError("IDE", "Could not load greeting page");
    }
    
}
