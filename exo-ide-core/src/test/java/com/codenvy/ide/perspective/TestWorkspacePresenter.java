/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.perspective;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.codenvy.ide.part.PartPresenter;
import com.codenvy.ide.part.PartStackPresenter;
import com.codenvy.ide.perspective.GenericPerspectivePresenter;
import com.codenvy.ide.perspective.WorkspacePresenter;
import com.codenvy.ide.perspective.WorkspaceView;
import com.codenvy.ide.perspective.PerspectivePresenter.PartStackType;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link WorkspacePresenter} functionality.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@RunWith(MockitoJUnitRunner.class)
public class TestWorkspacePresenter
{
   /**
    * 
    */
   private static final PartStackType TYPE = PartStackType.EDITING;
   
   @Mock
   Provider<PartStackPresenter> partStackProvider;

   @Mock
   Provider<GenericPerspectivePresenter> activePerspectiveProvider;

   @Mock
   GenericPerspectivePresenter activePerspective;
   
   @Mock
   WorkspaceView view;
   
   @Mock
   PartStackPresenter partStack;

   WorkspacePresenter presenter;

   @Before
   public void disarm()
   {
      // don't throw an exception if GWT.create() invoked
      GWTMockUtilities.disarm();

      when(partStackProvider.get()).thenReturn(partStack);
      when(activePerspectiveProvider.get()).thenReturn(activePerspective);

      presenter = new WorkspacePresenter(view, null, null, activePerspectiveProvider);
   }

   @After
   public void restore()
   {
      GWTMockUtilities.restore();
   }

   @Test
   public void shouldAddToStack()
   {
      PartPresenter part = mock(PartPresenter.class);
      presenter.showPart(part, TYPE);
      // verify part added to proper stack
      verify(activePerspective).openPart(eq(part), eq(TYPE));
   }

   @Test
   public void shouldCallSetActivePart()
   {
      PartPresenter part = mock(PartPresenter.class);
      presenter.setActivePart(part);

      verify(activePerspective).setActivePart(eq(part));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void shouldOpenNewPerspective()
   {
      String title = "new perspective";

      assertEquals(presenter.getActivePerspective(), activePerspective);

      Provider<GenericPerspectivePresenter> newPerspectiveProvider = mock(Provider.class);
      GenericPerspectivePresenter newPerspective = mock(GenericPerspectivePresenter.class);
      when(newPerspectiveProvider.get()).thenReturn(newPerspective);

      presenter.registerPerspective(title, mock(ImageResource.class), newPerspectiveProvider);
      presenter.openPerspective(title);
      
      assertEquals(presenter.getActivePerspective(), newPerspective);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void shouldGoToActivePerspective()
   {
      String title = "new perspective";

      assertEquals(presenter.getActivePerspective(), activePerspective);

      Provider<GenericPerspectivePresenter> newPerspectiveProvider = mock(Provider.class);
      GenericPerspectivePresenter newPerspective = mock(GenericPerspectivePresenter.class);
      when(newPerspectiveProvider.get()).thenReturn(newPerspective);

      presenter.registerPerspective(title, mock(ImageResource.class), newPerspectiveProvider);
      presenter.openPerspective(title);

      assertEquals(presenter.getActivePerspective(), newPerspective);
      
      verify(newPerspective).go((AcceptsOneWidget)anyObject());
      verifyZeroInteractions(activePerspective);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void shouldShownActivePerspective()
   {
      String title = "new perspective";
      PartPresenter part = mock(PartPresenter.class);

      assertEquals(presenter.getActivePerspective(), activePerspective);

      Provider<GenericPerspectivePresenter> newPerspectiveProvider = mock(Provider.class);
      GenericPerspectivePresenter newPerspective = mock(GenericPerspectivePresenter.class);
      when(newPerspectiveProvider.get()).thenReturn(newPerspective);

      presenter.registerPerspective(title, mock(ImageResource.class), newPerspectiveProvider);
      presenter.openPerspective(title);
      presenter.showPart(part, TYPE);

      assertEquals(presenter.getActivePerspective(), newPerspective);

      verify(newPerspective).openPart(eq(part), eq(TYPE));
      verifyZeroInteractions(activePerspective);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void shouldRegisteredNewPerspective()
   {
      assertEquals(presenter.getPerspectives().size(), 1);

      Provider<GenericPerspectivePresenter> newPerspectiveProvider = mock(Provider.class);
      presenter.registerPerspective("test", mock(ImageResource.class), newPerspectiveProvider);

      assertEquals(presenter.getPerspectives().size(), 2);
   }
}