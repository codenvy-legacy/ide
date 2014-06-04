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
package com.codenvy.ide.wizard.newproject.step;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Evgen Vidolob
 */
public class Step implements IsWidget {

    private static StepUiBinder ourUiBinder = GWT.create(StepUiBinder.class);
    private final HTMLPanel rootElement;
    @UiField
    DivElement  stepNum;
    @UiField
    SpanElement text;

    public Step(String title, int stepNumber) {
        rootElement = ourUiBinder.createAndBindUi(this);
        stepNum.setInnerText(String.valueOf(stepNumber));
        text.setInnerText(title);
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    interface StepUiBinder extends UiBinder<HTMLPanel, Step> {
    }
}