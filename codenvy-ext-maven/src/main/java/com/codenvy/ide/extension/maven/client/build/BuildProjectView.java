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

import com.codenvy.ide.mvp.View;

import com.codenvy.ide.extension.maven.client.build.BuildProjectView;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface BuildProjectView extends View<BuildProjectView.ActionDelegate>
{
   public interface ActionDelegate
   {
      public void onClearOutputClicked();
   }

   public void showMessageInOutput(String text);

   public void startAnimation();

   public void stopAnimation();

   public void clearOutput();

   public void setClearOutputButtonEnabled(boolean isEnabled);
}