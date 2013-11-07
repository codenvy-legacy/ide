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
package org.exoplatform.ide.extension.samples.client.inviting.google;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.client.framework.invite.GoogleContactsService;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.jenkins.client.marshal.StringContentUnmarshaller;
import org.exoplatform.ide.extension.samples.client.inviting.InviteClientService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class InviteGoogleDevelopersPresenter implements InviteGoogleDevelopersHandler, ViewClosedHandler,
                                            GoogleContactSelectionChangedHandler, JsPopUpOAuthWindow.JsPopUpOAuthWindowCallback {

    public interface Display extends IsView {

        void setDevelopersListVisible(boolean visible);

        void setDevelopers(List<GoogleContact> developers, GoogleContactSelectionChangedHandler selectionChangedHandler);

        boolean isSelected(GoogleContact contact);

        void setSelected(GoogleContact contact, boolean selected);

        HasValue<Boolean> getSelectAllCheckBox();

        String getInviteMessge();

        HasClickHandlers getInviteButton();

        HasClickHandlers getCloseButton();

        void setInviteButtonEnabled(boolean enabled);

        void setInviteButtonTitle(String title);

        HasValue<String> getEmailsTextField();

        FocusWidget getEmailsFocusWidget();

        void showEmailsHint();

        void hideEmailsHint();

        void setMessageFiledVisibility(boolean visible);

        HasClickHandlers getAddMessageButton();

        void setAddMessageButtonEnabled(boolean enabled);

        void setAddMessageButtonText(String text);

        String getAddMessageButtonText();

        HasClickHandlers getLoadGmailContactsButton();

        void setLoadGmailContactsButtonText(String text);

        String getLoadGmailContactsButtonText();

    }

    private Display                          display;

    private List<GoogleContact>              contacts;

    private List<String>                     customEmailsList         = new ArrayList<String>();

    private List<String>                     selectedEmailsList       = new ArrayList<String>();

    private List<String>                     emailsToInvite           = new ArrayList<String>();

    private int                              invitations              = 0;

    private boolean                          contactsLoaded           = false;

    /** Comparator for ordering Google contacts list alphabetically, by first e-mail. */
    private static Comparator<GoogleContact> googleContactsComparator = new GoogleContactsComparator();

    public InviteGoogleDevelopersPresenter() {
        IDE.getInstance().addControl(new InviteGoogleDevelopersControl());

        IDE.addHandler(InviteGoogleDevelopersEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    private void createDisplay() {
        contactsLoaded = false;
        if (display != null) {
            return;
        }
        display = GWT.create(Display.class);
        bindDisplay();
    }

    @Override
    public void onInviteGoogleDevelopers(InviteGoogleDevelopersEvent event) {
        createDisplay();
        customEmailsList.clear();
        selectedEmailsList.clear();
        emailsToInvite.clear();
        display.showEmailsHint();
        contacts = new ArrayList<GoogleContact>();
        updateInviteButton();
        IDE.getInstance().openView(display.asView());
        display.setDevelopersListVisible(false);
    }

    private void isAuthenticate() {
        try {
            StringContentUnmarshaller unmarshaller = new StringContentUnmarshaller(new StringBuilder());
            GoogleContactsService.getInstance().isAuthenticate(new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                @Override
                protected void onSuccess(StringBuilder s) {
                    if (s != null && !s.toString().isEmpty() && !(s.indexOf("https://www.google.com/m8/feeds") < 0)) {
                        loadGoogleContacts();
                    } else {
                        oAuthLoginStart();
                    }
                }

                @Override
                protected void onFailure(Throwable throwable) {
                    oAuthLoginStart();
                }
            });
        } catch (RequestException exception) {
            loadContactsFailed();
        }
    }

    public void oAuthLoginStart() {
        String message = "Would you like to find contacts in your Google contact list? <br> "
                         + "You will be redirected to Google authorization page.";
        String title = "You have to be logged in Google account!";

        BooleanValueReceivedHandler handler = new BooleanValueReceivedHandler() {
            @Override
            public void booleanValueReceived(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    String authUrl = Utils.getAuthorizationContext()
                                     + "/ide/oauth/authenticate?oauth_provider=google&mode=federated_login"
                                     + "&scope=https://www.google.com/m8/feeds"
                                     + "&userId=" + IDE.user.getUserId()
                                     + "&redirect_after_login=/ide/" + Utils.getWorkspaceName();
                    JsPopUpOAuthWindow authWindow = new JsPopUpOAuthWindow(authUrl, Utils.getAuthorizationErrorPageURL(), 980, 500,
                                                                           InviteGoogleDevelopersPresenter.this);
                    authWindow.loginWithOAuth();
                } else {
                    loadContactsFailed();
                }
            }
        };
        Dialogs.getInstance().ask(title, message, handler);
    }

    @Override
    public void oAuthFinished(int authenticationStatus) {
        if (authenticationStatus == 2) {
            loadGoogleContacts();
        }
    }

    private void loadGoogleContacts() {
        try {
            GoogleContactsService.getInstance()
                                 .getContacts(
                                              new AsyncRequestCallback<List<GoogleContact>>(
                                                                                            new InviteGoogleContactsUnmarshaller(
                                                                                                                                 new ArrayList<GoogleContact>())) {
                                                  @Override
                                                  protected void onSuccess(List<GoogleContact> result) {
                                                      googleContactsReceived(result);
                                                      contactsLoaded = true;
                                                      reactGmailContacts();
                                                  }

                                                  @Override
                                                  protected void onFailure(Throwable exception) {
                                                      loadContactsFailed();
                                                  }
                                              });
            IDELoader.show("Loading Google contacts...");
        } catch (RequestException exception) {
            loadContactsFailed();
        }
    }

    private void loadContactsFailed() {
        display.setDevelopersListVisible(false);
    }

    private void googleContactsReceived(List<GoogleContact> contacts) {
        for (GoogleContact contact : contacts) {
            if (contact.getEmailAddresses() != null && !contact.getEmailAddresses().isEmpty()) {
                this.contacts.add(contact);
            }
        }
        Collections.sort(contacts, googleContactsComparator);

        display.setDevelopers(contacts, this);
        display.setDevelopersListVisible(true);
    }

    @Override
    public void onGoogleContactSelectionChanged(GoogleContact contact, boolean selected) {
        updateSelectedEmailsList();
        updateInviteButton();
        if (selectedEmailsList.size() + customEmailsList.size() == 0) {
            resetMessageEntry();
        }
    }

    private void updateSelectedEmailsList() {
        selectedEmailsList.clear();

        for (GoogleContact contact : contacts) {
            if (display.isSelected(contact)) {
                selectedEmailsList.add(contact.getEmailAddresses().get(0));
            }
        }
    }

    private void bindDisplay() {
        display.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
        display.getAddMessageButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                reactMessageEntry();
            }
        });
        display.getLoadGmailContactsButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                reactGmailContacts();
            }
        });
        display.getInviteButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                emailsToInvite.clear();
                emailsToInvite.addAll(customEmailsList);
                emailsToInvite.addAll(selectedEmailsList);
                invitations = 0;
                sendNextEmail();
            }
        });

        display.getSelectAllCheckBox().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                boolean selectAll = event.getValue() == null ? false : event.getValue();
                for (GoogleContact contact : contacts) {
                    display.setSelected(contact, selectAll);
                }

                updateSelectedEmailsList();
                updateInviteButton();
                if (selectedEmailsList.size() + customEmailsList.size() == 0) {
                    resetMessageEntry();
                }
            }
        });

        display.getEmailsTextField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                parseCustomEmails(event.getValue());
            }
        });

        display.getEmailsFocusWidget().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                display.hideEmailsHint();
            }
        });

        display.getEmailsFocusWidget().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                String emails = display.getEmailsTextField().getValue();
                if (emails == null || emails.trim().isEmpty()) {
                    display.showEmailsHint();
                }
            }
        });
    }

    private void parseCustomEmails(String customEmails) {
        customEmailsList.clear();

        if (customEmails == null || customEmails.trim().isEmpty()) {
            updateInviteButton();
            return;
        }

        String[] mails = customEmails.split(",");
        for (String email : mails) {
            email = email.trim();
            String[] mailParts = email.split("@");
            if (mailParts.length != 2) {
                continue;
            }

            customEmailsList.add(email);
        }

        updateInviteButton();
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    private void reactMessageEntry() {
        if (display.getAddMessageButtonText().equals("Add a message")) {
            display.setAddMessageButtonText("Discard message");
            display.setMessageFiledVisibility(true);
        } else {
            display.setAddMessageButtonText("Add a message");
            display.setMessageFiledVisibility(false);
        }
    }

    private void resetMessageEntry() {
        if (display.getAddMessageButtonText().equals("Discard message")) {
            display.setAddMessageButtonText("Add a message");
            display.setMessageFiledVisibility(false);
        }
    }

    private void updateInviteButton() {
        int emails = selectedEmailsList.size() + customEmailsList.size();
        display.setInviteButtonEnabled(emails > 0);
        display.setAddMessageButtonEnabled(emails > 0);
        if (emails == 1)
            display.setInviteButtonTitle("Invite 1 developer");
        else
            display.setInviteButtonTitle("Invite" + (emails > 0 ? " " + emails + " " : " ") + "developers");

    }

    public void sendNextEmail() {
        if (emailsToInvite.size() == 0) {
            IDELoader.hide();
            IDE.getInstance().closeView(display.asView().getId());
            if (invitations == 1) {
                Dialogs.getInstance().showInfo("IDE", "One invitation was sent successfully.");
            } else {
                Dialogs.getInstance().showInfo("IDE", "" + invitations + " invitations were sent successfully.");
            }
            return;
        }

        final String emailToInvite = emailsToInvite.remove(0);
        String inviteMessage = display.getInviteMessge();

        IDELoader.show("Inviting " + emailToInvite);
        try {
            InviteClientService.getInstance().inviteUser(emailToInvite, inviteMessage, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    invitations++;
                    sendNextEmail();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDELoader.hide();
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                    return;
                }
            });
        } catch (RequestException e) {
            IDELoader.hide();
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void reactGmailContacts() {
        if (display.getLoadGmailContactsButtonText().equals("Show gmail contacts")) {
            if (!contactsLoaded) {
                isAuthenticate();
            } else {
                display.setDevelopersListVisible(true);
                display.setLoadGmailContactsButtonText("Hide gmail contacts");
            }
        } else if (display.getLoadGmailContactsButtonText().equals("Hide gmail contacts")) {
            display.setLoadGmailContactsButtonText("Show gmail contacts");
            display.setDevelopersListVisible(false);
        }
    }
}
