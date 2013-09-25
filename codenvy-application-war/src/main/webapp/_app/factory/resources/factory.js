
// get URL to css file
var scripts = document.getElementsByTagName('script');
var script = scripts[scripts.length - 1];

var css = script.src;
var className;
var counter = false;
var vertical = true;

if ("white" == script.getAttribute("style")) {
  className = "codenow-white";
  css = css.substring(0, css.lastIndexOf("/")) + "/factory-white.css";
} else {
  className = "codenow-dark";
  css = css.substring(0, css.lastIndexOf("/")) + "/factory-dark.css";
}

if ("vertical" == script.getAttribute("counter")) {
	counter = true;
	vertical = true;
} else if ("horizontal" == script.getAttribute("counter")) {
	counter = true;
	vertical = false;
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

if (counter == true) {
	if (vertical == true) {
		// add CodeNow button
		if (script.hasAttribute("target")) {
		  document.write("<div class=\"codenow-vertical\">" +
		  		"<div class=\"codenow-counter-vertical\">33</div>" +
		  		"<div class=\"" + className + " codenow-bottom\" " +
		  				"onclick=\"window.open('" + script.getAttribute("target") + "', '_blank');\"></div>" +
		  	"</div>");
		} else {
			  document.write("<div class=\"codenow-vertical\">" +
				  		"<div class=\"codenow-counter-vertical\">33</div>" +
				  		"<div class=\"" + className + " codenow-bottom\" " +
				  				"onclick=\"\"></div>" +
				  	"</div>");
		}			
	} else {
		// add CodeNow button
		if (script.hasAttribute("target")) {
		  document.write("<div class=\"codenow-horizontal\">" +
		  		"<div class=\"" + className + " codenow-bottom\" onclick=\"window.open('" + script.getAttribute("target") + "', '_blank');\"></div>" +
		  		"<div class=\"codenow-counter-horizontal\">&nbsp;33</div>" +
		  	"</div>");
		} else {
			  document.write("<div class=\"codenow-horizontal\">" +
				  		"<div class=\"" + className + " codenow-bottom\" onclick=\"\"></div>" +
				  		"<div class=\"codenow-counter-horizontal\">&nbsp;33</div>" +
				  	"</div>");
		}			
	}
} else {
	// add CodeNow button
	if (script.hasAttribute("target")) {
	  document.write("<div class=\"" + className + "\" onclick=\"window.open('" + script.getAttribute("target") + "', '_blank');\"></div>");
	} else {
	  document.write("<div class=\"" + className + "\" onclick=\"\"></div>");
	}	
}

// remove self from DOM
script.parentNode.removeChild(script);

