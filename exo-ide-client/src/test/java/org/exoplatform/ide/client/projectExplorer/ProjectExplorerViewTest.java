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
package org.exoplatform.ide.client.projectExplorer;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.googlecode.gwt.test.GwtTestWithMockito;
import com.googlecode.gwt.test.utils.events.Browser;

import org.junit.Test;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 6, 2012  
 */
public class ProjectExplorerViewTest extends GwtTestWithMockito
{

   /**
   * {@inheritDoc}
   */
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.IDE";
   }

   @Test
   public void shouldReactOnDoubleClick()
   {
      ProjectExpolorerView view = new ProjectExpolorerView();
      DoubleClickHandler clickHandler = mock(DoubleClickHandler.class);
      view.getTree().addDoubleClickHandler(clickHandler);
      Browser.dblClick(view.cellTree);
      // verify that double click event generated
      verify(clickHandler).onDoubleClick((DoubleClickEvent)any());
   }

   @Test
   public void shouldSelectOnClick()
   {
      // unable to test cell widgets with gwt-test-utils
   }
}
