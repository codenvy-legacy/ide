// var isomorphicDir = "../../SmartGWT/smartgwt/sc/";
if (typeof gadgets != "undefined") {
	var gadgetURL = gadgets.util.getUrlParameters().url
			.match(/^http[s]*:\/\/([^\/]*)\//)[0];
	var isomorphicDir = gadgetURL + "SmartGWT/smartgwt/sc/";

	var html = '<script type="text/javascript" src="' + isomorphicDir
			+ 'modules/ISC_Core.js"></script>'

			+ '<script type="text/javascript"src="' + isomorphicDir
			+ 'modules/ISC_Foundation.js" /></script>'

			+ '<script type="text/javascript" src="' + isomorphicDir
			+ 'modules/ISC_Containers.js" /></script>'

			+ '<script type="text/javascript" src="' + isomorphicDir
			+ 'modules/ISC_Grids.js" /></script>'

			+ '<script type="text/javascript" src="' + isomorphicDir
			+ 'modules/ISC_Forms.js" /></script>'
			
			+ '<script type="text/javascript" src="' + isomorphicDir
			+ 'modules/ISC_RichTextEditor.js" /></script>'

			+ '<script type="text/javascript" src="' + isomorphicDir
			+ 'modules/ISC_DataBinding.js" ></script>'

			+ '<script type="text/javascript" src="' + isomorphicDir
			+ 'skins/Enterprise/load_skin.js" /></script>';

	document.open();
	document.write(html);
	document.close();
}
