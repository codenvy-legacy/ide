
// get URL to css file
var scripts = document.getElementsByTagName('script');
var script = scripts[scripts.length - 1];

var css = script.src;
var className;

if ("white" == script.getAttribute("style")) {
  className = "codenow-white";
  css = css.substring(0, css.lastIndexOf("/")) + "/factory-white.css";
} else {
  className = "codenow-dark";
  css = css.substring(0, css.lastIndexOf("/")) + "/factory-dark.css";
}

// ensure css was injected before
var head = document.getElementsByTagName('head')[0];
var links = head.getElementsByTagName('link');
var injected = false;
for (i = 0; i < links.length; i++) {
	if (css == links[i].href) {
		injected = true;
		break;
	}
}	

// inject
if (!injected) {
	var link = document.createElement('link');
	link.rel = "stylesheet";
	link.type = "text/css";
	link.href = css;
	head.appendChild(link);	
}

// add CodeNow button
if (script.hasAttribute("target")) {
  document.write("<div class=\"" + className + "\" onclick=\"window.open('" + script.getAttribute("target") + "', '_blank');\"></div>");
} else {
  document.write("<div class=\"" + className + "\" onclick=\"\"></div>");
}

// remove self from DOM
script.parentNode.removeChild(script);

