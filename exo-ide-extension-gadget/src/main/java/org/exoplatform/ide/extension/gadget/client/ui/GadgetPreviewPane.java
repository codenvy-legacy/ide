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
package org.exoplatform.ide.extension.gadget.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;

import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.ui.PreviewFrame;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.gadget.shared.GadgetMetadata;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GadgetPreviewPane extends ViewImpl {

    /**
     *
     */
    public static final String TITLE = "Preview";

    private String meta;

    private GadgetMetadata metadata;

    /**
     *
     */
    public static final String ID = "gadgetpreview";

    private IDEConfiguration configuration;

    private FlowPanel framePanel;

    private String gadgetUrl;

    private String gadgetServer;

    public GadgetPreviewPane(String href, String gadgetServer) {
        super(ID, ViewType.OPERATION, TITLE);
        this.gadgetUrl = href;
        this.gadgetServer = gadgetServer;
        framePanel = new FlowPanel();
        framePanel.setSize("100%", "100%");
        add(framePanel);
    }

    /** Create iframe. Gadget will be load here. */
    public void showGadget() {
        String url = gadgetServer //
                     + "samplecontainer/samplecontainer.html?url=" + gadgetUrl;
        final PreviewFrame frame = new PreviewFrame(url);
        DOM.setElementAttribute(frame.getElement(), "scrolling", "no");
        DOM.setElementAttribute(frame.getElement(), "frameborder", "0");
        frame.setWidth("100%");
        frame.setHeight("100%");
        frame.setStyleName("");
        frame.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                setHandler(IFrameElement.as(frame.getElement()));
            }
        });

        framePanel.clear();
        framePanel.add(frame);
    }

    private native void setHandler(Element e)/*-{
        var type = "mousedown";
        var instance = this;
        if (typeof e.contentDocument != "undefined") {
            e.contentDocument
                .addEventListener(
                type,
                function () {
                    instance.@org.exoplatform.ide.extension.gadget.client.ui.GadgetPreviewPane::activate()();
                }, false);
        } else if (typeof e.contentWindow != "undefined") {
            e.contentWindow.document
                .attachEvent(
                "on" + type,
                function () {
                    instance.@org.exoplatform.ide.extension.gadget.client.ui.GadgetPreviewPane::activate()();
                });
        }
    }-*/;

    public String getId() {
        return ID;
    }

}
