package org.exoplatform.ide.core.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.json.JsonIntegerMap;

/**
 * Event that notifies of changed Core Expressions
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ExpressionsChangedEvent extends GwtEvent<ExpressionsChangedHandler>
{
   public static Type<ExpressionsChangedHandler> TYPE = new Type<ExpressionsChangedHandler>();

   private final JsonIntegerMap<Boolean> expressions;

   /**
    * @param expressions the map of ID's and current values
    */
   public ExpressionsChangedEvent(JsonIntegerMap<Boolean> expressions)
   {
      this.expressions = expressions;
   }

   @Override
   public Type<ExpressionsChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the map, having identifier of the expressions and their new values
    */
   public JsonIntegerMap<Boolean> getChangedExpressions()
   {
      return expressions;
   }

   @Override
   protected void dispatch(ExpressionsChangedHandler handler)
   {
      handler.onExpressionsChanged(this);
   }
}
