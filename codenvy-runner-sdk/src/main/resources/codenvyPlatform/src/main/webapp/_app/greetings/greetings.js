/*
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
/*
   Predefined greeting pages
*/

if (window['IDE'] && window['IDE']['config']) {
    window.IDE.config.greetings = {
      // user anonymous
      "anonymous": null,

      // user authenticated
      "authenticated": "/ws/_app/greetings/rightpane-authenticated.html",

      // anonymous user in temporary workspace
      "anonymous-workspace-temporary": "/ws/_app/greetings/temporary-workspace-rightpane-not-authenticated.html",

      // anonymous user in temporary private workspace
      "anonymous-workspace-temporary-private": "/ws/_app/greetings/temporary-private-workspace-rightpane-not-authenticated.html",

      // authenticated user in temporary workspace
      "authenticated-workspace-temporary": "/ws/_app/greetings/temporary-workspace-rightpane-authenticated.html",

      // authenticated user in temporary private workspace
      "authenticated-workspace-temporary-private": "/ws/_app/greetings/temporary-private-workspace-rightpane-authenticated.html",

      // anonymous user, temporary workspace, google-mbs-client-android project
      "anonymous-workspace-temporary-google-mbs-client-android": "/ws/_app/greetings/temporary-workspace-androidMBS-rightpane-not-authenticated.html",

      // authenticated user, temporary workspace, google-mbs-client-android project
      "authenticated-workspace-temporary-google-mbs-client-android": "/ws/_app/greetings/temporary-workspace-androidMBS-rightpane-authenticated.html"
    };
}
