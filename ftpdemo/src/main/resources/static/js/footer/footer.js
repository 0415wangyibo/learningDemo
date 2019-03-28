window.onunload = init();

function init() {
    var time = new Date().getFullYear();
    var foot= $("#foot");
    foot.empty();
    foot.append("<p>仅供学习使用 © 2019-" + time + "——by wangyb</p>");
    initToastr();
}
function initToastr() {
    toastr.options = {
        closeButton: false,
        debug: false,
        progressBar: false,
        positionClass: "toast-top-center",
        onclick: null,
        showDuration: "300",
        hideDuration: "0",
        timeOut: "1000",
        extendedTimeOut: "1000",
        showEasing: "swing",
        hideEasing: "linear",
        showMethod: "fadeIn",
        hideMethod: "fadeOut"
    };
}