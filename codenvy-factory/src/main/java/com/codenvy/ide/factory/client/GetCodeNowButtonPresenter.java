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

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

/**
 * Presenter to generate a CodeNow button.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GetCodeNowButtonPresenter.java Jun 11, 2013 12:17:04 PM azatsarynnyy $
 */
public class GetCodeNowButtonPresenter implements GetCodeNowButtonHandler, ViewClosedHandler {

    public interface Display extends IsView {
        /**
         * Returns 'Factory URL' field.
         * 
         * @return 'Factory URL' field
         */
        TextFieldItem getFactoryURLField();

        /**
         * Returns the 'Ok' button.
         * 
         * @return 'Ok' button
         */
        HasClickHandlers getOkButton();

        /** Give focus to the 'Factory URL' field. */
        void focusURLField();

        /** Select all text in the 'Factory URL' field. */
        void selectURLField();
    }

    /** Display. */
    private Display display;

    public GetCodeNowButtonPresenter() {
        IDE.addHandler(GetCodeNowButtonEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    private void openView() {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }

        display.getFactoryURLField().setValue("https://www.anonymous.codenvy.com");
        display.selectURLField();
        display.focusURLField();
    }

    /**
     * @see com.codenvy.ide.factory.client.ShareWithFactoryUrlHandler#onCreateFactoryURL(com.codenvy.ide.factory.client.ShareWithFactoryUrlEvent)
     */
    @Override
    public void onGetCodeNowButton(GetCodeNowButtonEvent event) {
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
