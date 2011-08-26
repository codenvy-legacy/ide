package org.exoplatform.ide.extension.ssh.client.keymanager.ui;

import com.google.gwt.uibinder.client.UiFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.ssh.client.SshKeyExtension;
import org.exoplatform.ide.extension.ssh.client.SshLocalizationConstant;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

public class SshKeyManagerView extends ViewImpl implements Display
{

   private static SshKeyManagerViewUiBinder uiBinder = GWT.create(SshKeyManagerViewUiBinder.class);

   interface SshKeyManagerViewUiBinder extends UiBinder<Widget, SshKeyManagerView>
   {
   }

   @UiField
   SshKeysGrid keysGrid;

   @UiField
   ImageButton closeButton;
   
   @UiField
   ImageButton generateButton;
   
   @UiField
   ImageButton uploadButton;

   public SshKeyManagerView()
   {
      super(ID, ViewType.MODAL, "Ssh Keys", null, 333, 400);
      add(uiBinder.createAndBindUi(this));
   }
   
   /**
    * Factory method, uses for UiBinder
    * @return instance of {@link SshLocalizationConstant}
    */
   @UiFactory
   public SshLocalizationConstant getSshLocalizationConstant()
   {
      return SshKeyExtension.CONSTANTS;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display#getKeyItemGrid()
    */
   @Override
   public HasSshGrid<KeyItem> getKeyItemGrid()
   {
      return keysGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display#getGenerateButton()
    */
   @Override
   public HasClickHandlers getGenerateButton()
   {
      return generateButton;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display#getUploadButton()
    */
   @Override
   public HasClickHandlers getUploadButton()
   {
      return uploadButton;
   }

}
