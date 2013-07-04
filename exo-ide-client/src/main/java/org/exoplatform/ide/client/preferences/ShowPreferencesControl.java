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
package org.exoplatform.ide.client.preferences;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;

/**
 * Control for showing and editing IDE preferences.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 18, 2012 12:47:10 PM anya $
 */
@RolesAllowed({"developer"})
public class ShowPreferencesControl extends SimpleControl implements IDEControl {
    public ShowPreferencesControl() {
        super(IDE.PREFERENCES_CONSTANT.showPreferencesControlId());
        setTitle(IDE.PREFERENCES_CONSTANT.showPreferencesControlTitle());
        setPrompt(IDE.PREFERENCES_CONSTANT.showPreferencesControlPrompt());
        setEvent(new ShowPreferencesEvent());
        setImages(IDEImageBundle.INSTANCE.preferences(), IDEImageBundle.INSTANCE.preferencesDisabled());
        setDelimiterBefore(true);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }

}
