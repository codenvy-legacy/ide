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
package org.exoplatform.ide.git.client.remote;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.git.shared.Remote;

/**
 * Presenter for add remote repository view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 19, 2011 11:12:44 AM anya $
 */
public abstract class AddRemoteRepositoryPresenter {
    interface Display extends IsView {
        /**
         * Get cancel button's click handler.
         * 
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

        /**
         * Get ok button's click handler.
         * 
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getOkButton();

        /**
         * Change the enable state of the ok button.
         * 
         * @param enable enable state of the ok button
         */
        void enableOkButton(boolean enable);

        /**
         * Get name field.
         * 
         * @return {@link HasValue} name field
         */
        HasValue<String> getName();

        /**
         * Get URL field.
         * 
         * @return {@link HasValue} url field
         */
        HasValue<String> getUrl();
    }

    /** Presenter's display. */
    private Display display;

    /**
     * @param remote remote repository
     * @param title view's title
     */
    public AddRemoteRepositoryPresenter(Remote remote, String title) {
        if (display == null) {
            display = new AddRemoteRepositoryView(title);
            bindDisplay();
        } else {
            display.asView().setTitle(title);
        }

        display.enableOkButton(false);
        if (remote != null) {
            display.getName().setValue(remote.getName());
            display.getUrl().setValue(remote.getUrl());
        }

        IDE.getInstance().openView(display.asView());
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                onSubmit();
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getName().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableOkButton(checkNotEmpty());
            }
        });

        display.getUrl().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableOkButton(checkNotEmpty());
            }
        });

    }

    /**
     * Checks name and url fields are not empty.
     * 
     * @return boolean <code>true</code> if fields are not empty(full filled)
     */
    private boolean checkNotEmpty() {
        return display.getName().getValue() != null && display.getName().getValue().length() > 0
               && display.getUrl().getValue() != null && display.getUrl().getValue().length() > 0;
    }

    /** @return {@link Display} */
    public Display getDisplay() {
        return display;
    }

    /** This method is called, when user submits adding remote repository. */
    public abstract void onSubmit();
}
