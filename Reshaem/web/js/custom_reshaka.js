function gup( name )
{
    name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    var regexS = "[\\?&amp;#]"+name+"=([^&amp;#]*)";
    var regex = new RegExp( regexS );
    var results = regex.exec( window.location.href );
    if( results == null )
        return "";
    else
        return results[1];
}

                
function getAfterJail(){
    var s = window.location.toString();
    if (s.indexOf("#") == -1) 
        return "";
    else return s.substring(s.indexOf("#"));
}
                
function getBeforeJail(){
    var s = window.location.toString();
    if (s.indexOf("#") == -1) 
        return s;
    else return s.substring(0,s.indexOf("#"));
}
                
function setParameter(paramName , paramValue){
    var s = window.location.toString();
    var before = getBeforeJail();
    var after = getAfterJail();
    var q = gup(paramName);
    if (q == ""){ // если такой параметр еще не задан
        if (after == ""){
            after = "#" + paramName + "=" + paramValue;
        }
        else {
            after = after + "&amp;"+ paramName + "=" + paramValue;
        }
    } else {  //если же найден, то заменяем paramValue
        var search = new RegExp('(\\b'+paramName+'\\b)=(\\w+)', 'gi');
        var after = after.replace(search, "$1="+paramValue);
    }
    window.location = before + after;     
} 
            
            
            
function orderDialogs(role){
    //                    s = gup("order_step");
    s = "";
    if (gup("operation") == "order") s = gup("step");
                    
    if (s == "1"){
        setLinkForLoginzaFrame("order", "2");
        dlg_login.show();
    }
    if (s == "2") if (role == 2) dlg_makeorder.show();
}
                
                
function loginDialogs(role){
    s = "";
    if (gup("operation") == "login") 
        dlg_login.show();
}
            
function partnerDialogs(role){
    s = "";
    if (gup("operation") == "partner") s = gup("step");
                    
    if (s == "1") {
        setLinkForLoginzaFrame("partner", "2");
        dlg_login.show();
    }
    if (s == "2") if (role == 2) partner_dlg.show();
}
                
function onlineDialogs(role){
    s = "";
    if (gup("operation") == "online") s = gup("step");
                    
    if (s == "1") {
        setLinkForLoginzaFrame("online", "2");
        dlg_login.show();
    }
    if (s == "2") if (role == 2) dlg_makeonline.show();
}
            
function orderButtonClicked(role){
    if (role == 2){
        setParameter("operation", "order");
        setParameter("step", "2");
    }
    if (role == 0){
        setParameter("operation", "order");
        setParameter("step", "1");
                        
    }
    orderDialogs(role);
}
            
            
function partnerButtonClicked(role){
    if (role == 0){
        setParameter("operation", "partner");
        setParameter("step", "1");
    //     setLinkForLoginzaFrame("partner", "1");
    }
    if (role == 2) {
        setParameter("operation", "partner");
        setParameter("step", "2");
    }
    //                    window.location.reload();
    partnerDialogs(role);
}
            
function onlineButtonClicked(role){
    if (role == 0){
        setParameter("operation", "online");
        setParameter("step", "1");
    //    setLinkForLoginzaFrame("online", "1");
    }
    if (role == 2) {
        setParameter("operation", "online");
        setParameter("step", "2");
    }
    //                    window.location.reload();
    //  alert("onlineDialogs()");
    onlineDialogs(role);
}
            
            
            
function logInButtonClicked(){

    if (gup("operation") == "order") 
    {
        setParameter("step", "2");
    }
                    
    if (gup("operation") == "partner")
    {
        setParameter("step", "2");
    }
                    
    if (gup("operation") == "online")
    {
        setParameter("step", "2");
    }
    window.location.reload();
}
            
function submitButtonClicked(){
    dlg_makeorder.hide();
    dlg_makeonline.hide();
    //                        setParameter("order_step", "0");
    setParameter("operation", "order");
    setParameter("step", "0");
}
            
function dialogsOpener(role){
    myOrdersButtonClicked();
    onlineDialogs(role);
    orderDialogs(role);
    partnerDialogs(role);
    loginDialogs(role);
}
                
function setLinkForLoginzaFrame(operation , step){
    var s0 = "https://loginza.ru/api/widget?token_url=http%3A%2F%2Freshaka.ru%2FopenId";
    var sk = "&providers_set=vkontakte,facebook,mailru,google,yandex&lang=ru&overlay=loginza";
    var s2= "%3Foperation%3D"+operation+"%26step%3D"+step;
    var sr = s0+s2+sk;
    frame = document.getElementById('openIdIframe');
    frame.setAttribute('src',sr);
    elem = document.getElementById("openIdScript");
    elem.setAttribute("src", "http://loginza.ru/js/widget.js");
}
            
function initOpenIdIframe(){
    var elem = document.getElementById("openIdIframe");
    elem.setAttribute("src", "https://loginza.ru/api/widget?token_url=http%3A%2F%2Freshaka.ru%2FopenId&providers_set=vkontakte,facebook,mailru,google,yandex&lang=ru&overlay=loginza");
    elem = document.getElementById("openIdScript");
    elem.setAttribute("src", "http://loginza.ru/js/widget.js");
}
            
function greetings(xhr, status, args){
    if(args.validationFailed || !args.first) {  
    //do something if auth failed
    //                jQuery('#login_dialog').effect("shake", { times:3 }, 100);
    } else {  
        //  alert("i should hide dialog");
        first_order_dlg.show();
    //dlg_login.hide();
    }  
}
                
function clearUrlParameters(){
    // alert("asas");
    var s = getBeforeJail() + "#";
    window.location = s;
//return s;              
}
                
function myOrdersButtonClicked(){
    if (gup("operation") == "myorder"){
        myOrdersFilter(); 
    }
}
              
function reshakaMyOrdersButtonClicked(){
    if (gup("operation") == "myorder"){
        reshakaMyOrdersFilter();
    }
}
                
//                function radioOnlineClicked(){
//                    if (gup("operation") == "onlineorder"){
//                        
//                    }
//                }
              
function getTextForLoginDialog(){
    if (gup("operation") == "order")
        if (gup("step") == "1"){
            return "Прежде чем заказать решение нужно войти в систему.";
        }
    if (gup("operation") == "online")
        if (gup("step") == "1"){
            return "Прежде чем заказать оперативную онлайн-помощь нужно войти в систему.";
        }
    return "";
}
        
function onLoginDialogShow(){
    var d = document.getElementById("login_dialog_message");
    d.innerHTML = getTextForLoginDialog();
}
                
function handleRegisterRequest(xhr, status, args) {  
    if(args.validationFailed || !args.registered) {  
        jQuery('#registration_dialog').effect("shake", {
            times:3
        }, 100);
    } else {  
        logInButtonClicked();
    }  
}  
                
function handleRecoverRequest(xhr, status, args) {  
    if(args.validationFailed || !args.recovery) {  
        //do something if auth failed
        jQuery('#recover_dialog').effect("shake", {
            times:3
        }, 100);
    } else {  
        dlg_recover.hide();
    }  
}  

function handleLoginRequest(xhr, status, args) {  
    if(args.validationFailed || !args.loggedIn) {  
        jQuery('#login_dialog').effect("shake", {
            times:3
        }, 100);
    } else {  
        logInButtonClicked();
    }  
}

function initSlider(){
    $("#slider").easySlider({
        auto: true,
        continuous: true,
        pause: 8000,
        prevText: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;',
        nextText: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
    });
}
