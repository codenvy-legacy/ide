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
package org.exoplatform.ide.client.application;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.api.Panel;
import org.exoplatform.ide.client.framework.ui.api.Perspective;
import org.exoplatform.ide.client.menu.Menu;
import org.exoplatform.ide.client.menu.MenuImpl;
import org.exoplatform.ide.client.ui.StandartPerspective;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEForm extends DockLayoutPanel implements IDEPresenter.Display {

    private StandartPerspective perspective;

    private MenuImpl menu;

    private Toolbar toolbar;

    private Toolbar statusbar;

    public IDEForm() {
        super(Unit.PX);
        DOM.setStyleAttribute(getElement(), "background", "#FFFFFF");
        RootLayoutPanel.get().add(this);

        createMenu();
        createToolbar();
        createStatusbar();
        createPerspective();
    }

    /** Creates Top Menu. */
    private void createMenu() {
        menu = new MenuImpl();
        addNorth(menu, 20);
    }

    /** Creates Toolbar. */
    private void createToolbar() {
        toolbar = new Toolbar("exoIDEToolbar");
        addNorth(toolbar, 32);
    }

    /** Creates Statusbar. */
    private void createStatusbar() {
        statusbar = new Toolbar("exoIDEStatusbar");
        statusbar.setHeight("30px");
        String background = Toolbar.RESOURCES.statusbarBackground().getSafeUri().asString();

        statusbar.setBackgroundImage(background);
        statusbar.setItemsTopPadding(3);
        addSouth(statusbar, 30);
    }

    /** Create Perspective. */
    private void createPerspective() {
        perspective = new StandartPerspective();
        add(perspective);
        Panel navigationPanel = perspective.addPanel("navigation", Direction.WEST, 300);
        navigationPanel.acceptType("navigation");

        Panel informationPanel = perspective.addPanel("information", Direction.EAST, 200);
        informationPanel.acceptType("information");

        Panel operationPanel = perspective.addPanel("operation", Direction.SOUTH, 150);
        operationPanel.acceptType("operation");

        Panel editorPanel = perspective.addPanel("editor", Direction.CENTER, 0);
        editorPanel.acceptType("editor");

        Style editorStyle = editorPanel.asWidget().getElement().getStyle();
        editorStyle.setBackgroundImage("url("+ IDEImageBundle.INSTANCE.noFileOpened().getURL()+")");
        editorStyle.setProperty("backgroundRepeat", "no-repeat");
        editorStyle.setProperty("backgroundPosition", "center center");
    }

    /** @see org.exoplatform.ide.client.application.IDEPresenter.Display#getMenu() */
    @Override
    public Menu getMenu() {
        return menu;
    }

    /** @see org.exoplatform.ide.client.application.IDEPresenter.Display#getPerspective() */
    @Override
    public Perspective getPerspective() {
        return perspective;
    }

    /** @see org.exoplatform.ide.client.application.IDEPresenter.Display#getToolbar() */
    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    /** @see org.exoplatform.ide.client.application.IDEPresenter.Display#getStatusbar() */
    @Override
    public Toolbar getStatusbar() {
        return statusbar;
    }

    /**
     * @see org.exoplatform.ide.client.application.IDEPresenter.Display#setContextMenuHandler(com.google.gwt.event.dom.client
     *      .ContextMenuHandler)
     */
    @Override
    public void setContextMenuHandler(ContextMenuHandler handler) {
        RootLayoutPanel.get().addDomHandler(handler, ContextMenuEvent.getType());
    }
}
