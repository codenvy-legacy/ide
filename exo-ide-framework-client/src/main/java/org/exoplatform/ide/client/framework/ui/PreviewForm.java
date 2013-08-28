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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PreviewForm extends ViewImpl {

    public static final String ID = "Preview";

    private PreviewFrame frame;

    /** @param eventBus */
    public PreviewForm() {
        super(ID, ViewType.OPERATION, ID);
        // setHeight100();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return "Preview";
    }

    /**
     * @param file
     * @return
     */
    public void showPreview(String href) {
        if (frame != null) {
            frame.removeFromParent();
        }
        frame = new PreviewFrame();
        frame.setUrl(href);
        DOM.setElementAttribute(frame.getElement(), "frameborder", "0");

        frame.getElement().setId("eXo-IDE-preview-frame");
        frame.getElement().setAttribute("name", "eXo-IDE-preview-frame");
        frame.setStyleName("");
        frame.setWidth("100%");
        frame.setHeight("100%");

        frame.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                setHandler(IFrameElement.as(frame.getElement()));
            }
        });
        add(frame);
    }

    private native void setHandler(Element e)/*-{
        var type = "mousedown";
        var instance = this;
        if (typeof e.contentDocument != "undefined") {
            e.contentDocument.addEventListener(type, function () {
                instance.@org.exoplatform.ide.client.framework.ui.PreviewForm::activate()();
            }, false);
        }
        else {
            e.contentWindow.document.attachEvent("on" + type, function () {
                instance.@org.exoplatform.ide.client.framework.ui.PreviewForm::activate()();
            });
        }
    }-*/;

    public String getId() {
        return ID;
    }

}
