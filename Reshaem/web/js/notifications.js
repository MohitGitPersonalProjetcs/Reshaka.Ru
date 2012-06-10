var NE = function () {
    
    this.handlers = [];
    
    this.init = function (func, interval) {
        setInterval(func, interval);
    }
    
    this.addListener = function (f) {
       this.handlers.push(f);
    }
    
    this.handleNotifications = function(xhr, status, args) {
        if(args.ok == true) {
            var notify = eval('('+ args.notifications +')');
            for(j=0; j<notify.length; j++)
                for(i=0; i<this.handlers.length;i++) {
                    this.handlers[i](notify[j])
                }
        }
    }
}