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
package org.exoplatform.ide.client.messages;

import com.google.gwt.i18n.client.Constants;

/**
 * Interface to represent the constants contained in resource bundle: 'IdePermissionsLocalizationConstant.properties'.
 * <p/>
 * Localization message for form from permissions group.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 */
public interface IdePermissionsLocalizationConstant extends Constants {
    /*
     * PermissionsListGrid
     */
    @Key("permissions.list.grid.identity")
    String listGridIdentity();

    @Key("permissions.list.grid.read")
    String listGridRead();

    @Key("permissions.list.grid.write")
    String listGridWrite();

    /*
     * PermissionsManagerForm
     */
    @Key("permissions.title")
    String permissionsTitle();

    @Key("permissions.name")
    String permissionsName();

    @Key("permissions.owner")
    String permissionsOwner();

    /*
     * PermissionsManagerPresenter
     */
    @Key("permissions.setAclFailure")
    String permissionsSetAclFailure();

    @Key("permissions.noAclProperty")
    String permissionsNoAclProperty();

}
