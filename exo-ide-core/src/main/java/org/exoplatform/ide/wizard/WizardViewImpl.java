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
package org.exoplatform.ide.wizard;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;


/**
 * WizardViewImpl is the view of wizard.
 * The view shows wizard pages to the end user. It has an area at the top containing 
 * the wizard page title and notice, at the middle of page is the current wizard page, 
 * Back and Next buttons, at the bottom of page is Cancel and Finish buttons.    
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class WizardViewImpl extends DialogBox implements WizardView
{
   private Button btnNext;

   private Button btnBack;

   private Button btnFinish;

   private Button btnCancel;

   private Label caption;

   private Label notice;

   private SimplePanel imagePanel;

   private DeckLayoutPanel contentPanel;

   private WizardView.ActionDelegate delegate;

   private final int ANIMATION_TIME = 400;

   private final int NO_TIME = 0;

   /**
    * Create view with given instance of resources
    * 
    * @param resources resources for wizard (for example css)
    */
   public WizardViewImpl(WizardResource resources, String title)
   {
      setText(title);
      addStyleName(resources.wizardCss().ideWizard());

      DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
      mainPanel.setSize("600px", "400px");

      DockLayoutPanel northPanel = new DockLayoutPanel(Unit.PX);

      FlowPanel buttonPanel = new FlowPanel();
      btnFinish = new Button("Finish");
      btnFinish.setStyleName(resources.wizardCss().alignBtn());
      buttonPanel.add(btnFinish);

      btnCancel = new Button("Cancel");
      btnCancel.setStyleName(resources.wizardCss().alignBtn());
      buttonPanel.add(btnCancel);

      mainPanel.addSouth(buttonPanel, 26);

      //TODO needs improvement next and back buttons      
      btnBack = new Button("<");
      btnBack.setStyleName(resources.wizardCss().backBtn());
      SimplePanel wrapBackBtn = new SimplePanel(btnBack);
      wrapBackBtn.setHeight("100%");
      mainPanel.addWest(wrapBackBtn, 20);
      
      btnNext = new Button(">");
      btnNext.setStyleName(resources.wizardCss().nextBtn());
      SimplePanel wrapNextBtn = new SimplePanel(btnNext);
      wrapNextBtn.setHeight("100%");
      mainPanel.addEast(wrapNextBtn, 20);

      imagePanel = new SimplePanel();
      northPanel.addEast(imagePanel, 48);

      caption = new Label();
      notice = new Label();

      northPanel.addNorth(caption, 20);
      northPanel.add(notice);

      mainPanel.addNorth(northPanel, 48);

      contentPanel = new DeckLayoutPanel();
      mainPanel.add(contentPanel);

      add(mainPanel);

      bind();
   }

   /**
    * Adds behavior to wizard view components
    */
   private void bind()
   {
      btnNext.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            delegate.onNextClicked();
         }
      });

      btnBack.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            delegate.onBackClicked();
         }
      });

      btnFinish.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            delegate.onFinishClicked();
         }
      });

      btnCancel.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            delegate.onCancelClicked();
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   public void setNextButtonVisible(boolean isVisible)
   {
      btnNext.setVisible(isVisible);
   }

   /**
    * {@inheritDoc}
    */
   public void setBackButtonVisible(boolean isVisible)
   {
      btnBack.setVisible(isVisible);
   }

   /**
    * {@inheritDoc}
    */
   public void setNextButtonEnabled(boolean isEnabled)
   {
      btnNext.setEnabled(isEnabled);
   }

   /**
    * {@inheritDoc}
    */
   public void setFinishButtonEnabled(boolean isEnabled)
   {
      btnFinish.setEnabled(isEnabled);
   }

   /**
    * {@inheritDoc}
    */
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   /**
    * {@inheritDoc}
    */
   public void setCaption(String caption)
   {
      this.caption.setText(caption);
   }

   /**
    * {@inheritDoc}
    */
   public void setNotice(String notice)
   {
      this.notice.setText(notice);
   }

   /**
    * {@inheritDoc}
    */
   public void close()
   {
      this.hide();
   }

   /**
    * {@inheritDoc}
    */
   public AcceptsOneWidget getContentPanel()
   {
      return contentPanel;
   }

   /**
    * {@inheritDoc}
    */
   public void setImage(IsWidget image)
   {
      imagePanel.setWidget(image);
   }

   /**
    * {@inheritDoc}
    */
   public void showWizard()
   {
      this.center();
      this.show();
   }

   /**
    * {@inheritDoc}
    */
   public void setChangePageAnimationEnabled(boolean isEnabled)
   {
      if (isEnabled)
      {
         contentPanel.setAnimationDuration(ANIMATION_TIME);
      }
      else
      {
         contentPanel.setAnimationDuration(NO_TIME);
      }
   }
}