var epub = null;

window.onload = function () {
    /**
     * 初始化epub对象
     */
    if (init) {
        init();
        if (epub) {
            if (start) {
                start();
            }
            epub.loadComplete();
        } else {
            alert("epub init fail");
        }
    }
    //回掉loadComplete
    else {
        alert("load ok,but init fail");
    }
};


function initEPUB(w, h, platform) {
    epub = new $.Epub();
    if (epub == null) {
        alert("init epub fail");
        return;
    }
    epub.init(w, h);
    epub.setPlatform(platform);
}

/*
 function init()
 {
 initEPUB(768,1024,"ios");
 }

 function start()
 {
 epub.setStartPage(1);
 }
 */

