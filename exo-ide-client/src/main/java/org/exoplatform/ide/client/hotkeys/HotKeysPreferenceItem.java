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
package org.exoplatform.ide.client.hotkeys;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.preference.AbstractPreferenceItem;
import org.exoplatform.ide.client.framework.preference.PreferencePerformer;

/**
 * Hotkeys preference item.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 20, 2012 3:15:23 PM anya $
 */
public class HotKeysPreferenceItem extends AbstractPreferenceItem {
    private static final String NAME = IDE.PREFERENCES_CONSTANT.customizeHotkeysTitle();

    public HotKeysPreferenceItem(PreferencePerformer performer) {
        super(NAME, new Image(IDEImageBundle.INSTANCE.customizeHotKeys()), performer);
    }
}
