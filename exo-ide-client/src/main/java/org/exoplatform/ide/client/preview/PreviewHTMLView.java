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
package org.exoplatform.ide.client.preview;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.PreviewFrame;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PreviewHTMLView extends ViewImpl implements
                                              org.exoplatform.ide.client.preview.PreviewHTMLPresenter.Display {

    /** ID of Preview View */
    private static final String ID = "idePreviewHTMLView";

    private static final int DEFAULT_WIDTH = 500;

    private static final int DEFAULT_HEIGHT = 300;

    private FlowPanel previewPanel;

    private HTML previewDisabledHTML;

    private PreviewFrame previewFrame;

    private boolean previewAvailable = true;

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.previewHtmlTitle();

    public PreviewHTMLView() {
        super(ID, ViewType.OPERATION, TITLE, new Image(IDEImageBundle.INSTANCE.preview()), DEFAULT_WIDTH, DEFAULT_HEIGHT);

        previewPanel = new FlowPanel();
        if (ViewType.POPUP.equals(getType())) {
            previewPanel.setSize("100%", "100%");
            add(previewPanel);
        } else {
            add(previewPanel);
        }

        previewDisabledHTML = new HTML();
        previewDisabledHTML.setSize("100%", "100%");
        previewDisabledHTML.setVisible(false);
        previewPanel.add(previewDisabledHTML);

        previewFrame = new PreviewFrame();
        previewFrame.setSize("100%", "100%");
        DOM.setElementAttribute(previewFrame.getElement(), "frameborder", "0");
        DOM.setStyleAttribute(previewFrame.getElement(), "border", "none");

        previewFrame.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                setHandler(IFrameElement.as(previewFrame.getElement()));
            }
        });
        previewPanel.add(previewFrame);
    }

    private native void setHandler(Element e)/*-{
        var type = "mousedown";
        var instance = this;
        if (typeof e.contentDocument != "undefined") {
            e.contentDocument.addEventListener(type,
                function () {
                    instance.@org.exoplatform.ide.client.preview.PreviewHTMLView::activate()();
                },
                false);
        }
        else {
            e.contentWindow.document.attachEvent("on" + type,
                function () {
                    instance.@org.exoplatform.ide.client.preview.PreviewHTMLView::activate()();
                }
            );
        }
    }-*/;

    @Override
    public void showPreview(String url) {
        previewFrame.setUrl(url);
    }

    @Override
    public void setPreviewAvailable(boolean available) {
        if (previewAvailable == available) {
            return;
        }
        previewAvailable = available;

        if (available) {
            previewDisabledHTML.setVisible(false);
            previewFrame.setVisible(true);
        } else {
            previewFrame.setVisible(false);
            previewDisabledHTML.setVisible(true);
        }
    }

    @Override
    public void setMessage(String message) {
        String html =
                "<table style=\"width:100%; height:100%;\"><tr style=\"vertical-align:top;\"><td style=\"text-align:center;\">"
                + message + "</td></tr></table>";
        previewDisabledHTML.setHTML(html);
    }

}
