function encode(string) { 
    string = string.replace(/\r\n/g,"\n"); 
    var utftext = ""; 
 
    for (var n = 0; n < string.length; n++) { 
 
        var c = string.charCodeAt(n); 
 
        if (c < 128) { 
            utftext += String.fromCharCode(c); 
        } 
        else if((c > 127) && (c < 2048)) { 
            utftext += String.fromCharCode((c >> 6) | 192); 
            utftext += String.fromCharCode((c & 63) | 128); 
        } 
        else { 
            utftext += String.fromCharCode((c >> 12) | 224); 
            utftext += String.fromCharCode(((c >> 6) & 63) | 128); 
            utftext += String.fromCharCode((c & 63) | 128); 
        } 
 
    } 
 
    return utftext; 
}

function changeUser(w, u) {
    if(u != undefined) {
        if(w.parent.location.href.indexOf('ichat')>=0)
            w.parent.location = 'ichat.xhtml?friend='+u;
        else w.parent.location = 'chat.xhtml?friend='+u;
    }
}




/*-----------------------------------------------------------------------------------*/
/*	TABS
/*-----------------------------------------------------------------------------------*/
$(document).ready(function() {
    //Default Action
    $(".tab_content").hide(); //Hide all content
    $("ul.tabs li:first").addClass("active").show(); //Activate first tab
    $(".tab_content:first").show(); //Show first tab content
	
    //On Click Event
    $("ul.tabs li").click(function() {
        //            alert('new tab');
        //                olosh();
        
            
        $("ul.tabs li").removeClass("active"); //Remove any "active" class
        $(this).addClass("active"); //Add "active" class to selected tab
        $(".tab_content").hide(); //Hide all tab content
        var activeTab = $(this).find("a").attr("href"); //Find the rel attribute value to identify the active tab + content
        //        $(activeTab).fadeIn(); //Fade in the active content
        $(activeTab).fadeIn();
        //        $(activeTab).
        return false;
    });

});