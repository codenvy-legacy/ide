/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
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
package com.codenvy.ide.wizard;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Contains of resources wizards view.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface WizardResource extends ClientBundle {
    public interface WizardCSS extends CssResource {
        String mainFont();

        String mainBoldFont();

        String errorFont();

        String greyFontColor();

        String cursorPointer();

        String line();
    }

    @Source({"Wizard.css", "com/codenvy/ide/api/ui/style.css"})
    WizardCSS wizardCSS();

    @Source("new_project_icon.png")
    ImageResource newProjectIcon();

    @Source("new_project_icon.png")
    ImageResource newResourceIcon();

    @Source("new_resource_icon.png")
    ImageResource templateIcon();

    @Source("back.png")
    ImageResource back();

    @Source("next.png")
    ImageResource next();

    @Source("cancel.png")
    ImageResource cancel();

    @Source("ok.png")
    ImageResource ok();

    @Source("question.png")
    ImageResource question();
}