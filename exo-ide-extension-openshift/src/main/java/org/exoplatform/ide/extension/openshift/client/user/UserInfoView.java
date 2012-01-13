package org.exoplatform.ide.extension.openshift.client.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.info.ApplicationInfoListGrid;
import org.exoplatform.ide.extension.openshift.client.info.Property;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;

import java.util.ArrayList;

/**
 * view for showing user's information.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 14, 2011 4:25:33 PM anya $
 * 
 */
public class UserInfoView extends ViewImpl implements UserInfoPresenter.Display
{
   public static final String ID = "ideUserInfoView";

   private static final int HEIGHT = 320;

   private static final int WIDTH = 700;

   private static final String LOGIN_FIELD_ID = "ideUserInfoViewLoginField";

   private static final String DOMAIN_FIELD_ID = "ideUserInfoViewDomainField";

   private static UserInfoViewUiBinder uiBinder = GWT.create(UserInfoViewUiBinder.class);

   interface UserInfoViewUiBinder extends UiBinder<Widget, UserInfoView>
   {
   }

   /**
    * User's login field.
    */
   @UiField
   TextInput loginField;

   /**
    * User's domain.
    */
   @UiField
   TextInput domainField;

   /**
    * Grid with user's applications.
    */
   @UiField
   ApplicationGrid applicationGrid;

   /**
    * Application's properties.
    */
   @UiField
   ApplicationInfoListGrid applicationInfoGrid;

   /**
    * Ok button.
    */
   @UiField
   ImageButton okButton;

   public UserInfoView()
   {
      super(ID, ViewType.MODAL, OpenShiftExtension.LOCALIZATION_CONSTANT.userInfoViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      domainField.setName(DOMAIN_FIELD_ID);
      domainField.setHeight("22px");
      loginField.setName(LOGIN_FIELD_ID);
      loginField.setHeight("22px");
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.user.UserInfoPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.user.UserInfoPresenter.Display#getLoginField()
    */
   @Override
   public HasValue<String> getLoginField()
   {
      return loginField;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.user.UserInfoPresenter.Display#getDomainField()
    */
   @Override
   public HasValue<String> getDomainField()
   {
      return domainField;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.user.UserInfoPresenter.Display#getApplicationInfoGrid()
    */
   @Override
   public ListGridItem<Property> getApplicationInfoGrid()
   {
      return applicationInfoGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.user.UserInfoPresenter.Display#getApplicationGrid()
    */
   @Override
   public ListGridItem<AppInfo> getApplicationGrid()
   {
      return applicationGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.user.UserInfoPresenter.Display#addDeleteButtonSelectionHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addDeleteButtonSelectionHandler(SelectionHandler<AppInfo> handler)
   {
      applicationGrid.addDeleteButtonSelectionHandler(handler);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.user.UserInfoPresenter.Display#clearApplicationInfo()
    */
   @Override
   public void clearApplicationInfo()
   {
      applicationInfoGrid.setValue(new ArrayList<Property>());
   }
}
