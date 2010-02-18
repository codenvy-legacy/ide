package org.exoplatform.ideall.client.statusbar;

import org.exoplatform.gwtframework.ui.smartgwt.component.Label;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.layout.Layout;

public class StatusBarForm extends Layout implements StatusBarPresenter.Display
{
   private final int PANEL_HEIGHT = 20;

   private Label pathInfoField;

   private StatusBarPresenter presenter;

   public StatusBarForm(HandlerManager eventBus)
   {
      setWidth100();
      setHeight(PANEL_HEIGHT);

      Layout l1 = new Layout();
      l1.setWidth(4);
      addMember(l1);

      pathInfoField = new Label();
      pathInfoField.setCanSelectText(true);
      pathInfoField.setMargin(2);
      pathInfoField.setAlign(Alignment.LEFT);
      pathInfoField.setWidth100();
      addMember(pathInfoField);

      presenter = new StatusBarPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   public HasValue<String> getPathInfoField()
   {
      return pathInfoField;
   }

}
