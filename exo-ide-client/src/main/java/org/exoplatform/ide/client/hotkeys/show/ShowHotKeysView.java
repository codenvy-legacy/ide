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
package org.exoplatform.ide.client.hotkeys.show;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * View for show keyboard shortcuts.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowHotKeysView.java May 10, 2012 11:28:21 AM azatsarynnyy $
 */

public class ShowHotKeysView extends ViewImpl implements ShowHotKeysPresenter.Display {

    /** View's identifier. */
    public static final String ID = "ideShowHotKeysView";

    /** View's title. */
    private static final String TITLE = IDE.PREFERENCES_CONSTANT.showHotKeysTitle();

    /** Initial width of this view */
    private static final int WIDTH = 640;

    /** Initial height of this view */
    private static final int HEIGHT = 300;

    private static ShowHotKeysViewUiBinder uiBinder = GWT.create(ShowHotKeysViewUiBinder.class);

    interface ShowHotKeysViewUiBinder extends UiBinder<Widget, ShowHotKeysView> {
    }

    @UiField
    ImageButton closeButton;

    @UiField
    HotKeyItemListGrid hotKeyItemListGrid;

    public ShowHotKeysView() {
        super(ID, ViewType.POPUP, TITLE, new Image(IDEImageBundle.INSTANCE.showHotKeys()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
    }

    /** @see org.exoplatform.ide.client.hotkeys.show.ShowHotKeysPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.client.hotkeys.show.ShowHotKeysPresenter.Display#getHotKeyItemListGrid() */
    @Override
    public ListGridItem<HotKeyItem> getHotKeyItemListGrid() {
        return hotKeyItemListGrid;
    }

}
