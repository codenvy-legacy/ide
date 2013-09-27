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
package org.exoplatform.ide.extension.heroku.client.stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.StackListAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.StackMigrationAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.StackMigrationResponse;
import org.exoplatform.ide.extension.heroku.shared.Stack;
import org.exoplatform.ide.git.client.GitPresenter;

import java.util.List;

/**
 * Presenter for changing Heroku application's stack.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 29, 2011 9:41:46 AM anya $
 */
public class ChangeStackPresenter extends GitPresenter implements ViewClosedHandler, ChangeApplicationStackHandler,
                                                                  LoggedInHandler {
    interface Display extends IsView {
        ListGridItem<Stack> getStackGrid();

        HasClickHandlers getChangeButton();

        HasClickHandlers getCancelButton();

        void enableChangeButton(boolean enable);

        Stack getSelectedStack();
    }

    private String herokuApplication = null;

    /** Presenter's view. */
    private Display display;

    /**
     *
     */
    public ChangeStackPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ChangeApplicationStackEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getStackGrid().addSelectionHandler(new SelectionHandler<Stack>() {

            @Override
            public void onSelection(SelectionEvent<Stack> event) {
                display.enableChangeButton(event.getSelectedItem() != null && !event.getSelectedItem().isCurrent());
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getChangeButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doMigrateStack();
            }
        });
    }

    /** @see org.exoplatform.ide.extension.heroku.client.stack.ChangeApplicationStackHandler#onChangeApplicationStack(org.exoplatform.ide
     * .extension.heroku.client.stack.ChangeApplicationStackEvent) */
    @Override
    public void onChangeApplicationStack(ChangeApplicationStackEvent event) {
        herokuApplication = event.getApplication();

        if (event.getApplication() != null && !event.getApplication().isEmpty()) {
            getStacks();
            return;
        }

        if (makeSelectionCheck()) {
            getStacks();
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

    public void getStacks() {
        //String projectId = (herokuApplication == null) ? ((ItemContext)selectedItems.get(0)).getProject().getId() : null;
        String projectId = (herokuApplication == null) ? getSelectedProject().getId() : null;
        try {
            HerokuClientService.getInstance().getStackList(herokuApplication, vfs.getId(), projectId,
                                                           new StackListAsyncRequestCallback(this) {
                                                               @Override
                                                               protected void onSuccess(List<Stack> result) {
                                                                   if (display == null) {
                                                                       display = GWT.create(Display.class);
                                                                       bindDisplay();
                                                                       IDE.getInstance().openView(display.asView());
                                                                   }
                                                                   display.enableChangeButton(false);
                                                                   display.getStackGrid().setValue(result, true);
                                                               }
                                                           });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client
     * .login.LoggedInEvent) */
    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        if (!event.isFailed()) {
            getStacks();
        }
    }

    /** Perform stack migration. */
    public void doMigrateStack() {
        Stack stack = display.getSelectedStack();
        if (stack == null) {
            return;
        }

//      String projectId = (herokuApplication == null) ? ((ItemContext)selectedItems.get(0)).getProject().getId() : null;
        String projectId = (herokuApplication == null) ? getSelectedProject().getId() : null;
        try {
            HerokuClientService.getInstance().migrateStack(herokuApplication, vfs.getId(), projectId, stack.getName(),
                                                           new StackMigrationAsyncRequestCallback(this) {

                                                               @Override
                                                               protected void onSuccess(StackMigrationResponse result) {
                                                                   IDE.getInstance().closeView(display.asView().getId());
                                                                   String output =
                                                                           (result.getResult() != null && !result.getResult().isEmpty())
                                                                           ? result.getResult().replace("\n",
                                                                                                        "<br>") : "";
                                                                   IDE.fireEvent(new OutputEvent(output, Type.INFO));
                                                               }
                                                           });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

}
