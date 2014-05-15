/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.factory.client.welcome;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Frame;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Vitaliy Guliy
 */
@Singleton
public class GreetingPartViewImpl extends BaseView<GreetingPartView.ActionDelegate> implements GreetingPartView {

    private Frame frame;

    @Inject
    public GreetingPartViewImpl(PartStackUIResources resources) {
        super(resources);

        frame = new Frame();
        frame.getElement().getStyle().setBorderStyle(Style.BorderStyle.NONE);
        frame.setWidth("100%");
        frame.setHeight("100%");

        frame.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);

        frame.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                frame.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
            }
        });

        container.add(frame);
    }

    @Override
    public void showGreeting(String url) {
        frame.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        frame.setUrl(url);
    }

}
