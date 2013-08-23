/*
 * Copyright (C) 2011 eXo Platform SAS.
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
 */
package org.exoplatform.ide.client.application;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.ui.client.command.ui.ToolbarBuilder;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.menu.CloseMenuHandler;
import org.exoplatform.ide.client.editor.EditorView;
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuEvent;
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.Perspective;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.menu.ContextMenu;
import org.exoplatform.ide.client.menu.Menu;
import org.exoplatform.ide.client.menu.RefreshMenuEvent;
import org.exoplatform.ide.client.menu.RefreshMenuHandler;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEPresenter implements RefreshMenuHandler, ViewOpenedHandler, ViewClosedHandler,
                                     ViewVisibilityChangedHandler, ClosingViewHandler, ViewActivatedHandler, ShowContextMenuHandler {

    public interface Display {
        Menu getMenu();

        Toolbar getToolbar();

        Toolbar getStatusbar();

        Perspective getPerspective();

        void setContextMenuHandler(ContextMenuHandler handler);
    }

    private Display display;

    private ControlsRegistration controlsRegistration;

    private View activeView;

    public IDEPresenter(Display display, final ControlsRegistration controlsRegistration) {
        this.display = display;
        this.controlsRegistration = controlsRegistration;

        IDE.addHandler(RefreshMenuEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
        IDE.addHandler(ShowContextMenuEvent.TYPE, this);

        display.getPerspective().addViewOpenedHandler(this);
        display.getPerspective().addClosingViewHandler(this);
        display.getPerspective().addViewClosedHandler(this);
        display.getPerspective().addViewVisibilityChangedHandler(this);

        display.setContextMenuHandler(contextMenuHandler);

        new ToolbarBuilder(IDE.eventBus(), display.getToolbar(), display.getStatusbar());
        new VirtualFileSystemSwitcher();
        new CloseViewsOnEscapePressedHandler();

        new Timer() {
            @Override
            public void run() {
                try {
                    DOM.getElementById("ide-preloader").removeFromParent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                new Timer() {
                    @Override
                    public void run() {
                        try {
                            new SessionKeepAlive();
                            new IDEConfigurationInitializer(controlsRegistration).loadConfiguration();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.schedule(500);
            }
        }.schedule(1000);
    }

    public void openView(View view) {
        display.getPerspective().openView(view);
    }

    public void closeView(String viewId) {
        display.getPerspective().closeView(viewId);
    }

    @Override
    public void onRefreshMenu(RefreshMenuEvent event) {
        display.getMenu().refresh(controlsRegistration.getRegisteredControls());
    }

    @Override
    public void onViewOpened(ViewOpenedEvent event) {
        IDE.fireEvent(event);
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        IDE.fireEvent(event);
    }

    @Override
    public void onViewVisibilityChanged(ViewVisibilityChangedEvent event) {
        IDE.fireEvent(event);
    }

    @Override
    public void onClosingView(ClosingViewEvent event) {
        IDE.fireEvent(event);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler#onViewActivated(org.exoplatform.ide.client.framework
     * .ui.api.event.ViewActivatedEvent) */
    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        this.activeView = event.getView();
    }

    /** @see org.exoplatform.ide.client.framework.event.ShowContextMenuHandler#onShowContextMenu(org.exoplatform.ide.client.framework
     * .event.ShowContextMenuEvent) */
    @Override
    public void onShowContextMenu(ShowContextMenuEvent event) {
        final int x = event.getX();
        final int y = event.getY();

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                ContextMenu.get().show(controlsRegistration.getRegisteredControls(), x, y, popupMenuCloseHandler);
            }
        });
    }

    private final ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {
        @Override
        public void onContextMenu(final ContextMenuEvent event) {
            if (activeView == null || !activeView.canShowContextMenu()) {
                return;
            }
            final int x = event.getNativeEvent().getClientX();
            final int y = event.getNativeEvent().getClientY();

            if (x < activeView.asWidget().getAbsoluteLeft()
                || x > (activeView.asWidget().getAbsoluteLeft() + activeView.asWidget().getOffsetWidth())
                || y < activeView.asWidget().getAbsoluteTop()
                || y > (activeView.asWidget().getAbsoluteTop() + activeView.asWidget().getOffsetHeight())) {
                return;
            }

            event.stopPropagation();
            event.preventDefault();

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    ContextMenu.get().show(controlsRegistration.getRegisteredControls(), x, y, popupMenuCloseHandler);
                }
            });
        }
    };

    private CloseMenuHandler popupMenuCloseHandler = new CloseMenuHandler() {
        @Override
        public void onCloseMenu() {
            if (activeView instanceof EditorView) {
                ((EditorView)activeView).getEditor().setFocus();
            } else
                activeView.asWidget().getElement().focus();
        }
    };

}
