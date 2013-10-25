
/*
When an AJAX method is started, this adds a class "loading" to the body (in main.css).
When the body has this class, the scroll bars are turned off and the class "modal" becomes visible.
The div "modal" (bottom of page) contains the text "Loading...".
The CSS positions, sizes and colours the div and displays an animation in the background (80% white).
*/
$("body").on({
    ajaxStart: function() {
        $(this).addClass("loading");
    },
    ajaxStop: function() {
        $(this).removeClass("loading");
    }
});