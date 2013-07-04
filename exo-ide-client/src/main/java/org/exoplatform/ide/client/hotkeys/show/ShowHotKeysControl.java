/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;

/**
 * Control for show keyboard shortcuts.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowHotKeysControl.java May 10, 2012 10:17:30 AM azatsarynnyy $
 */
public class ShowHotKeysControl extends SimpleControl implements IDEControl {

    /** Control's identifier. */
    public static final String ID = IDE.IDE_LOCALIZATION_CONSTANT.showHotKeysIdControl();

    /** Control's title. */
    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.showHotKeysTitleControl();

    public ShowHotKeysControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setGroupName("Hotkeys & Dependencies");
        setImages(IDEImageBundle.INSTANCE.showHotKeys(), IDEImageBundle.INSTANCE.showHotKeysDisabled());
        setEvent(new ShowHotKeysEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setEnabled(true);
        setVisible(true);
    }

}
