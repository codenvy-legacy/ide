/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.client.github;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.http.client.RequestException;
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
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GetCollaboratorsHandler.java Aug 6, 2012
 */
public class GetCollaboratorsHandler implements EventHandler {

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

    protected void doInvite() {
        Collaborators collaboratorsForInvite = display.getCollaboratorsForInvite();
        List<GitHubUser> collaborators = collaboratorsForInvite.getCollaborators();
        for (GitHubUser gitHubUser : collaborators) {
            System.out.println("GetCollaboratorsHandler.doInvite()" + gitHubUser.getEmail());
        }
    }

    void onGetCollaborators(GetCollboratorsEvent event) {
        AutoBean<Collaborators> autoBean = GitExtension.AUTO_BEAN_FACTORY.collaborators();
        AutoBeanUnmarshaller<Collaborators> unmarshaller = new AutoBeanUnmarshaller<Collaborators>(autoBean);
        try {
            GitHubClientService.getInstance().getCollaborators(event.getUser(), event.getRepository(),
                                                               new AsyncRequestCallback<Collaborators>(unmarshaller) {

                                                                   @Override
                                                                   protected void onSuccess(Collaborators result) {
                                                                       showColloborators(result);

                                                                   }

                                                                   @Override
                                                                   protected void onFailure(Throwable exception) {
                                                                       // TODO Auto-generated method stub

                                                                   }
                                                               });
        } catch (RequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void showColloborators(Collaborators result) {
        display = GWT.create(Display.class);
        bindDisplay();
        IDE.getInstance().openView(display.asView());
        display.showCollaborators(result);
    }


}
