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
package org.exoplatform.ide.texteditor.api;

import org.exoplatform.ide.util.SignalEvent;

/**
 * A listener that is called when the user presses a key.
 */
public interface KeyListener
{
   /*
    * The reason for preventDefault() not preventing default behavior is that
    * Firefox does not have support the defaultPrevented attribute, so we have
    * know way of knowing if it was prevented from the native event. We could
    * create a proxy for SignalEvent to note calls to preventDefault(), but
    * this would not catch the case that the implementor interacts directly to
    * the native event.
    */
   /**
    * @param event the event for the key press. Note: Calling preventDefault()
    *        may not prevent the default behavior in some cases. The return
    *        value of this method is a better channel for indicating the
    *        default behavior should be prevented.
    * @return true if the event was handled (the default behavior will not run
    *         in this case), false to proceed with the default behavior. Even
    *         if true is returned, other listeners will still get the callback
    */
   boolean onKeyPress(SignalEvent event);
}