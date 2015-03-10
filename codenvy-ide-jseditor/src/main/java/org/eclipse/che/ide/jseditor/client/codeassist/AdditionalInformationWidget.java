/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.jseditor.client.codeassist;

import org.eclipse.che.ide.jseditor.client.popup.PopupResources;
import org.eclipse.che.ide.jseditor.client.popup.PopupWidget;
import org.eclipse.che.ide.util.dom.Elements;

import elemental.dom.Element;

public class AdditionalInformationWidget extends PopupWidget<Element> {

    public AdditionalInformationWidget(PopupResources popupResources) {
        super(popupResources);
    }

    @Override
    public Element getEmptyDisplay() {
        final Element noProposalMessage = Elements.createLiElement(getItemStyle());
        noProposalMessage.setTextContent("No information available");
        return noProposalMessage;
    }

    @Override
    public Element createItem(final Element itemModel) {
        return itemModel;
    }
}
