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
package com.codenvy.ide.toolbar;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenvy.ide.api.ui.menu.ExtendedCommand;

import com.codenvy.ide.core.event.ExpressionsChangedEvent;
import com.codenvy.ide.core.expressions.Expression;
import com.codenvy.ide.core.expressions.ToggleStateExpression;
import com.codenvy.ide.toolbar.ToggleCommand;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.codenvy.ide.toolbar.ToolbarView;

import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonIntegerMap;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.resources.client.ImageResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Testing {@link ToolbarPresenter} functionality.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestToolbarPresenter
{
   private static final boolean IS_VISIBLE = true;

   private static final boolean IS_ENABLED = true;
   
   private static final boolean IS_SELECTED = true;

   private static final String PATH = "test";

   @Mock
   private SimpleEventBus eventBus;

   @Mock
   private ToolbarView view;

   private ToolbarPresenter presenter;

   @Before
   public void disarm()
   {
      // don't throw an exception if GWT.create() invoked
      GWTMockUtilities.disarm();

      setUp();
   }
   
   /**
    * Create general components for all test.
    */
   private void setUp()
   {
      presenter = new ToolbarPresenter(view, eventBus);
   }

   @After
   public void restore()
   {
      GWTMockUtilities.restore();
   }

   /**
    * If execute addItem method in the presenter then must be called
    * addItem method in the view with same parameters.
    */
   @Test
   public void shouldAddItemWithCommand()
   {
      Expression visible = mock(Expression.class);
      when(visible.getValue()).thenReturn(!IS_VISIBLE);

      Expression enabled = mock(Expression.class);
      when(enabled.getValue()).thenReturn(!IS_ENABLED);

      // create command with expressions
      ExtendedCommand command = mock(ExtendedCommand.class);
      when(command.inContext()).thenReturn(visible);
      when(command.canExecute()).thenReturn(enabled);

      presenter.addItem(PATH, command);
      
      verify(view).addItem(anyString(), (ExtendedCommand)anyObject(), eq(!IS_VISIBLE), eq(!IS_ENABLED));
   }

   /**
    * If execute addItem method in the presenter then must be called
    * addItem method in the view with default parameters.
    */
   @Test
   public void shouldAddItemWithOutCommand()
   {
      presenter.addItem(PATH, null);

      verify(view).addItem(anyString(), (ExtendedCommand)anyObject(), eq(IS_VISIBLE), eq(IS_ENABLED));
   }

   /**
    * If execute addToggleItem method in the presenter then must be called
    * addToggleItem method in the view with same parameters.
    */
   @Test
   public void shouldAddToggleItemWithToggleCommand()
   {
      Expression visible = mock(Expression.class);
      when(visible.getValue()).thenReturn(!IS_VISIBLE);

      Expression enabled = mock(Expression.class);
      when(enabled.getValue()).thenReturn(!IS_ENABLED);

      ToggleStateExpression selected = mock(ToggleStateExpression.class);
      when(selected.getValue()).thenReturn(!IS_SELECTED);

      // create command with expressions
      ToggleCommand command = mock(ToggleCommand.class);
      when(command.inContext()).thenReturn(visible);
      when(command.canExecute()).thenReturn(enabled);
      when(command.getState()).thenReturn(selected);

      presenter.addToggleItem(PATH, command);

      verify(view).addToggleItem(anyString(), (ToggleCommand)anyObject(), eq(!IS_VISIBLE), eq(!IS_ENABLED),
         eq(!IS_SELECTED));
   }

   /**
    * If execute addToggleItem method in the presenter then must be called
    * addToggleItem method in the view with default parameters.
    */
   @Test
   public void shouldAddToggleItemWithOutToggleCommand()
   {
      presenter.addToggleItem(PATH, null);

      verify(view).addToggleItem(anyString(), (ToggleCommand)anyObject(), eq(IS_VISIBLE), eq(IS_ENABLED),
         eq(IS_SELECTED));
   }

   /**
    * If execute addDropDownItem method in the presenter then must be called
    * addDropDownItem method in the view with default parameters.
    */
   @Test
   public void shouldAddDropDownItemWithOutEpressions()
   {
      presenter.addDropDownItem(PATH, mock(ImageResource.class), "toolTip");
      
      verify(view)
         .addDropDownItem(anyString(), (ImageResource)anyObject(), anyString(), eq(IS_VISIBLE), eq(IS_ENABLED));
   }
   
   /**
    * If execute addDropDownItem method in the presenter then must be called 
    * addDropDownItem method in the view with same parameters.
    */
   @Test
   public void shouldAddDropDownItemWithEpressions()
   {
      Expression visible = mock(Expression.class);
      when(visible.getValue()).thenReturn(!IS_VISIBLE);

      Expression enabled = mock(Expression.class);
      when(enabled.getValue()).thenReturn(!IS_ENABLED);

      presenter.addDropDownItem(PATH, mock(ImageResource.class), "toolTip", visible, enabled);
      
      verify(view).addDropDownItem(anyString(), (ImageResource)anyObject(), anyString(), eq(!IS_VISIBLE),
         eq(!IS_ENABLED));
   }

   /**
    * If execute copyMainMenuItem method in the presenter then must be called 
    * copyMainMenuItem method in the view with same parameters.
    */
   @Test
   public void shouldCopyMainMenuItem()
   {
      presenter.copyMainMenuItem(PATH, PATH);

      verify(view).copyMainMenuItem(eq(PATH), eq(PATH));
   }

   /**
    * If expression is changed then item state must be changed.
    */
   @Test
   public void shouldBeChangedItemStateIfExpressionIsChanged()
   {
      // create expressions
      Expression visible = mock(Expression.class);
      int visibleExpressionID = 1;
      when(visible.getValue()).thenReturn(!IS_VISIBLE);
      when(visible.getId()).thenReturn(visibleExpressionID);

      Expression enabled = mock(Expression.class);
      int enableExprssionID = 2;
      when(enabled.getValue()).thenReturn(!IS_ENABLED);
      when(enabled.getId()).thenReturn(enableExprssionID);

      ToggleStateExpression selected = mock(ToggleStateExpression.class);
      int selectedExpressionID = 3;
      when(selected.getValue()).thenReturn(!IS_SELECTED);
      when(selected.getId()).thenReturn(3);

      // create command with expressions
      ToggleCommand command = mock(ToggleCommand.class);
      when(command.inContext()).thenReturn(visible);
      when(command.canExecute()).thenReturn(enabled);
      when(command.getState()).thenReturn(selected);
      
      presenter.addToggleItem(PATH, command);
      
      // create list of changed expressions
      JsonIntegerMap<Boolean> expressions = JsonCollections.createIntegerMap();
      expressions.put(visibleExpressionID, IS_VISIBLE);
      expressions.put(enableExprssionID, IS_ENABLED);
      expressions.put(selectedExpressionID, IS_SELECTED);

      ExpressionsChangedEvent event = mock(ExpressionsChangedEvent.class);
      when(event.getChangedExpressions()).thenReturn(expressions);

      presenter.onExpressionsChanged(event);

      // check changed item states 
      verify(view).setVisible(eq(PATH), eq(IS_VISIBLE));
      verify(view).setEnabled(eq(PATH), eq(IS_ENABLED));
      verify(view).setSelected(eq(PATH), eq(IS_SELECTED));
   }
}