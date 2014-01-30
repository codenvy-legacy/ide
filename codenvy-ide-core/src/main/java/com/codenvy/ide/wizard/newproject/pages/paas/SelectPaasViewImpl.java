/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.wizard.newproject.pages.paas;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * @author Evgen Vidolob
 */
public class SelectPaasViewImpl implements SelectPaasPageView {
    private static SelectPaasViewImplUiBinder ourUiBinder = GWT.create(SelectPaasViewImplUiBinder.class);
    private       ActionDelegate delegate;
    private final HTMLPanel      rootElement;

    @UiField(provided = true)
    final Resources                res;
    @UiField(provided = true)
    final CoreLocalizationConstant locale;
    @UiField
    SimplePanel paasPanel;
    private Array<ToggleButton>      paasButtons;
    @Inject
    public SelectPaasViewImpl(Resources resource, CoreLocalizationConstant locale) {
        res = resource;
        this.locale = locale;
        rootElement = ourUiBinder.createAndBindUi(this);

    }

    /** {@inheritDoc} */
    @Override
    public void selectPaas(int id) {
        for (int i = 0; i < paasButtons.size(); i++) {
            ToggleButton button = paasButtons.get(i);
            button.setDown(i == id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setPaases(Array<PaaS> paases) {
        paasButtons = Collections.createArray();

        Grid grid = new Grid(2, paases.size());
        paasPanel.setWidget(grid);
        HTMLTable.CellFormatter formatter = grid.getCellFormatter();

        //create button for each paas
        for (int i = 0; i < paases.size(); i++) {
            PaaS paas = paases.get(i);

            ImageResource icon = paas.getImage();
            final ToggleButton btn;
            if (icon != null) {
                btn = new ToggleButton(new Image(icon));
            } else {
                btn = new ToggleButton();
            }
            btn.setSize("84px", "84px");
            btn.setEnabled(false);

            final int id = i;
            btn.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    delegate.onPaaSSelected(id);
                }
            });
            grid.setWidget(0, i, btn);
            formatter.setHorizontalAlignment(0, i, HasHorizontalAlignment.ALIGN_CENTER);

            Label title = new Label(paas.getTitle());
            grid.setWidget(1, i, title);
            formatter.setHorizontalAlignment(1, i, HasHorizontalAlignment.ALIGN_CENTER);

            paasButtons.add(btn);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnablePaas(int id, boolean isEnabled) {
        ToggleButton button = paasButtons.get(id);
        button.setEnabled(isEnabled);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {

        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    interface SelectPaasViewImplUiBinder
            extends UiBinder<HTMLPanel, SelectPaasViewImpl> {
    }
}