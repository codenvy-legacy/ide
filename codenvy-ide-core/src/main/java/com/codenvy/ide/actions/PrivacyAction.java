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

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.CustomComponentAction;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;


/**
 * Action to display project privacy status.
 *
 * @author Kevin Pollet
 */
@Singleton
public class PrivacyAction extends Action implements CustomComponentAction {

    private final CoreLocalizationConstant coreLocalizationConstant;
    private final Resources                resources;
    private final AppContext               appContext;
    private final FlowPanel                container;
    private final Element                  tooltipHeader;
    private final Element                  tooltipMessage;
    private       String                   previousVisibility;

    @Inject
    public PrivacyAction(CoreLocalizationConstant coreLocalizationConstant, AppContext appContext, Resources resources) {

        this.coreLocalizationConstant = coreLocalizationConstant;
        this.appContext = appContext;
        this.resources = resources;
        this.container = new FlowPanel();
        this.tooltipHeader = DOM.createDiv();
        this.tooltipMessage = DOM.createDiv();

        tooltipHeader.appendChild(DOM.createSpan());

        final Element tooltip = DOM.createDiv();
        tooltip.appendChild(tooltipHeader);
        tooltip.appendChild(tooltipMessage);
        tooltip.setClassName(resources.coreCss().privacyTooltip());

        container.addStyleName(resources.coreCss().privacy());
        container.getElement().appendChild(tooltip);
        container.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                final Element action = event.getRelativeElement();
                tooltip.getStyle().setProperty("top", (action.getOffsetHeight() + 13) + "px");
                tooltip.getStyle().setProperty("right", ((Document.get().getClientWidth() - action.getAbsoluteRight()) - 47) + "px");
            }
        }, MouseOverEvent.getType());
    }

    @Override
    public Widget createCustomComponent(Presentation presentation) {
        return container;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void update(ActionEvent e) {
        final CurrentProject project = appContext.getCurrentProject();
        if (project == null) {
            e.getPresentation().setVisible(false);
        } else {
            final ProjectDescriptor projectDescriptor = project.getProjectDescription();
            if (!projectDescriptor.getVisibility().equals(previousVisibility)) {
                final boolean isPrivate = "private".equals(projectDescriptor.getVisibility());
                final SVGResource icon = isPrivate ? resources.privateProject() : resources.publicProject();

                container.removeStyleName(
                        isPrivate ? resources.coreCss().publicProjectSvg() : resources.coreCss().privateProjectSvg());
                container.addStyleName(
                        isPrivate ? resources.coreCss().privateProjectSvg() : resources.coreCss().publicProjectSvg());

                if (container.getWidgetCount() > 0) {
                    container.remove(0);
                }
                container.insert(new SVGImage(icon), 0);

                final Element tooltipHeaderText = DOM.createSpan();
                tooltipHeaderText.setInnerHTML(
                        isPrivate ? coreLocalizationConstant.privacyTooltipPrivateHeader()
                                  : coreLocalizationConstant.privacyTooltipPublicHeader());

                tooltipHeader.removeAllChildren();
                tooltipHeader.appendChild(new SVGImage(icon).getElement());
                tooltipHeader.appendChild(tooltipHeaderText);
                tooltipMessage.setInnerHTML(
                        isPrivate ? coreLocalizationConstant.privacyTooltipPrivateMessage()
                                  : coreLocalizationConstant.privacyTooltipPublicMessage());

                e.getPresentation().setVisible(true);
                previousVisibility = projectDescriptor.getVisibility();
            }
        }
    }
}
