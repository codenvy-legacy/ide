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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 12:14:23 PM anya $
 * 
 */
public class VersionsTabPain extends Composite
{
   private static VersionsTabPainUiBinder uiBinder = GWT.create(VersionsTabPainUiBinder.class);

   interface VersionsTabPainUiBinder extends UiBinder<Widget, VersionsTabPain>
   {
   }

   @UiField
   VersionsGrid versionsGrid;

   public VersionsTabPain()
   {
      initWidget(uiBinder.createAndBindUi(this));
   }

   /**
    * @return the versionsGrid
    */
   public VersionsGrid getVersionsGrid()
   {
      return versionsGrid;
   }
}