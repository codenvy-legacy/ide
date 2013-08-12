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
package com.codenvy.ide.ext.gae.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Google App Engine client resources.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 16, 2012 5:43:05 PM anya $
 */
public interface GAEResources extends ClientBundle {
    @Source("com/codenvy/ide/ext/gae/images/gae.png")
    ImageResource googleAppEngine();

    @Source("com/codenvy/ide/ext/gae/images/gae_logo.png")
    ImageResource googleAppEngineLogo();

    @Source("com/codenvy/ide/ext/gae/images/gae_48.png")
    ImageResource googleAppEngine48();

    @Source("com/codenvy/ide/ext/gae/images/create_app.png")
    ImageResource createApplicationConrtol();

    @Source("com/codenvy/ide/ext/gae/images/general.png")
    ImageResource general();

    @Source("com/codenvy/ide/ext/gae/images/get_logs.png")
    ImageResource getLogs();

    @Source("com/codenvy/ide/ext/gae/images/logs.png")
    ImageResource logs();

    @Source("com/codenvy/ide/ext/gae/images/rollback_application.png")
    ImageResource rollbackApplication();

    @Source("com/codenvy/ide/ext/gae/images/update_application.png")
    ImageResource updateApplication();

    @Source("com/codenvy/ide/ext/gae/images/crons.png")
    ImageResource crons();

    @Source("com/codenvy/ide/ext/gae/images/backends.png")
    ImageResource backends();

    @Source("com/codenvy/ide/ext/gae/images/dos.png")
    ImageResource updateDos();

    @Source("com/codenvy/ide/ext/gae/images/update_indexes.png")
    ImageResource updateIndexes();

    @Source("com/codenvy/ide/ext/gae/images/vacuum_indexes.png")
    ImageResource vacuumIndexes();

    @Source("com/codenvy/ide/ext/gae/images/update_pagespeed.png")
    ImageResource updatePagespeed();

    @Source("com/codenvy/ide/ext/gae/images/update_queues.png")
    ImageResource updateQueues();

    @Source("com/codenvy/ide/ext/gae/images/update.png")
    ImageResource update();

    @Source("com/codenvy/ide/ext/gae/images/start.png")
    ImageResource start();

    @Source("com/codenvy/ide/ext/gae/images/stop.png")
    ImageResource stop();

    @Source("com/codenvy/ide/ext/gae/images/remove.png")
    ImageResource remove();

    @Source("com/codenvy/ide/ext/gae/images/configure.png")
    ImageResource configure();

    @Source("com/codenvy/ide/ext/gae/images/rollback.png")
    ImageResource rollback();

    @Source("com/codenvy/ide/ext/gae/images/rollback_all.png")
    ImageResource rollbackAll();

    @Source("com/codenvy/ide/ext/gae/images/resource_limits.png")
    ImageResource resourceLimits();

    @Source("com/codenvy/ide/ext/gae/images/login.png")
    ImageResource login();

    @Source("com/codenvy/ide/ext/gae/images/logout.png")
    ImageResource logout();
}