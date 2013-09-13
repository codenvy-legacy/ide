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
package org.exoplatform.ide.client.progress.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.job.Job;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 19, 2011 evgen $
 */
public class JobWidget extends Composite {
    private static final String EVEN_BACKGROUND = "#FFFFFF";

    private static final String ODD_BACKGROUND = "#f8f8f8";

    private static final String OVER_BACKGROUND = "#D9E8FF";

    private Grid body;

    private String backgroundColor;

    private Image image = new Image();

    private HTML message = new HTML();

    /**
     *
     */
    protected JobWidget(boolean odd) {
        body = new Grid(1, 2);
        body.setWidget(0, 0, image);
        body.setWidget(0, 1, message);
        body.getCellFormatter().setWidth(0, 0, "16px");
        body.getCellFormatter().getElement(0, 0).getStyle().setPaddingTop(5.0, Style.Unit.PX);
        body.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        body.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);

        initWidget(body);

        if (odd) {
            backgroundColor = ODD_BACKGROUND;
        } else {
            backgroundColor = EVEN_BACKGROUND;
        }

        setBackgroundColor(backgroundColor);
        setWidth("100%");
        body.addDomHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                setBackgroundColor(backgroundColor);
            }
        }, MouseOutEvent.getType());

        body.addDomHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(MouseOverEvent event) {
                setBackgroundColor(OVER_BACKGROUND);
            }
        }, MouseOverEvent.getType());
    }

    /**
     *
     */
    public JobWidget(Job job, boolean odd) {
        this(odd);
        updateJob(job);
    }

    /** @param job */
    public final void updateJob(Job job) {
        switch (job.getStatus()) {
            case STARTED:
                image.setResource(IDEImageBundle.INSTANCE.asyncRequest());
                message.getElement().setInnerHTML(job.getStartMessage());
                break;

            case FINISHED:
                image.setResource(IDEImageBundle.INSTANCE.ok());
                message.getElement().setInnerHTML(job.getFinishMessage());
                break;
            case ERROR:
                image.setResource(IDEImageBundle.INSTANCE.cancel());
                message.setHTML(new SafeHtmlBuilder().appendHtmlConstant("<pre>" + job.getError().getMessage() + "</pre>").toSafeHtml());
                message.getElement().getStyle().setColor("#880000");
                break;
        }
    }

    /** @param backgroundColor */
    private void setBackgroundColor(String backgroundColor) {
        DOM.setStyleAttribute(getElement(), "background", backgroundColor);
    }
}
