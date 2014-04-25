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
package com.codenvy.ide.part.console;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.workspace.WorkBenchPartControllerImpl;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Template for the Console View Part.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class ConsolePartPresenter extends BasePresenter implements ConsolePartView.ActionDelegate, ConsolePart {
    private static final String TITLE = "Console";
    private ConsolePartView view;

    /** Construct empty Part */
    @Inject
    public ConsolePartPresenter(ConsolePartView view) {
        this.view = view;
        this.view.setTitle(TITLE);
        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return TITLE;
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "Displays console output";
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    private void performPostOutputActions() {
        PartPresenter activePart = partStack.getActivePart();
        if (activePart == null || !activePart.equals(this)) {
            partStack.setActivePart(this);
        }
        view.scrollBottom();
    }

    /**
     * Print text on console.
     *
     * @param text
     *         text that need to be shown
     */
    public void print(String text) {
        String []lines = text.split("\n");
        for (String line : lines) {
            view.print(line);
        }
        performPostOutputActions();
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        view.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void onClearClicked() {
        clear();
    }
    
    /** {@inheritDoc} */
    @Override
    public void onOpen() {
        super.onOpen();
        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

            @Override
            public boolean execute() {
                view.scrollBottom();
                return false;
            }
        }, WorkBenchPartControllerImpl.DURATION);
    }

    @Override
    public void displayException(Exception e) {
    }

    @Override
    public void displayMsgInfo(String text) {
    }

    @Override
    public void displayMsgError(String text) {
    }

    @Override
    public void displayMsgWarn(String text) {
    }

}