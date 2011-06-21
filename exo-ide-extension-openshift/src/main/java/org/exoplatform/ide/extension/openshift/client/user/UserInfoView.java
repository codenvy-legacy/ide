package org.exoplatform.ide.extension.openshift.client.user;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.info.ApplicationInfoGrid;
import org.exoplatform.ide.extension.openshift.client.info.Property;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;

/**
 * view for showing user's information.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 14, 2011 4:25:33 PM anya $
 *
 */
public class UserInfoView extends ViewImpl implements UserInfoPresenter.Display
{
   public static final String ID = "ideUserInfoView";

   private static final int HEIGHT = 320;

   private static final int WIDTH = 700;

   public static final String TYPE = "modal";

   private static final String LOGIN_FIELD_ID = "ideUserInfoViewLoginField";

   private static final String DOMAIN_FIELD_ID = "ideUserInfoViewDomainField";

   public static final String TITLE = OpenShiftExtension.LOCALIZATION_CONSTANT.userInfoViewTitle();

   private static UserInfoViewUiBinder uiBinder = GWT.create(UserInfoViewUiBinder.class);

   interface UserInfoViewUiBinder extends UiBinder<Widget, UserInfoView>
   {
   }

   /**
    * User's login field.
    */
   @UiField
   TextField loginField;

   /**
    * User's domain.
    */
   @UiField
   TextField domainField;

   /**
    * Grid with user's applications.
    */
   @UiField
   ApplicationGrid applicationGrid;

   /**
    * Application's properties.
    */
   @UiField
   ApplicationInfoGrid applicationInfoGrid;

   /**
    * Ok button.
    */
   @UiField
   ImageButton okButton;

   public UserInfoView()
   {
      super(ID, TYPE, TITLE, null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      domainField.setId(DOMAIN_FIELD_ID);
      domainField.setHeight(22);
      loginField.setId(LOGIN_FIELD_ID);
      loginField.setHeight(22);
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
}
