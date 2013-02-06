/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.api.selection;

import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.core.event.SelectionChangedEvent;
import org.exoplatform.ide.extension.SDK;
import org.exoplatform.ide.selection.Selection;

/**
 * Selection API allows to provide a way of data-based communication, when Parts provide a static 
 * access to the data selected in active Part.
 * In order to listen to dynamic Selection changes, please subscribe to {@link SelectionChangedEvent} 
 * on {@link EventBus}. 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@SDK(title = "ide.api.ui.selection")
public interface SelectionAgent
{

   /**
    * Provides a way of getting current app-wide Selection.
    * 
    * @return
    */
   public Selection<?> getSelection();

}