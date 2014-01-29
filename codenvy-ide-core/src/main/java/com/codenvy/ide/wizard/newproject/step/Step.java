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