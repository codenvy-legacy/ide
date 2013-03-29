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
package com.codenvy.ide.api.ui.keybinding;

import com.codenvy.ide.api.ui.menu.ExtendedCommand;

import com.codenvy.ide.util.input.CharCodeWithModifiers;


/**
 * Scheme is set of the key bindings
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface Scheme
{

   /**
    * Get id of the scheme.
    *
    * @return the scheme id
    */
   String getSchemeId();

   /**
    * Get scheme description
    *
    * @return the scheme description
    */
   String getDescription();

   /**
    * Add key binding for command.
    *
    * @param keyBinging the key sequence.
    * @param command    the command which keys bind.
    */
   void addKeyBinding(CharCodeWithModifiers keyBinging, ExtendedCommand command);

}
