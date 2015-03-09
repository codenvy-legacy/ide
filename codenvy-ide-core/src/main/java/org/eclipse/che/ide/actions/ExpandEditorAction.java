/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.actions;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.workspace.WorkBenchPresenter;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.CustomComponentAction;
import org.eclipse.che.ide.api.action.Presentation;

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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class ExpandEditorAction extends Action implements CustomComponentAction {

    private final Resources                resources;
    private final WorkBenchPresenter       workBenchPresenter;
    private final CoreLocalizationConstant constant;
    private final AnalyticsEventLogger     eventLogger;

    private boolean               expanded      = false;
    private List<SVGToggleButton> toggleButtons = new ArrayList<>();

    @Inject
    public ExpandEditorAction(Resources resources, CoreLocalizationConstant constant, WorkBenchPresenter workBenchPresenter,
                              AnalyticsEventLogger eventLogger) {
        super(constant.actionExpandEditorTitle(), null, null, resources.fullscreen());
        this.resources = resources;
        this.workBenchPresenter = workBenchPresenter;
        this.constant = constant;
        this.eventLogger = eventLogger;

        setExpandEditorEventHandler();
    }

    /**
     * Using native functions helps us to bind different components of IDE each other.
     */
    private native void setExpandEditorEventHandler() /*-{
        var instance = this;
        $wnd.IDE.eventHandlers.expandEditor = function () {
            instance.@org.eclipse.che.ide.actions.ExpandEditorAction::expandEditor()();
        };
    }-*/;


    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
    }

    @Override
    public Widget createCustomComponent(Presentation presentation) {
        final Element tooltip = DOM.createSpan();
        tooltip.setInnerHTML(constant.actionExpandEditorTitle());

        final SVGToggleButton svgToggleButton = new SVGToggleButton(presentation.getSVGIcon(), null);
        toggleButtons.add(svgToggleButton);
        svgToggleButton.addFace(SVGButtonBase.SVGFaceName.DOWN, new SVGButtonBase.SVGFace(new SVGButtonBase.SVGFaceChange[]{
                new SVGButtonBase.SVGStyleChange(new String[]{resources.coreCss().editorFullScreenSvgDown()})}));
        svgToggleButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                expandEditor();
            }
        });

        FlowPanel widgetPanel = new FlowPanel();
        widgetPanel.addStyleName(resources.coreCss().editorFullScreen());
        widgetPanel.add(svgToggleButton);
        widgetPanel.getElement().appendChild(tooltip);
        widgetPanel.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                final Element panel = event.getRelativeElement();
                tooltip.getStyle().setProperty("top", (panel.getAbsoluteTop() + panel.getOffsetHeight() + 9) + "px");
                tooltip.getStyle().setProperty("right", (Document.get().getClientWidth() - panel.getAbsoluteRight() - 2) + "px");
            }
        }, MouseOverEvent.getType());

        return widgetPanel;
    }

    /**
     * Handles the clicking on Expand button and expands or restores the editor.
     */
    public void expandEditor() {
        if (expanded) {
            workBenchPresenter.restoreEditorPart();
            for (SVGToggleButton toggleButton : toggleButtons) {
                toggleButton.setDown(false);
            }
            expanded = false;
        } else {
            workBenchPresenter.expandEditorPart();
            for (SVGToggleButton toggleButton : toggleButtons) {
                toggleButton.setDown(true);
            }
            expanded = true;
        }
    }

}
