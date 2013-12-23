/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
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
package com.codenvy.ide.welcome;

import com.codenvy.ide.util.Utils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
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

        fbFrame.setUrl(UriUtils.fromString("/ide/" + Utils.getWorkspaceName() + "/_app/fblike.html"));
        googleFrame.setUrl(UriUtils.fromString("/ide/" + Utils.getWorkspaceName() + "/_app/googleone.html"));
        googleFrame.getElement().setAttribute("scrolling", "no");
    }

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