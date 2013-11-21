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
package com.codenvy.ide.tutorial.wizard.newproject.pages.template;

import com.google.inject.Provider;

/**
 * The provider for a template wizard page.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class TemplatePageProvider implements Provider<TemplatePage> {
    private String caption;
    private String templateID;

    public TemplatePageProvider(String caption, String templateID) {
        this.caption = caption;
        this.templateID = templateID;
    }

    /** {@inheritDoc} */
    @Override
    public TemplatePage get() {
        return new TemplatePage(caption, templateID);
    }
}