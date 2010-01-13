var registryURL = "/portal/rest/registry/repository";

// for the shell the configuration is being placed in the
// /src/main/webapp/shell/classes/conf/exo-configuration.xml

// in the gadget and standalone application the configuration is being looked in
// the webapps/portal.war/WEB-INF/conf/jcr/jcr-configuration.xml firstly

// default configuration for the gadget under the GateIn-beta03 - URL of WebDAV:
var appConfig = {
	"context" : "/rest/private",
	"gadgetServer" : "/gadgets/",
	//"gadgetServer" : "/eXoGadgetServer/gadgets/", /*for portal*/
	"publicContext" : "/rest",
	"repository" : "repository",
	"workspace" : "workspace"
}

//"gadgetServer" : "/eXoGadgetServer/oauthfree/gadgets/", /*for portal*/
var metadata = {};
