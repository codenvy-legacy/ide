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
package org.exoplatform.ide.extension.samples.client.startpage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.event.CreateProjectEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.inviting.google.InviteGoogleDevelopersEvent;
import org.exoplatform.ide.git.client.clone.CloneRepositoryEvent;
import org.exoplatform.ide.git.client.github.gitimport.ImportFromGithubEvent;

/**
 * Presenter for welcome view.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: WelcomePresenter.java Aug 25, 2011 12:27:27 PM vereshchaka $
 */
public class StartPagePresenter implements OpenStartPageHandler, ViewClosedHandler {

    public interface Display extends IsView {

        HasClickHandlers getCloneLink();

        HasClickHandlers getProjectLink();

        HasClickHandlers getImportLink();

        HasClickHandlers getInvitationsLink();
        
        void disableInvitationsLink();

    }

    private Display          display;

    private ReadOnlyUserView readOnlyUserView;

    public StartPagePresenter() {
        IDE.addHandler(OpenStartPageEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    private void bindDisplay() {
        display.getCloneLink().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (IDE.isRoUser()) {
                    if (readOnlyUserView == null)
                        readOnlyUserView = new ReadOnlyUserView(IDE.user.getWorkspaces());
                    IDE.getInstance().openView(readOnlyUserView);
                } else {
                    IDE.fireEvent(new CloneRepositoryEvent());
                }
            }
        });

        display.getProjectLink().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (IDE.isRoUser()) {
                    if (readOnlyUserView == null)
                        readOnlyUserView = new ReadOnlyUserView(IDE.user.getWorkspaces());
                    IDE.getInstance().openView(readOnlyUserView);
                } else {
                    IDE.fireEvent(new CreateProjectEvent());
                }
            }
        });

        display.getImportLink().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (IDE.isRoUser()) {
                    if (readOnlyUserView == null)
                        readOnlyUserView = new ReadOnlyUserView(IDE.user.getWorkspaces());
                    IDE.getInstance().openView(readOnlyUserView);
                } else {
                    IDE.fireEvent(new ImportFromGithubEvent());
                }
            }
        });

        display.getInvitationsLink().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!(IDE.isRoUser() || IDE.currentWorkspace.isTemporary())) {
                    IDE.fireEvent(new InviteGoogleDevelopersEvent());
                }
            }
        });
        if (IDE.isRoUser() || IDE.currentWorkspace.isTemporary())
          display.disableInvitationsLink();
    }

    /**
     * @see org.exoplatform.ide.client.OpenStartPageHandler.OpenWelcomeHandler#onOpenStartPage(org.exoplatform.ide.client
     *      .OpenStartPageEvent.OpenWelcomeEvent)
     */
    @Override
    public void onOpenStartPage(OpenStartPageEvent event) {
        if (display == null) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((View)d);
            display = d;
            bindDisplay();
            IDE.fireEvent(new WelcomePageOpenedEvent());
        } else {
            IDE.fireEvent(new ExceptionThrownEvent("Start Page View must be null"));
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}
