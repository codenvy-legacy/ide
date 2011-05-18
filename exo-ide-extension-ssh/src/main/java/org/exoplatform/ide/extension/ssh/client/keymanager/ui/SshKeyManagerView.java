package org.exoplatform.ide.extension.ssh.client.keymanager.ui;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.ssh.client.keymanager.HasSshGrid;
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

   public SshKeyManagerView()
   {
      super(ID, ViewType.MODAL, "Ssh Keys", null, 300, 400);
      add(uiBinder.createAndBindUi(this));
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

}
