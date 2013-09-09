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
