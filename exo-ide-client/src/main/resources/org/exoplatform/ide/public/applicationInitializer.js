var registryURL = "/rest/private/registry/repository";
var proxyServiceContext = "/rest/private/proxy";

// for the shell the configuration is being placed in the
// /src/main/webapp/shell/classes/conf/exo-configuration.xml

// in the gadget and standalone application the configuration is being looked in
// the webapps/portal.war/WEB-INF/conf/jcr/jcr-configuration.xml firstly

// default configuration for the gadget under the GateIn-beta03 - URL of WebDAV:
var appConfig = {
	"context" : "/rest/private",
	"publicContext" : "/rest",
	"entryPoint" : "/rest/private/jcr/repository/dev-monit",
	
	"gadgetServer" : "/gadgets/", /*under GWT Shell*/
	//"gadgetServer" : "/eXoGadgetServer/gadgets/" /*for portal*/
}
