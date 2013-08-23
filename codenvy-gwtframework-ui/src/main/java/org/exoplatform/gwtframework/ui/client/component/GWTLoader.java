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

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.ui.client.GwtResources;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTLoader extends Loader {
    private static final String LOADER_ID = "GWTLoaderId";

    private PopupPanel loader;

    private FlowPanel loaderContent;

    private Element messageElement;

    /**
     *
     */
    public GWTLoader() {
        GwtResources.INSTANCE.css().ensureInjected();
        String loadingMessage = getMessage();
        loaderContent = new FlowPanel();
        loaderContent.getElement().setId(LOADER_ID);

        loaderContent.setStyleName(GwtResources.INSTANCE.css().loaderCenteredContent());

        Image image = new Image(GwtResources.INSTANCE.loader());
        image.setStyleName(GwtResources.INSTANCE.css().loaderImage());

        messageElement = DOM.createSpan();
        messageElement.setInnerHTML(loadingMessage);

        loaderContent.add(image);
        loaderContent.getElement().appendChild(com.google.gwt.dom.client.Document.get().createBRElement());
        loaderContent.getElement().appendChild(messageElement);

        loader = new PopupPanel();
        loader.setWidget(loaderContent);
        loader.setStyleName(GwtResources.INSTANCE.css().loaderBackground());
    }

    /** @see org.exoplatform.gwtframework.commons.rest.AsyncRequestLoader#show() */
    public void show() {
        loader.show();
        String message = getMessage();
        messageElement.setInnerHTML(message);
        DOM.setStyleAttribute(loader.getElement(), "zIndex", (Integer.MAX_VALUE - 1) + "");
    }

    /** @see org.exoplatform.gwtframework.commons.rest.AsyncRequestLoader#hide() */
    public void hide() {
        loader.hide();
    }

}
