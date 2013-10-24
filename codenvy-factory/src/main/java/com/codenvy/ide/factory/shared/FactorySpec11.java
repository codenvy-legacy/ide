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

package com.codenvy.ide.factory.shared;

/**
 * Describe parameters for Codenvy Factory feature. Version of specification Codenvy Factory 1.1
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 22.10.13 vlad $
 */
public interface FactorySpec11 extends FactorySpec10 {
    String ID           = "id";
    String STYLE        = "style";
    String DESCRIPTION  = "description";
    String CONTACT_MAIL = "contactmail";
    String AUTHOR       = "author";
    String LINKS        = "links";
}
