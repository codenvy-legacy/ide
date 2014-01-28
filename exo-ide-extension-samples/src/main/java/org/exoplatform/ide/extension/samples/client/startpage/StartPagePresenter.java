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
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.event.CreateProjectEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.extension.samples.client.inviting.google.InviteGoogleDevelopersEvent;
import org.exoplatform.ide.git.client.clone.CloneRepositoryEvent;
import org.exoplatform.ide.git.client.github.gitimport.ImportFromGithubEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Presenter for welcome view.
 * @author Oksana Vereshchaka
 */
public class StartPagePresenter implements OpenStartPageHandler, ViewClosedHandler, PremiumAccountInfoReceivedHandler {

    public interface Display extends IsView {

        HasClickHandlers getCloneLink();

        HasClickHandlers getProjectLink();

        HasClickHandlers getImportLink();

        HasClickHandlers getInvitationsLink();

        Anchor getSupportLink();

        void disableInvitationsLink();

    }

    private Display          display;
    private ReadOnlyUserView readOnlyUserView;
    private boolean          premiumUser;

    public StartPagePresenter() {
        IDE.addHandler(OpenStartPageEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(PremiumAccountInfoReceivedEvent.TYPE, this);
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

        changeSupportLink();
    }

    /** {@inheritDoc} */
    @Override
    public void onPremiumAccountInfoReceived(PremiumAccountInfoReceivedEvent event) {
        premiumUser = event.isUserHasPremiumAccount();

        if (display != null) {
            changeSupportLink();
        }
    }

    /** Perform change Support Link for premium user. */
    private void changeSupportLink() {
        display.getSupportLink().setHref(premiumUser ? "javascript:UserVoice.showPopupWidget();"
                                                     : "http://helpdesk.codenvy.com");
        if (!premiumUser) {
            display.getSupportLink().setTarget("_blank");
        }
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }
}
