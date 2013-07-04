/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
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
