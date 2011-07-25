/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.jenkins.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.jenkins.client.SelectGitRemoteRepositoryPresenter.Display;
import org.exoplatform.ide.extension.jenkins.client.JenkinsExtension;
import org.exoplatform.ide.extension.jenkins.client.JenkinsMessages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class SelectGitRemoteRepositoryView extends ViewImpl implements Display
{

   private static BuildJavaProjectViewUiBinder uiBinder = GWT.create(BuildJavaProjectViewUiBinder.class);

   interface BuildJavaProjectViewUiBinder extends UiBinder<Widget, SelectGitRemoteRepositoryView>
   {
   }

   @UiField(provided = true)
   final JenkinsMessages messages = JenkinsExtension.MESSAGES;

   @UiField
   SelectItem gitRepoUrl;

   @UiField
   ImageButton buildButton;

   @UiField
   ImageButton cancelButton;

   public SelectGitRemoteRepositoryView()
   {
      super("ideJenkinsBuildView", ViewType.MODAL, JenkinsExtension.MESSAGES.buildJavaAppViewTitle(), null, 400, 150);

      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.SelectGitRemoteRepositoryPresenter.Display#getBuildButton()
    */
   @Override
   public HasClickHandlers getBuildButton()
   {
      return buildButton;
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.SelectGitRemoteRepositoryPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.SelectGitRemoteRepositoryPresenter.Display#getGitRepository()
    */
   @Override
   public HasValue<String> getGitRepository()
   {
      return gitRepoUrl;
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.SelectGitRemoteRepositoryPresenter.Display#setGitRepositoryValues(java.util.Map)
    */
   @Override
   public void setGitRepositoryValues(String[] values)
   {
      gitRepoUrl.setValueMap(values);
   }

}
