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
package org.exoplatform.ide.perspective;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.exoplatform.ide.api.ui.workspace.WorkspaceAgent;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.menu.MainMenuPresenter;
import org.exoplatform.ide.part.PartPresenter;
import org.exoplatform.ide.perspective.PerspectivePresenter.PartStackType;
import org.exoplatform.ide.presenter.Presenter;
import org.exoplatform.ide.toolbar.ToolbarPresenter;

/**
 * Root Presenter that implements Workspace logic. Descendant Presenters are injected via
 * constructor and exposed to coresponding UI containers.
 * It contains Menu, Toolbar and Perspective Presenter to exopse their views into corresponding places 
 * and to maintain their interactions.
 * 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class WorkspacePresenter implements Presenter, WorkspaceView.ActionDelegate, WorkspaceAgent
{
   public static final String GENERAL_PERSPECTIVE = "Default";

   private final WorkspaceView view;

   private final MainMenuPresenter menu;

   private PerspectivePresenter activePerspective;

   private JsonStringMap<PerspectiveDescriptor> pespectives = JsonCollections.createStringMap();

   private final ToolbarPresenter toolbarPresenter;

   /**
    * Instantiates Presenter
    * 
    * @param view
    * @param menu
    * @param genericPerspectiveProvider
    */
   @Inject
   protected WorkspacePresenter(WorkspaceView view, MainMenuPresenter menu, ToolbarPresenter toolbarPresenter,
      Provider<GenericPerspectivePresenter> genericPerspectiveProvider)
   {
      super();
      this.view = view;
      this.toolbarPresenter = toolbarPresenter;
      this.view.setDelegate(this);
      this.menu = menu;

      // register default perspective
      registerPerspective(GENERAL_PERSPECTIVE, null, genericPerspectiveProvider);
      this.activePerspective = genericPerspectiveProvider.get();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(AcceptsOneWidget container)
   {
      // Expose Project Explorer into Tools Panel
      menu.go(view.getMenuPanel());
      toolbarPresenter.go(view.getToolbarPanel());
      activePerspective.go(view.getPerspectivePanel());
      container.setWidget(view);
   }

   /**
    * Provides active Perspective instance
    * 
    * @return
    */
   public PerspectivePresenter getActivePerspective()
   {
      return activePerspective;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void registerPerspective(String title, ImageResource icon,
      Provider<? extends PerspectivePresenter> pespectiveProvider)
   {
      pespectives.put(title, new PerspectiveDescriptor(title, icon, pespectiveProvider));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void openPerspective(String title)
   {
      // SHOW IF ALREADY INITIALIZED
      // INITIALIZE AND SHOW IF NEW ONE
      PerspectiveDescriptor perspectiveDescriptor = pespectives.get(title);
      // instantiate perspective or get the same thanks to @Singleton
      PerspectivePresenter newPerspective = perspectiveDescriptor.pespectiveProvider.get();
      activePerspective = newPerspective;
      activePerspective.go(view.getPerspectivePanel());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void closePerspective(String title)
   {
      // REMOVE FROM INITIALIZED
      // CALL CLOSE
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setActivePart(PartPresenter part)
   {
      activePerspective.setActivePart(part);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void showPart(PartPresenter part, PartStackType type)
   {
      activePerspective.openPart(part, type);
   }

   /**
    * Wrapper containing the insformation about Perspective and it's Provider.
    */
   private class PerspectiveDescriptor
   {
      @SuppressWarnings("unused")
      protected String title;

      @SuppressWarnings("unused")
      protected ImageResource icon;

      protected Provider<? extends PerspectivePresenter> pespectiveProvider;

      public PerspectiveDescriptor(String title, ImageResource icon,
         Provider<? extends PerspectivePresenter> pespectiveProvider)
      {
         super();
         this.title = title;
         this.icon = icon;
         this.pespectiveProvider = pespectiveProvider;
      }
   }

}
