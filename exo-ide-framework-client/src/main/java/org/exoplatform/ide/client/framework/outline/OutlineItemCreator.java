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
package org.exoplatform.ide.client.framework.outline;

import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.codeassitant.Token;

/**
 * This interface is used to create code outline item widget from OutlineTreeGrid class of
 * exo.ide.client library. Function getOutlineItemWidget(Token token) is realized in the OutlineItemCreatorImpl class and specific
 * {FileType}OutlineItemCreator classes of exo-ide-editor-{FileType} libraries. Each {FileType}OutlineItemCreator classes are
 * loaded like editors in the IDE at the start by using method org.exoplatform.ide.client.IDE->addOutlineItemCreator().
 *
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $
 */

public interface OutlineItemCreator {
    Widget getOutlineItemWidget(Token token);
}
