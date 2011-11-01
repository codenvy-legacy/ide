package org.exoplatform.ide.extension.samples.client.convert;

import com.google.gwt.user.client.ui.Label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.ItemTree;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;
import java.util.Set;

/**
 * Convert folder to project view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Nov 1, 2011 12:33:44 PM anya $
 *
 */
public class ConvertToProjectView extends ViewImpl implements ConvertToProjectPresenter.Display
{
   private static final int WIDTH = 550;

   private static final int HEIGHT = 380;
   
   private static final String ID = ConvertToProjectView.class.getName();

   private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.convertTitle();

   /**
    * Project's name field.
    */
   @UiField
   TextInput projectName;
   
   /**
    * Project's type field.
    */
   @UiField
   SelectItem projectType;

   /**
    * Convert button.
    */
   @UiField
   ImageButton convertButton;
   
   /**
    * Next step button.
    */
   @UiField
   ImageButton nextButton;
   
   /**
    * Back to previous step button.
    */
   @UiField
   ImageButton backButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;
   
   /**
    * Browser tree.
    */
   @UiField
   ItemTree browserTree;
   
   /**
    * Layout of the first step.
    */
   @UiField
   Border step1Layout;
   
   /**
    * Layout of the second step.
    */
   @UiField
   FlowPanel step2Layout;
   
   /**
    * Title of the step.
    */
   @UiField
   Label stepTitle;
   
   /**
    * Description of the step.
    */
   @UiField
   Label stepDescription;
   
   private static ConvertToProjectViewUiBinder uiBinder = GWT.create(ConvertToProjectViewUiBinder.class);

   interface ConvertToProjectViewUiBinder extends UiBinder<Widget, ConvertToProjectView>
   {
   }

   public ConvertToProjectView()
   {
      super(ID, ViewType.MODAL, TITLE, null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#getConvertButton()
    */
   @Override
   public HasClickHandlers getConvertButton()
   {
      return convertButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#getProjectType()
    */
   @Override
   public HasValue<String> getProjectType()
   {
      return projectType;
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#setProjectType(java.util.Set)
    */
   @Override
   public void setProjectType(Set<String> set)
   {
      projectType.setValueMap(set.toArray(new String[set.size()]));
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#getNextButton()
    */
   @Override
   public HasClickHandlers getNextButton()
   {
      return nextButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#getBackButton()
    */
   @Override
   public HasClickHandlers getBackButton()
   {
      return backButton;
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#enableNextButton(boolean)
    */
   @Override
   public void enableNextButton(boolean enable)
   {
      nextButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#enableBackButton(boolean)
    */
   @Override
   public void enableBackButton(boolean enable)
   {
      backButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#enableConvertButton(boolean)
    */
   @Override
   public void enableConvertButton(boolean enable)
   {
      convertButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#getBrowserTree()
    */
   @Override
   public TreeGridItem<Item> getBrowserTree()
   {
      return browserTree;
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#getSelectedItems()
    */
   @Override
   public List<Item> getSelectedItems()
   {
      return browserTree.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#selectItem(java.lang.String)
    */
   @Override
   public void selectItem(String itemId)
   {
      browserTree.selectItem(itemId);
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#stepOne()
    */
   @Override
   public void stepOne()
   {
      stepTitle.setText(SamplesExtension.LOCALIZATION_CONSTANT.convertLocationTitle());
      stepDescription.setText(SamplesExtension.LOCALIZATION_CONSTANT.convertLocationText());
      
      step1Layout.setVisible(true);
      nextButton.setVisible(true);
      
      step2Layout.setVisible(false);
      backButton.setVisible(false);
      convertButton.setVisible(false);
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#stepTwo()
    */
   @Override
   public void stepTwo()
   {
      stepTitle.setText(SamplesExtension.LOCALIZATION_CONSTANT.convertPropertiesTitle());
      stepDescription.setText(SamplesExtension.LOCALIZATION_CONSTANT.convertPropertiesText());
      
      step2Layout.setVisible(true);
      backButton.setVisible(true);
      convertButton.setVisible(true);
      
      step1Layout.setVisible(false);
      nextButton.setVisible(false);
   }

   /**
    * @see org.exoplatform.ide.client.project.convert.ConvertToProjectPresenter.Display#getProjectName()
    */
   @Override
   public HasValue<String> getProjectName()
   {
      return projectName;
   }
}
