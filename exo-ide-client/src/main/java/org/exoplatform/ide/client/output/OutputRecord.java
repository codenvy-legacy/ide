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
package org.exoplatform.ide.client.output;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;

import org.exoplatform.ide.client.framework.output.event.OutputMessage;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OutputRecord extends HTML implements MouseOutHandler, MouseOverHandler {

    private static final String LOG_COLOR = "#000077";

    private static final String INFO_COLOR = "#007700";

    private static final String GIT_COLOR = "#000000";
    
    private static final String JREBEL_COLOR = "#000000";

    private static final String WARNING_COLOR = "#AA0077";

    private static final String ERROR_COLOR = "#880000";

    private static final String OUTPUT_COLOR = "#000088";

    private static final String EVEN_BACKGROUND = "#FFFFFF";

    // private static final String ODD_BACKGROUND = "#ff9999";
    private static final String ODD_BACKGROUND = "#f8f8f8";

    private static final String OVER_BACKGROUND = "#D9E8FF";

    private String backgroundColor;

    public OutputRecord(OutputMessage message, boolean odd) {
        if (message.getType() == OutputMessage.Type.LOG) {
            setContents("<font color=\"" + LOG_COLOR + "\">[" + OutputMessage.Type.LOG.name() + "] "
                        + message.getMessage() + "</font>");
        } else if (message.getType() == OutputMessage.Type.INFO) {
            setContents("<font color=\"" + INFO_COLOR + "\">[" + OutputMessage.Type.INFO.name() + "] "
                        + message.getMessage() + "</font>");
        } else if (message.getType() == OutputMessage.Type.WARNING) {
            setContents("<font color=\"" + WARNING_COLOR + "\">[" + OutputMessage.Type.WARNING.name() + "] "
                        + message.getMessage() + "</font>");
        } else if (message.getType() == OutputMessage.Type.ERROR) {
            setContents("<font color=\"" + ERROR_COLOR + "\">[" + OutputMessage.Type.ERROR.name() + "] "
                        + message.getMessage() + "</font>");
        } else if (message.getType() == OutputMessage.Type.OUTPUT) {
            setContents("<font color=\"" + OUTPUT_COLOR + "\">[" + OutputMessage.Type.OUTPUT.name() + "] "
                        + message.getMessage() + "</font>");
        } else if (message.getType() == OutputMessage.Type.GIT) {
            setContents("<font color=\"" + GIT_COLOR + "\">"
                        + message.getMessage().replace("\n", "<br>").replace(" ", "&nbsp;") + "</font>");
        } else if (message.getType() == OutputMessage.Type.JRebel) {
            setContents("<font color=\"" + JREBEL_COLOR + "\">[" + OutputMessage.Type.JRebel.name() + "] "
                        + message.getMessage() + "</font>");
        }

        if (odd) {
            backgroundColor = ODD_BACKGROUND;
        } else {
            backgroundColor = EVEN_BACKGROUND;
        }

        setBackgroundColor(backgroundColor);
        setWidth("100%");
        addMouseOverHandler(this);
        addMouseOutHandler(this);
    }

    /** @param string */
    private void setContents(String html) {
        String table =
                "<table cellpadding=\"3\" cellspacing=\"0\" border=\"0\"><tr>" + "<td style=\""
                + "font-family: Verdana,Bitstream Vera Sans,sans-serif;" + "font-size: 11px;" + "font-style: normal;"
                + "font-weight: normal" + "\">" + html + "</td></tr></table>";
        getElement().setInnerHTML(table);
    }

    /** @param backgroundColor */
    private void setBackgroundColor(String backgroundColor) {
        DOM.setStyleAttribute(getElement(), "background", backgroundColor);
    }

    /** @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent) */
    @Override
    public void onMouseOver(MouseOverEvent event) {
        setBackgroundColor(OVER_BACKGROUND);
    }

    /** @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent) */
    @Override
    public void onMouseOut(MouseOutEvent event) {
        setBackgroundColor(backgroundColor);
    }

}
