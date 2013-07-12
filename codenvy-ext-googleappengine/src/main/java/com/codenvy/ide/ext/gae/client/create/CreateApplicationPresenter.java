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
package com.codenvy.ide.ext.gae.client.create;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateApplicationPresenter implements CreateApplicationView.ActionDelegate {
    private CreateApplicationView view;
    private EventBus              eventBus;
    private ConsolePart           console;
    private GAEClientService      service;
    private GAELocalization       constant;

    public CreateApplicationPresenter(CreateApplicationView view, EventBus eventBus, ConsolePart console, GAEClientService service,
                                      GAELocalization constant) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;

        this.view.setDelegate(this);
    }

    public void showDialog() {
        if (!view.isShown()) {
            view.showDialog();
        }
    }

    @Override
    public void onCreateApplicationButtonClicked() {
        //todo
    }

    @Override
    public void onDeployApplicationButtonClicked() {
        //todo
    }

    @Override
    public void onCancelButtonClicked() {
        view.close();
    }
}
