/**
 * Created by Dogerina on 16/06/2015.
 */
function put(msg) {
    document.getElementById("message_textarea").value = msg;
    document.getElementById("chat-submit").click();
}

setInterval(function () {
    put("ok");
}, 7500);
