
// get URL to css file
var scripts = document.getElementsByTagName('script');
var script = scripts[scripts.length - 1];
var css = script.src;
css = css.substring(0, css.lastIndexOf(".")) + ".css";

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
document.write("<div class=\"codenow\" onclick=\"window.open('" + script.getAttribute("target") + "', '_blank');\"></div>");

// remove self from DOM
script.parentNode.removeChild(script);

