/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.tasks.part;

import com.codenvy.ide.api.ui.perspective.AbstractPartPresenter;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

import com.google.gwt.user.client.Window;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class TasksPartPresenter extends AbstractPartPresenter implements TasksPartView.ActionDelegate
{
   private final TasksPartViewImpl partViewImpl;

   private JsonArray<String> tasks;

   /**
    * 
    */
   @Inject
   public TasksPartPresenter(TasksPartViewImpl partViewImpl)
   {
      this.partViewImpl = partViewImpl;
      partViewImpl.setDelegate(this);
      tasks = JsonCollections.createArray();
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public String getTitle()
   {
      return "Tasks";
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public ImageResource getTitleImage()
   {
      return null;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public String getTitleToolTip()
   {
      return "This is tooltip message";
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void go(AcceptsOneWidget container)
   {
      //      Label label = new Label();
      //      label.setText("This is content");
      //      container.setWidget(label);
      container.setWidget(partViewImpl);
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void onAddEvent()
   {
      String newTask = Window.prompt("Define name for new Task", "");
      if (newTask != null)
      {
         tasks.add(newTask);
         partViewImpl.displayTasks(tasks);
      }
   }
   
    /**
    * {@inheritDoc}
    */
   @Override
   public boolean onClose()
   {
      return true;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void onRemoveEvent(String task)
   {
      if (task != null && Window.confirm("Do you really want to remove task:" + task + "?"))
      {
         tasks.remove(task);
         partViewImpl.displayTasks(tasks);
      }
   }

}
