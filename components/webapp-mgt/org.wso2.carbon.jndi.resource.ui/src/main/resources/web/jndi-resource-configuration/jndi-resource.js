/**
 * Created by kalpa on 9/15/14.
 */


function deleteRow(name, msg) {
    CARBON.showConfirmationDialog(msg + " '" + name + "'?", function(){
        document.location.href = "deletejndiresource.jsp?" + "name=" + name;
    });
}

function forward(destinationJSP){
    document.location.href = destinationJSP;
}