/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * {@link SelectItem} represents select list field.
 * <p/>
 * Implements HasValue interface.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SelectItem.java Oct 20, 2011 9:58:00 AM vereshchaka $
 */
public class SelectItem extends ListBox implements HasValue<String> {

    /** @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared
     * .ValueChangeHandler) */
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> valueChangeHandler) {
        return addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                valueChangeHandler.onValueChange(new ValueChangeEventImpl(getValue()));
            }
        });
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     *      <p>
     *      Note that this implementation return <code>null</code> if SelectItem has no items
     *      </p>
     */
    public String getValue() {
        if (getSelectedIndex() == -1)
            return null;
        return getValue(getSelectedIndex());
    }

    /** @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object) */
    public void setValue(String value) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getValue(i).equals(value)) {
                setItemSelected(i, true);
                return;
            }
        }
    }

    public void setWidth(int width) {
        setWidth(width + "px");
    }

    /**
     * Clear old values and set new value map:<p/>
     * <p/>
     * The first value will be selected.<p/>
     * The event will be fire, that value was selected
     *
     * @param values
     */
    public void setValueMap(String[] values) {
        clear();
        for (String value : values) {
            addItem(value);
        }
        setSelectedIndex(0);
    }

    /**
     * Clear old values and set new value map:<p/>
     * The event will be fire, that <code>selected</code> value was selected
     *
     * @param values
     *         the list of values
     * @param selected
     *         the selected value
     */
    public void setValueMap(String[] values, String selected) {
        clear();
        for (String value : values) {
            addItem(value);
        }
        setValue(selected, true);
    }

    /**
     * Clear old values and set new value map:
     * <p/>
     * first argument -the text of the item to be added,<p/>
     * second argument - the item's value, to be submitted if it is part of a FormPanel; cannot be null
     * <p/>
     * The first value will be selected.
     *
     * @param values
     */
    public void setValueMap(LinkedHashMap<String, String> values) {
        clear();
        Set<Entry<String, String>> s = values.entrySet();

        Iterator<Entry<String, String>> it = s.iterator();

        while (it.hasNext()) {
            Entry<String, String> en = it.next();
            addItem(en.getValue(), en.getKey());
        }
        setSelectedIndex(0);
    }

    public void setHeight(int height) {
        setHeight(height + "px");
    }

    public void clearValue() {
        setValue("");
    }

    private class ValueChangeEventImpl extends ValueChangeEvent<String> {

        /** @param value */
        protected ValueChangeEventImpl(String value) {
            super(value);
        }

    }

    /**
     * Get the display value of the item.
     *
     * @return {@link String} display value
     */
    public String getDisplayValue() {
        int index = getSelectedIndex();
        if (index >= 0) {
            return getItemText(index);
        }
        return null;
    }

    /** @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean) */
    @Override
    public void setValue(String value, boolean fireEvents) {
        setValue(value);
        if (fireEvents) {
            fireEvent(new ChangeEventImpl());
        }
    }

    private class ChangeEventImpl extends ChangeEvent {
    }
}
