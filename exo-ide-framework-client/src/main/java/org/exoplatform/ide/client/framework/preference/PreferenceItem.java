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
package org.exoplatform.ide.client.framework.preference;

import com.google.gwt.user.client.ui.Image;

import java.util.List;

/**
 * Preference item describes the single preference, which can have sub-preferences and thus organize tree structure. Preference
 * items are registered as follows:<br>
 * <code>
 *    Preferences.get().addPreferenceItem(new SamplePreference());
 * </code>
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 18, 2012 3:24:39 PM anya $
 * 
 */
public interface PreferenceItem
{
   /**
    * Returns preference's name (title).
    * 
    * @return {@link String} preference's name
    */
   String getName();

   /**
    * Sets preference's name (title).
    * 
    * @param name preference's name (title)
    */
   void setName(String name);

   /**
    * Returns image associated with preference.
    * 
    * @return {@link Image} preference's image
    */
   Image getImage();

   /**
    * Sets image associated with preference.
    * 
    * @param image preference's image
    */
   void setImage(Image image);

   /**
    * Returns the list of sub preferences.
    * 
    * @return {@link List} of {@link PreferenceItem} list of sub preferences
    */
   List<PreferenceItem> getChildren();

   /**
    * Sets the list of sub preferences.
    * 
    * @param children list of sub preferences
    */
   void setChildren(List<PreferenceItem> children);

   /**
    * Returns the preference's performer (which will do defined actions, when preference is called).
    * 
    * @return {@link PreferencePerformer} preference's performer
    */
   PreferencePerformer getPreferencePerformer();

   /**
    * Sets the preference's performer (which will do defined actions, when preference is called).
    * 
    * @param preferencePerformer preference performer
    */
   void setPreferencePerformer(PreferencePerformer preferencePerformer);
}
