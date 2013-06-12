/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.factory.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

import java.util.Date;

/**
 * Presenter to ask user commit his changes before generating a Factory URL.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CommitChangesPresenter.java Jun 11, 2013 12:17:04 PM azatsarynnyy $
 */
public class CommitChangesPresenter implements CommitChangesHandler, ViewClosedHandler {

    public interface Display extends IsView {
        /**
         * Returns 'Commit description' field.
         * 
         * @return 'Commit description' field
         */
        TextFieldItem getDescriptionField();

        /**
         * Set placeholder text for 'Description' field.
         * 
         * @param text description placeholder text
         */
        void setPlaceholderText(String text);

        /**
         * Returns the 'Ok' button.
         * 
         * @return 'Ok' button
         */
        HasClickHandlers getOkButton();

        /**
         * Returns the 'Continue' button.
         * 
         * @return 'Continue' button
         */
        HasClickHandlers getContinueButton();

        /** Give focus to the 'Commit description' field. */
        void focusDescriptionField();

        /** Select all text in the 'Commit description' field. */
        void selectDescriptionField();
    }

    /** Display. */
    private Display display;

    public CommitChangesPresenter() {
        IDE.addHandler(CommitChangesEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO commit changes to git repo
            }
        });

        display.getContinueButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO open FactoryUrlPresenter
            }
        });
    }

    private void openView() {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }

        DateTimeFormat format = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601);
        display.setPlaceholderText("Codenvy: Factory URL - " + format.format(new Date()));
        display.selectDescriptionField();
        display.focusDescriptionField();
    }

    /**
     * @see com.codenvy.ide.factory.client.CommitChangesHandler#onCommitChanges(com.codenvy.ide.factory.client.CommitChangesEvent)
     */
    @Override
    public void onCommitChanges(CommitChangesEvent event) {
        openView();
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
