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

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.CustomComponentAction;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.workspace.WorkBenchPresenter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

    private Resources resources;
    private WorkBenchPresenter workBenchPresenter;

    @Inject
    public ExpandEditorAction(Resources resources, CoreLocalizationConstant constant, WorkBenchPresenter workBenchPresenter) {
        super(constant.actionExpandEditorTitle(), constant.actionExpandEditorTitle(), null, resources.fullscreen());
        this.resources = resources;
        this.workBenchPresenter = workBenchPresenter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public Widget createCustomComponent(Presentation presentation) {
        final SVGToggleButton svgToggleButton = new SVGToggleButton(presentation.getSVGIcon(), null);
        svgToggleButton.setClassNameBaseVal(resources.coreCss().editorFullScreenSvg());
        svgToggleButton.addFace(SVGButtonBase.SVGFaceName.DOWN, new SVGButtonBase.SVGFace(new SVGButtonBase.SVGFaceChange[]{
                new SVGButtonBase.SVGStyleChange(new String[]{resources.coreCss().editorFullScreenSvgDown()})}));
        svgToggleButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(!svgToggleButton.isDown()){
                    workBenchPresenter.restoreEditorPart();
                } else{
                    workBenchPresenter.expandEditorPart();
                }
            }
        });
        return svgToggleButton;
    }
}
