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
package org.exoplatform.ide.extension.googleappengine.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.api.FolderTreeUnmarshaller;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.googleappengine.client.GaeTools;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;
import org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationEvent;
import org.exoplatform.ide.extension.googleappengine.client.login.OAuthLoginView;
import org.exoplatform.ide.extension.googleappengine.shared.GaeUser;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 21, 2012 2:27:42 PM anya $
 */
public class CreateApplicationPresenter extends GoogleAppEnginePresenter implements CreateApplicationHandler,
                                                                                    ViewClosedHandler {
    interface Display extends IsView {
        /** @return {@link HasClickHandlers} handler for ready button click */
        HasClickHandlers getDeployButton();

        /** @return {@link HasClickHandlers} handler for cancel button click */
        HasClickHandlers getCancelButton();

        /** @return {@link HasClickHandlers} handler for cancel button click */
        HasClickHandlers getCreateButton();

        /**
         *
         */
        void enableDeployButton(boolean enable);

        /**
         *
         */
        void enableCreateButton(boolean enable);

        void setUserInstructions(String instructions);
    }

    private Display display;

    private static final String GOOGLE_APP_ENGINE_URL = "https://appengine.google.com/start/createapp";

    private final String restContext = Utils.getRestContext();

    public CreateApplicationPresenter() {
        

        IDE.addHandler(CreateApplicationEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getDeployButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                onDeploy();
            }
        });

        display.getCreateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                startCreateApp();
            }
        });
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide
     * .extension.googleappengine.client.create.CreateApplicationEvent) */
    @Override
    public void onCreateApplication(CreateApplicationEvent event) {
        currentProject = (event.getProject() != null) ? event.getProject() : currentProject;

        if (display == null) {
            display = GWT.create(Display.class);
        }

        isUserLogged();
    }

    private void checkIfAppengineWebXmlExist() {
        final Loader loader = new GWTLoader();
        try {
            loader.setMessage("Searching for appengine-web.xml...");
            loader.show();
            FolderModel target = currentProject;
            FolderTreeUnmarshaller unmarshaller = new FolderTreeUnmarshaller(target);
            VirtualFileSystem.getInstance().getTree(target.getId(), new AsyncRequestCallback<Folder>(unmarshaller) {
                @Override
                protected void onSuccess(Folder result) {
                    Item item = result;
                    List<Item> q = new ArrayList<Item>();
                    q.addAll(((FolderModel)item).getChildren().getItems());
                    while (!q.isEmpty()) {
                        Item current = q.remove(0);

                        if (current instanceof FolderModel) {
                            if (((FolderModel)current).getChildren().getItems().size() > 0) {
                                q.addAll(((FolderModel)current).getChildren().getItems());
                            }
                        } else {
                            if ("appengine-web.xml".equals(current.getName())
                                && "WEB-INF".equals(((FileModel)current).getParent().getName())) {
                                loader.hide();
                                bindDisplay();
                                IDE.getInstance().openView(display.asView());
                                return;
                            }
                        }
                    }

                    loader.hide();
                    Dialogs.getInstance().showError("File appengine-web.xml not found. Creating application failed.");
                }

                @Override
                protected void onFailure(Throwable exception) {
                    loader.hide();
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            loader.hide();
            IDE.fireEvent(new ExceptionThrownEvent(e));
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

    /** Perform actions, when user is ready. */
    public void onDeploy() {
        if (isAppEngineProject()) {
            IDE.fireEvent(new DeployApplicationEvent(currentProject));
        } else {
            Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.createApplicationCannotDeploy());
        }
        IDE.getInstance().closeView(display.asView().getId());
    }

    /**
     * Will be opened Google AppEngine page for create new application with redirection url in query parameter (e.g.
     * https://appengine.google.com/start/createapp?redirect_url=http://tenant.cloud-ide.com/rest/service After creation new
     * Application Google will call http://tenant.cloud-ide.com/rest/service?app_id=s~new-app&foo=bar IDE can get new application
     * id in this case.
     */
    private void startCreateApp() {
        display.enableDeployButton(true);
        display.enableCreateButton(false);
        display.setUserInstructions(GoogleAppEngineExtension.GAE_LOCALIZATION.deployApplicationInstruction());

        String projectId = currentProject.getId();
        String vfsId = currentVfs.getId();
        UrlBuilder builder = new UrlBuilder();
        String redirectUrl = builder.setProtocol(Window.Location.getProtocol())//
                .setHost(Window.Location.getHost())//
                .setPath(restContext + Utils.getWorkspaceName() + "/appengine/change-appid/" + vfsId + "/" + projectId).buildString();

        String url = GOOGLE_APP_ENGINE_URL + "?redirect_url=" + redirectUrl;

        clickToCreate(url);
    }

    private static native void clickToCreate(String url)
   /*-{
       $wnd.open(url, "_blank");
   }-*/;

    private void isUserLogged() {
        AutoBean<GaeUser> user = GoogleAppEngineExtension.AUTO_BEAN_FACTORY.user();
        AutoBeanUnmarshaller<GaeUser> unmarshaller = new AutoBeanUnmarshaller<GaeUser>(user);
        try {
            GoogleAppEngineClientService.getInstance().getLoggedUser(
                    new GoogleAppEngineAsyncRequestCallback<GaeUser>(unmarshaller) {

                        @Override
                        protected void onSuccess(GaeUser result) {
                            if (!GaeTools.isAuthenticatedInAppEngine(result.getToken())) {
                                // IDE.fireEvent(new LoginEvent());
                                new OAuthLoginView();
                            } else {
                                if (ProjectResolver.APP_ENGINE_JAVA.equals(currentProject.getProjectType()))
                                    checkIfAppengineWebXmlExist();
                                else
                                {
                                    bindDisplay();
                                    IDE.getInstance().openView(display.asView());
                                }
                            }
                        }

                        /**
                         * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback#onFailure(java
                         * .lang.Throwable)
                         */
                        @Override
                        protected void onFailure(Throwable exception) {
                            super.onFailure(exception);
                            // TODO
                        }
                    });
        } catch (RequestException e) {
            // TODO
        }
    }
}
