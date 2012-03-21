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
package org.exoplatform.ide.extension.gadget.shared;

/**
 * Interface describe OpenSocial Gadget views.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Views.java Mar 20, 2012 17:23:11 PM azatsarynnyy $
 *
 */

public interface Views
{
   /**
    * @return preferred height
    */
   public double getPreferredHeight();

   /**
    * @param preferredHeight height to set
    */
   public void setPreferredHeight(double preferredHeight);

   /**
    * @return preferred width
    */
   public double getPreferredWidth();

   /**
    * @param preferredWidth width to set 
    */
   public void setPreferredWidth(double preferredWidth);

   /**
    * @return quirks
    */
   public boolean getQuirks();

   /**
    * @param quirks quirks to set
    */
   public void setQuirks(boolean quirks);

   /**
    * @return type
    */
   public String getType();

   /**
    * @param type type to set
    */
   public void setType(String type);
}