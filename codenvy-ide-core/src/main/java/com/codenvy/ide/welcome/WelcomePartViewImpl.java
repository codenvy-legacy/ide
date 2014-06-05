/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.welcome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link WelcomePartView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class WelcomePartViewImpl extends Composite implements WelcomePartView {
    interface WelcomePartViewImplUiBinder extends UiBinder<Widget, WelcomePartViewImpl> {
    }

    private static WelcomePartViewImplUiBinder ourUiBinder = GWT.create(WelcomePartViewImplUiBinder.class);

    @UiField
    Frame           fbFrame;
    @UiField
    Frame           googleFrame;
    @UiField
    DockLayoutPanel westPanel;
    @UiField
    DockLayoutPanel eastPanel;
    @UiField(provided = true)
    final   WelcomePageResources        res;
    @UiField(provided = true)
    final   WelcomeLocalizationConstant locale;
    private ActionDelegate              delegate;

    /**
     * Create view.
     *
     * @param res
     * @param locale
     */
    @Inject
    protected WelcomePartViewImpl(WelcomePageResources res, WelcomeLocalizationConstant locale) {
        this.res = res;
        this.locale = locale;

        initWidget(ourUiBinder.createAndBindUi(this));

        fbFrame.setUrl(UriUtils.fromString(facebookLikeURL()));
        googleFrame.setUrl(UriUtils.fromString(googleLikeURL()));
        
        googleFrame.getElement().setAttribute("scrolling", "no");
    }
    
    /**
     * Returns URL to Facebook like page.
     * 
     * @return
     */
    private static native String facebookLikeURL() /*-{
        return $wnd["facebook_like_url"];
    }-*/;    

    /**
     * Returns URL to Google like page.
     * 
     * @return
     */
    private static native String googleLikeURL() /*-{
        return $wnd["google_like_url"];
    }-*/;
    

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void addItem(String title, String caption, ImageResource icon, final int itemIndex) {
        SimplePanel panel = new SimplePanel();
        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.addStyleName(res.welcomeCSS().item());

        DockLayoutPanel item = new DockLayoutPanel(Style.Unit.PX);
        item.setWidth("100%");
        item.setHeight("100%");

        item.addWest(new Image(icon), 90);

        FlowPanel center = new FlowPanel();

        Anchor anchor = new Anchor();
        anchor.addStyleName(res.welcomeCSS().link());
        anchor.setText(title);
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onItemClicked(itemIndex);
            }
        });

        Label label = new Label();
        label.setText(caption);
        label.addStyleName(res.welcomeCSS().welcomeLabel());

        center.add(anchor);
        center.add(label);

        item.add(center);

        panel.setWidget(item);

        if (itemIndex % 2 == 0) {
            westPanel.addNorth(panel, 110);
        } else {
            eastPanel.addNorth(panel, 110);
        }
    }
}