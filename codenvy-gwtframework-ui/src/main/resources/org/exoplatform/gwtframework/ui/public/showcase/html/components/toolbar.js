/**
 *
 */

function Toolbar() {

    var toolbarId;

    this.create = function (id, placeHolder) {
        this.toolbarId = id;

        var html = document.getElementById("toolbarTemplate").innerHTML;
        html = html.replace("{toolbarId}", id);
        document.getElementById(placeHolder).innerHTML = html;
    }

    this.addLeft = function (obj) {
        var h = "<div class=\"exoToolbarElementLeft\">" + obj.getHTML() + "</div>";
        document.getElementById(this.toolbarId).innerHTML += h;
    }

    this.addRight = function (obj) {
        var h = "<div class=\"exoToolbarElementRight\">" + obj.getHTML() + "</div>";
        document.getElementById(this.toolbarId).innerHTML += h;
    }

}
