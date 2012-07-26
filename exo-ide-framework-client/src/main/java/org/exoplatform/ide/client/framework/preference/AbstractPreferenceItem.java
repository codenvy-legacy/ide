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

import java.util.ArrayList;
import java.util.List;

/**
 * Parent class for preference items, that implements {@link PreferenceItem}.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 19, 2012 10:02:53 AM anya $
 * 
 */
public abstract class AbstractPreferenceItem implements PreferenceItem
{
   /**
    * Preference's name (title).
    */
   private String name;

   /**
    * Preference's image.
    */
   private Image image;

   /**
    * Sub preferences.
    */
   private List<PreferenceItem> children;

   /**
    * Preference performer.
    */
   private PreferencePerformer performer;

   /**
    * @param name preference's name (title)
    * @param image preference's image
    */
   protected AbstractPreferenceItem(String name, Image image)
   {
      this.name = name;
      this.image = image;
   }

   /**
    * @param name preference's name (title)
    * @param image preference's image
    * @param performer preference performer
    */
   protected AbstractPreferenceItem(String name, Image image, PreferencePerformer performer)
   {
      this.name = name;
      this.image = image;
      this.performer = performer;
   }

   /**
    * @see org.exoplatform.ide.client.framework.preference.PreferenceItem#getName()
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.client.framework.preference.PreferenceItem#setName(java.lang.String)
    */
   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @see org.exoplatform.ide.client.framework.preference.PreferenceItem#getImage()
    */
   @Override
   public Image getImage()
   {
      return image;
   }

   /**
    * @see org.exoplatform.ide.client.framework.preference.PreferenceItem#setImage(com.google.gwt.user.client.ui.Image)
    */
   @Override
   public void setImage(Image image)
   {
      this.image = image;
   }

   /**
    * @see org.exoplatform.ide.client.framework.preference.PreferenceItem#getChildren()
    */
   @Override
   public List<PreferenceItem> getChildren()
   {
      if (children == null)
      {
         children = new ArrayList<PreferenceItem>();
      }
      return children;
   }

   /**
    * @see org.exoplatform.ide.client.framework.preference.PreferenceItem#setChildren(java.util.List)
    */
   @Override
   public void setChildren(List<PreferenceItem> children)
   {
      this.children = children;
   }

   /**
    * @see org.exoplatform.ide.client.framework.preference.PreferenceItem#getPreferencePerformer()
    */
   @Override
   public PreferencePerformer getPreferencePerformer()
   {
      return performer;
   }

   /**
    * @see org.exoplatform.ide.client.framework.preference.PreferenceItem#setPreferencePerformer(org.exoplatform.ide.client.framework.preference.PreferencePerformer)
    */
   @Override
   public void setPreferencePerformer(PreferencePerformer preferencePerformer)
   {
      this.performer = preferencePerformer;
   }
}
