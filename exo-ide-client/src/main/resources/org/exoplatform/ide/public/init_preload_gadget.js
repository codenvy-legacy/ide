if (typeof gadgets != "undefined") {
	var gadgetURL = gadgets.util.getUrlParameters().url.match(/^http[s]*:\/\/([^\/]*)\//)[0];
	var frameURL = gadgetURL + "IDE/org.exoplatform.ide.IDEGadget/";
	var html = '<iframe src="' + frameURL + 'preload.html" id="pre" tabIndex="-2" style="position: absolute; width: 0; height: 0; border: 0"></iframe>'
	document.open();
	document.write(html);
	document.close();
}