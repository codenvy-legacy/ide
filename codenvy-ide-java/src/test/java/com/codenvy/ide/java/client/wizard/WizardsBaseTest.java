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
package com.codenvy.ide.java.client.wizard;

import com.codenvy.ide.java.client.BaseTest;

import com.codenvy.ide.java.client.projectmodel.JavaProject;
import com.codenvy.ide.java.client.projectmodel.SourceFolder;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.wizard.WizardPagePresenter;

import com.codenvy.ide.json.JsonCollections;

import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public abstract class WizardsBaseTest extends BaseTest
{
   @Mock
   protected ResourceProvider resourceProvider;

   @Mock
   protected JavaProject project;

   @Mock
   protected SourceFolder sourceFolder;

   @Mock
   protected com.codenvy.ide.java.client.projectmodel.Package aPackage;

   @Mock
   protected WizardPagePresenter.WizardUpdateDelegate updateDelegate;

   @Before
   public void init()
   {
      when(resourceProvider.getActiveProject()).thenReturn(project);
      when(project.getSourceFolders()).thenReturn(JsonCollections.createArray(sourceFolder));
      when(sourceFolder.getChildren()).thenReturn(JsonCollections.<Resource>createArray(aPackage));
      when(sourceFolder.getName()).thenReturn("src");
      when(aPackage.getName()).thenReturn("com.ide");
   }
}
