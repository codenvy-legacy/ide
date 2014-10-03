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

package com.codenvy.ide.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.CustomComponentAction;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.workspace.WorkBenchPresenter;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGButtonBase;
import org.vectomatic.dom.svg.ui.SVGToggleButton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class ExpandEditorAction extends Action implements CustomComponentAction {

    private final Resources                resources;
    private final WorkBenchPresenter       workBenchPresenter;
    private final CoreLocalizationConstant constant;
    private final AnalyticsEventLogger     eventLogger;

    @Inject
    public ExpandEditorAction(Resources resources, CoreLocalizationConstant constant, WorkBenchPresenter workBenchPresenter,
                              AnalyticsEventLogger eventLogger) {
        super(constant.actionExpandEditorTitle(), null, null, resources.fullscreen());
        this.resources = resources;
        this.workBenchPresenter = workBenchPresenter;
        this.constant = constant;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
    }

    @Override
    public Widget createCustomComponent(Presentation presentation) {
        final Element tooltip = DOM.createSpan();
        tooltip.setInnerHTML(constant.actionExpandEditorTitle());

        final SVGToggleButton svgToggleButton = new SVGToggleButton(presentation.getSVGIcon(), null);
        svgToggleButton.addFace(SVGButtonBase.SVGFaceName.DOWN, new SVGButtonBase.SVGFace(new SVGButtonBase.SVGFaceChange[]{
                new SVGButtonBase.SVGStyleChange(new String[]{resources.coreCss().editorFullScreenSvgDown()})}));
        svgToggleButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!svgToggleButton.isDown()) {
                    workBenchPresenter.restoreEditorPart();
                } else {
                    workBenchPresenter.expandEditorPart();
                }
            }
        });

        final FlowPanel flowPanel = new FlowPanel();
        flowPanel.addStyleName(resources.coreCss().editorFullScreen());
        flowPanel.add(svgToggleButton);
        flowPanel.getElement().appendChild(tooltip);
        flowPanel.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                final Element panel = event.getRelativeElement();
                tooltip.getStyle().setProperty("top", (panel.getAbsoluteTop() + panel.getOffsetHeight() + 9) + "px");
                tooltip.getStyle().setProperty("right", (Document.get().getClientWidth() - panel.getAbsoluteRight() - 2) + "px");
            }
        }, MouseOverEvent.getType());

        return flowPanel;
    }
}
