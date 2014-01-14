/*
	IDE2 can show a greeting page, depending on project type and workspace.
	All configuration contained in the GREETING_PANE_CONTENT variable as map:
		var GREETING_PANE_CONTENT = {
			selector1: url1,
			selector2: url2,
			...
		};

	Below the main rule for valid selector configuration

		"anonymous|authenticated[-workspace-temporary][-project-type-in-lower-case]" : url-to-greeting-page

	Note, that 'project-type-in-lower-case' means the project type processed by the following rules:
		- all characters changed to lowercase
		- all '/' characters changed on '-'
		- all spaces changed on '-'

	Examples of selectors:

		* authenticated user, Java Script project opened
 			"authenticated-javascript"

 		* anonymous user, Maven Multi-module project opened
 			"anonymous-maven-multi-module"

 		* the same above but workspace is temporary
 			"anonymous-workspace-temporary-maven-multi-module"

 	IDE2 supports following types of projects: Jar, JavaScript, Maven Multi-module, nodejs,
 		PHP, Python, Rails, Servlet/JSP, Spring.

 */

var GREETING_PANE_CONTENT = {
	// user anonymous
	"anonymous": null,

	// user authenticated
	"authenticated": null,


	// anonymous user in temporary workspace
	"anonymous-workspace-temporary": ide_base_path + "greeting/temporary-workspace-rightpane-not-authenticated.html",

	// anonymous user in temporary private workspace
	"anonymous-workspace-temporary-private": ide_base_path + "greeting/temporary-private-workspace-rightpane-not-authenticated.html",

	// authenticated user in temporary workspace
	"authenticated-workspace-temporary": ide_base_path + "greeting/temporary-workspace-rightpane-authenticated.html",

	// authenticated user in temporary private workspace
	"authenticated-workspace-temporary-private": ide_base_path + "greeting/temporary-private-workspace-rightpane-authenticated.html",


	// anonymous user, temporary workspace, google-mbs-client-android project
	"anonymous-workspace-temporary-google-mbs-client-android": ide_base_path + "greeting/temporary-workspace-androidMBS-rightpane-not-authenticated.html",

	// authenticated user, temporary workspace, google-mbs-client-android project
	"authenticated-workspace-temporary-google-mbs-client-android": ide_base_path + "greeting/temporary-workspace-androidMBS-rightpane-authenticated.html"

};
