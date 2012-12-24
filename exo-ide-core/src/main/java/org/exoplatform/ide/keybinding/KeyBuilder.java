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
package org.exoplatform.ide.keybinding;

import org.exoplatform.ide.util.input.CharCodeWithModifiers;
import org.exoplatform.ide.util.input.ModifierKeys;

/**
 * A builder for {@link CharCodeWithModifiers}. It's simplify creating CharCodeWithModifiers object.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class KeyBuilder
{
   private int modifiers;

   private int charCode;

   public KeyBuilder()
   {
   }

   /**
    * Add ACTION modifier.
    * Action is abstraction for the primary modifier used for chording shortcuts
    * in IDE. To stay consistent with native OS shortcuts, this will be set
    * if CTRL is pressed on Linux or Windows, or if CMD is pressed on Mac.
    *
    * @return the KeyBuilder with action modifier added
    */
   public KeyBuilder action()
   {
      modifiers |= ModifierKeys.ACTION;
      return this;
   }

   /**
    * Add ALT modifier
    *
    * @return the KeyBuilder with ALT modifier added
    */
   public KeyBuilder alt()
   {
      modifiers |= ModifierKeys.ALT;
      return this;
   }

   /**
    * Add CTRL modifier.
    * <b>
    * This will only be set on Mac. (On Windows and Linux, the
    * {@link org.exoplatform.ide.keybinding.KeyBuilder#action()} will be set instead.)
    * </b>
    *
    * @return the KeyBuilder with CTRL modifier added
    */
   public KeyBuilder control()
   {
      modifiers |= ModifierKeys.CTRL;
      return this;
   }

   /**
    * Add SHIFT modifier.
    *
    * @return the KeyBuilder with SHIFT modifier added
    */
   public KeyBuilder shift()
   {
      modifiers |= ModifierKeys.SHIFT;
      return this;
   }

   /**
    * Key binding has no modifier keys.
    *
    * @return the KeyBuilder with NONE modifier added
    */
   public KeyBuilder none()
   {
      modifiers = ModifierKeys.NONE;
      return this;
   }


   /**
    * Set char code
    *
    * @param charCode the code of the character.
    * @return the KeyBuilder with char code added
    */
   public KeyBuilder charCode(int charCode)
   {
      this.charCode = charCode;
      return this;
   }

   /**
    * Build CharCodeWithModifiers object.
    *
    * @return new CharCodeWithModifiers object.
    */
   public CharCodeWithModifiers build()
   {
      return new CharCodeWithModifiers(modifiers, charCode);
   }

}
