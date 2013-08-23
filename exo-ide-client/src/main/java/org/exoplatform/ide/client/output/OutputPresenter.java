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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputHandler;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OutputPresenter implements OutputHandler, ViewClosedHandler, ShowOutputHandler {

    public interface Display extends IsView {

        void clearOutput();

        void outMessage(OutputMessage message);

        HasClickHandlers getClearOutputButton();

    }

    private Display display;

    private List<OutputMessage> messages = new ArrayList<OutputMessage>();

    public OutputPresenter() {
        IDE.addHandler(OutputEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ShowOutputEvent.TYPE, this);
        IDE.getInstance().addControl(new ShowOutputCommand(), Docking.NONE);
    }

    public void bindDisplay() {
        display.getClearOutputButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                messages.clear();
                display.clearOutput();
            }
        });

        for (OutputMessage message : messages) {
            display.outMessage(message);
        }
    }

    public void onOutput(OutputEvent event) {
        try {
            openView();

            OutputMessage message = new OutputMessage(event.getMessage(), event.getOutputType());
            if (message.getType() == OutputMessage.Type.LOG) {
                return;
            }

            messages.add(message);
            display.outMessage(message);
        } catch (Exception e) {
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.client.output.ShowOutputHandler#onShowOutput(org.exoplatform.ide.client.output.ShowOutputEvent) */
    @Override
    public void onShowOutput(ShowOutputEvent event) {
        openView();
    }

    /**
     *
     */
    private void openView() {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView((View)display);
            bindDisplay();
        } else {
            ((View)display).setViewVisible();
        }

    }

}
