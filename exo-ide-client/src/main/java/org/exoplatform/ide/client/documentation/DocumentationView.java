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
package org.exoplatform.ide.client.documentation;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: DocumentationForm Jan 21, 2011 12:23:13 PM evgen $
 */
public class DocumentationView extends ViewImpl implements DocumentationPresenter.Display {

    private static final String ID = "ideDocumentationView";

    private static final String FRAME_ID = "ideDocumentationFrame";

    private static final Image DOCUMENTATION_TAB_ICON = new Image(IDEImageBundle.INSTANCE.documentation());

    private Frame iFrame;

    public DocumentationView() {
        super(ID, ViewType.INFORMATION, IDE.IDE_LOCALIZATION_CONSTANT.documentationTitle());
        setIcon(DOCUMENTATION_TAB_ICON);

        iFrame = new Frame();
        DOM.setElementAttribute(iFrame.getElement(), "scrolling", "no");
        DOM.setElementAttribute(iFrame.getElement(), "frameborder", "0");
        DOM.setElementAttribute(iFrame.getElement(), "style", "overflow:visible");
        iFrame.setStyleName("");
        iFrame.setWidth("100%");
        iFrame.setHeight("100%");
        iFrame.ensureDebugId(FRAME_ID);
        add(iFrame);
        setWidth("100%");
    }

    /** @see org.exoplatform.ide.client.documentation.DocumentationPresenter.Display#setDocumentationURL(java.lang.String) */
    @Override
    public void setDocumentationURL(String url) {
        iFrame.setUrl(url);
    }
}
