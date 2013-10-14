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
package org.exoplatform.ide.extension.java.jdi.client;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.java.jdi.client.events.JRebelUserInfoEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.JRebelUserInfoHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: May 14, 2013 2:38:19 PM valeriy $
 */
public class JRebelUserInfoPresenter implements ViewClosedHandler, JRebelUserInfoHandler, ProjectOpenedHandler {

    public interface Display extends IsView {
        HasClickHandlers getOkButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getJRebelFirstNameField();

        HasValue<String> getJRebelLastNameField();

        HasValue<String> getJRebelPhoneNumberField();

        void setJRebelErrorMessageLabel(String message);

    }

    private Display      display;

    private ProjectModel project;
    
    private final byte   JREBEL_UPDATED = 0;

    public JRebelUserInfoPresenter() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(JRebelUserInfoEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!checkJRebelFieldFill()) {
                    return;
                }
                setJRebelCountProperty(Integer.toString(JREBEL_UPDATED));
                sendProfileInfoToZeroTurnaround();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
                Dialogs.getInstance().showError(DebuggerExtension.LOCALIZATION_CONSTANT.jRebelRedeployFailed());
            }
        });

        display.getJRebelFirstNameField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
            }
        });

        display.getJRebelLastNameField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
            }
        });

        display.getJRebelPhoneNumberField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                checkJRebelFieldFill();
            }
        });
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

    private void sendProfileInfoToZeroTurnaround() {
        String url = Utils.getRestContext() + "/ide/jrebel/profile/send";

        JSONObject json = new JSONObject();
        json.put("firstName", new JSONString(display.getJRebelFirstNameField().getValue()));
        json.put("lastName", new JSONString(display.getJRebelLastNameField().getValue()));
        json.put("phone", new JSONString(display.getJRebelPhoneNumberField().getValue()));

        try {
            AsyncRequest.build(RequestBuilder.POST, url)
                        .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                        .data(json.toString())
                        .send(new AsyncRequestCallback<Void>() {
                            @Override
                            protected void onSuccess(Void result) {
                                // success
                            }

                            @Override
                            protected void onFailure(Throwable exception) {
                            }
                        });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
        IDE.getInstance().closeView(display.asView().getId());
    }

    private boolean checkJRebelFieldFill() {
        if (!display.getJRebelFirstNameField().getValue().isEmpty()
            && !display.getJRebelLastNameField().getValue().isEmpty()
            && !display.getJRebelPhoneNumberField().getValue().isEmpty()) {
            String phone = display.getJRebelPhoneNumberField().getValue();

            boolean phoneMatched = phone.matches("^[+]?[\\d\\-\\s().]+$");
            if (!phoneMatched) {
                display.setJRebelErrorMessageLabel(
                       "Valid phone number consists of digits or special characters '+', '(', ')', '-' only.");
            } else {
                display.setJRebelErrorMessageLabel("");
            }
            return phoneMatched;
        }
        display.setJRebelErrorMessageLabel("All fields are required!");
        return false;
    }

    private void setJRebelCountProperty(String count) {
        if (count == null) {
            return;
        }
        else {
            for (Property prop : project.getProperties()) {
                if (prop.getName().equals(ProjectProperties.JREBEL_COUNT.value())) {
                    List<String> value = new ArrayList<String>();
                    value.add(count);
                    prop.setValue(value);
                    break;
                }
            }
        }
        
        if (project.getLinks().isEmpty()) {
            try {
                VirtualFileSystem.getInstance()
                                 .getItemById(project.getId(),
                                              new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(project))) {

                                                  @Override
                                                  protected void onSuccess(ItemWrapper result) {
                                                      project.setLinks(result.getItem().getLinks());
                                                      updateProjectProperties();
                                                  }

                                                  @Override
                                                  protected void onFailure(Throwable exception) {
                                                      IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                  }
                                              });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        } else {
            updateProjectProperties();
        }
    }
    
    private void updateProjectProperties(){
        try {
            VirtualFileSystem.getInstance().updateItem(project, null, new AsyncRequestCallback<ItemWrapper>() {

                @Override
                protected void onSuccess(ItemWrapper result) {
                    // nothing to do
                }

                @Override
                protected void onFailure(Throwable ignore) {
                    // ignore this exception
                }
            });
        } catch (RequestException e) {
            // ignore this exception
        }
    }
    
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
    }

    @Override
    public void onJRebelInfo(JRebelUserInfoEvent e) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();

            IDE.getInstance().openView(display.asView());
        }
    }
}
