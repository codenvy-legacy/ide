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
 * Interface to represent the constants contained in resource bundle: 'IdeVersionsLocalizationConstant.properties'.
 * <p/>
 * Localization message for form from versions group.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 */
public interface IdeVersionsLocalizationConstant extends Constants {
    /*
     * VersionsGrid
     */
    @Key("versions.grid.name")
    String versionsGridName();

    @Key("versions.grid.date")
    String versionsGridDate();

    @Key("versions.grid.length")
    String versionsGridLength();

    /*
     * ViewVersionsForm
     */
    @Key("viewVersions.title")
    String viewVersionsTitle();

    @Key("viewVersions.for")
    String viewVersionsFor();

    /*
     * VersionContentPresenter
     */
    @Key("versions.displayPrefix")
    String versionsDisplayPrefix();

    /*
     * RestoreToVersionCommandHandler
     */
    @Key("restoreToVersion.dialog.title")
    String restoreToVersionDialogTitle();

    /*
     * ShowVersionListCommandHandler VersionHistoryCommandHandler
     */
    @Key("versions.openFile")
    String versionsOpenFile();

    @Key("versions.version.title")
    String versionTitle();

    /*
     * ViewVersionsPresenter
     */
    @Key("versions.error.openFile")
    String versionErrorOpenFile();

}
