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

        display.getSupportLink().setHref(premiumUser ? "javascript:UserVoice.showPopupWidget();"
                                                     : "javascript:window.open('http://helpdesk.codenvy.com');");
    }

    @Override
    public void onPremiumAccountInfoReceived(PremiumAccountInfoReceivedEvent event) {
        if (display == null) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((View)d);
            display = d;
            premiumUser = event.isUserHasPremiumAccount();
            bindDisplay();
            IDE.fireEvent(new WelcomePageOpenedEvent());
        } else {
            IDE.fireEvent(new ExceptionThrownEvent("Start Page View must be null"));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenStartPage(OpenStartPageEvent event) {
        getUserFromApi();
    }

    /** {@inheritDoc} */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** Call User API to fetch information about current loggined user. */
    private void getUserFromApi() {
        try {
            AsyncRequestCallback<StringBuilder> callback =
                    new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                        @Override
                        protected void onSuccess(StringBuilder json) {
                            List<String> accountIds = new ArrayList<String>();
                            JSONValue jsonUserProfile = JSONParser.parseStrict(json.toString());
                            String currentUserId = jsonUserProfile.isObject().get("id").isString().stringValue();
                            if (jsonUserProfile.isObject()!= null && jsonUserProfile.isObject().containsKey("accounts")) {
                                JSONArray jsonAccounts = jsonUserProfile.isObject().get("accounts").isArray();
                                for (int i = 0; i < jsonAccounts.size(); i++) {
                                    JSONObject jsonAccount = jsonAccounts.get(i).isObject();
                                    accountIds.add(jsonAccount.get("id").isString().stringValue());
                                }

                                accountInfo.clear();
                                getAccountPremierProperty(currentUserId, accountIds);
                            }
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new PremiumAccountInfoReceivedEvent(false));
                        }
                    };
            AsyncRequest.build(RequestBuilder.GET, "/api/user").header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
        } catch (RequestException e) {
            IDE.fireEvent(new PremiumAccountInfoReceivedEvent(false));
        }
    }

    private HashMap<String, Boolean> accountInfo = new HashMap<String, Boolean>();

    /** Get information about all accounts which user managed. */
    private void getAccountPremierProperty(final String currentUserId, final List<String> accountIds) {
        if (accountIds.isEmpty()) {
            IDE.fireEvent(new PremiumAccountInfoReceivedEvent(false));
            return;
        }

        final String accountId = accountIds.get(0);
        accountIds.remove(0);

        try {
            AsyncRequestCallback<StringBuilder> callback =
                    new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                        @Override
                        protected void onSuccess(StringBuilder json) {
                            JSONObject jsonAccount = JSONParser.parseStrict(json.toString()).isObject();
                            String accountOwnerId = jsonAccount.get("owner").isObject().get("id").isString().stringValue();
                            if (accountOwnerId.equals(currentUserId)) {
                                JSONObject jsonAttributes = jsonAccount.get("attributes").isObject();
                                if (jsonAttributes.containsKey("tariff_plan")) {
                                    IDE.fireEvent(new PremiumAccountInfoReceivedEvent(true));
                                    return;
                                }
                            }
                            getAccountPremierProperty(currentUserId, accountIds);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new PremiumAccountInfoReceivedEvent(false));
                        }
                    };
            AsyncRequest.build(RequestBuilder.GET, "/api/account/" + accountId).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                        .send(callback);
        } catch (RequestException e) {
            IDE.fireEvent(new PremiumAccountInfoReceivedEvent(false));
        }
    }

}
