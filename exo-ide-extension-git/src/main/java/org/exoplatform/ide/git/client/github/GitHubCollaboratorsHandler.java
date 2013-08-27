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
package org.exoplatform.ide.git.client.github;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.GitHubUser;

import java.util.List;

/**
 * Provide access via GitHubClientService for getting list of collaborators and allow invite invite they to Codenvy community. For inviting
 * call REST service from cloud-ide project.
 * <p/>
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GitHubCollaboratorsHandler.java Aug 6, 2012
 */
public class GitHubCollaboratorsHandler {

    interface Display extends IsView {
        void showCollaborators(Collaborators collaborators);

        Collaborators getCollaboratorsForInvite();

        HasClickHandlers getInviteButton();

        HasClickHandlers getCloseButton();
    }

    private Display display;

    public void bindDisplay() {
        display.getCloseButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getInviteButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doInvite();
            }
        });
    }

    /** Send invitation to selected collaborators */
    protected void doInvite() {
        Collaborators collaboratorsForInvite = display.getCollaboratorsForInvite();
        List<GitHubUser> collaborators = collaboratorsForInvite.getCollaborators();
        for (GitHubUser gitHubUser : collaborators) {
            System.out.println("GetCollaboratorsHandler.doInvite()" + gitHubUser.getEmail());
        }

        IDE.getInstance().closeView(display.asView().getId());
    }

    /**
     * Get list of collaborators of GitHub repository. Display in pop window wit proposal inviting join to eXo IDE community
     * 
     * @param user
     * @param repository
     */
    public void showCollaborators(String user, String repository) {
        Window.alert("User > " + user);
        Window.alert("Repository > " + repository);

        AutoBean<Collaborators> autoBean = GitExtension.AUTO_BEAN_FACTORY.collaborators();
        AutoBeanUnmarshaller<Collaborators> unmarshaller = new AutoBeanUnmarshaller<Collaborators>(autoBean);
        try {
            GitHubClientService.getInstance().getCollaborators(user, repository,
                                                               new AsyncRequestCallback<Collaborators>(unmarshaller) {

                                                                   @Override
                                                                   protected void onSuccess(Collaborators result) {
                                                                       if (result != null && !result.getCollaborators().isEmpty()) {
                                                                           display = GWT.create(Display.class);
                                                                           bindDisplay();
                                                                           IDE.getInstance().openView(display.asView());
                                                                           display.showCollaborators(result);
                                                                       }
                                                                   }

                                                                   @Override
                                                                   protected void onFailure(Throwable exception) {
                                                                       // Nothing todo
                                                                   }
                                                               });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }
}
