/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.maven.client.build;

import com.codenvy.ide.extension.maven.client.build.BuildProjectView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class BuildProjectViewImpl extends Composite implements BuildProjectView
{
   private static BuildProjectViewImplUiBinder uiBinder = GWT.create(BuildProjectViewImplUiBinder.class);

   interface BuildProjectViewImplUiBinder extends UiBinder<Widget, BuildProjectViewImpl>
   {
   }

   @Inject
   protected BuildProjectViewImpl()
   {
      initWidget(uiBinder.createAndBindUi(this));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      // TODO Auto-generated method stub
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void showMessageInOutput(String text)
   {
      // TODO Auto-generated method stub
   }

   @Override
   public void startAnimation()
   {
      // TODO Auto-generated method stub
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void stopAnimation()
   {
      // TODO Auto-generated method stub
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void clearOutput()
   {
      // TODO Auto-generated method stub
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setClearOutputButtonEnabled(boolean isEnabled)
   {
      // TODO Auto-generated method stub
   }
}