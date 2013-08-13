/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ui;

import com.codenvy.ide.ui.base.HasStyle;
import com.codenvy.ide.ui.base.Style;
import com.codenvy.ide.ui.base.StyleHelper;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget that can have several child widgets.
 * <p>
 * Base of a lot of other components :)
 * 
 * @since 2.0.4.0
 * 
 * @author Carlos Alexandro Becker
 */
public class ComplexWidget extends ComplexPanel implements HasWidgets,
                                                           HasStyle {

	/**
	 * Creates a new widget that is based on the provided html tag.
	 * 
	 * @param tag
	 *            the html tag used for this widget
	 */
	public ComplexWidget(String tag) {
		setElement(DOM.createElement(tag));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Widget w) {
		add(w, getElement());
		
//		// logical add
//		getChildren().add(w);
//
//		// physical add
//		getElement().appendChild(w.getElement());

	}

	/**
	 * Inserts another widget into this one.
	 * 
	 * @param w
	 *            the widget to be inserted
	 * @param beforeIndex
	 *            the index of the position before which it should be set
	 */
	public void insert(Widget w, int beforeIndex) {
		insert(w, getElement(), beforeIndex, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setStyle(Style style) {
		StyleHelper.setStyle(this, style);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addStyle(Style style) {
		StyleHelper.addStyle(this, style);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeStyle(Style style) {
		StyleHelper.removeStyle(this, style);
	}
}
