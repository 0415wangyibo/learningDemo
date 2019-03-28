window.onunload = init();

function init() {
    getList();
}

function btnDelete(downName) {
    console.log(downName);
    $.ajax({
        type: "delete",
        url:"/download/job/"+downName,
        dataType: "json",
        success: function (resp) {
            var data = resp.data;
            if(data.ifOk==true) {
                toastr.success("success");
            }else {
                toastr.info(data.reason);
            }
            /**
             * 处理删除成功还是失败的逻辑
             */
            getList();
        },
        error: function () {
            toastr.error('请求失败');
        }
    });
}

function getList() {
    $.ajax({
        type: "get",
        url: "/download/list",
        dataType: "json",
        success: function (resp) {
            initTable(resp.data);
        },
        error: function () {
            toastr.error('获取任务列表失败');
        }
    });

    function initTable(data) {
        var jobTable = $("#jobTable");
        jobTable.empty();
        if (null != data && data.length > 0) {
            var i = data.length - 1;
            var len = data.length;
            for (; i >= 0; i--) {
                var item = data[i];
                jobTable.append(
                    '<tr class="table-success">' +
                    '<td>' + (len - i) + '</td>' +
                    '<td>' + item.downName + '</td>' +
                    '<td>' + changeSize(item.downLoadTotal) + '</td>' +
                    '<td>' + changeSize(item.loadedTotal) + '</td>' +
                    '<td>' + changeNumber(item.fileNumber) + '</td>' +
                    '<td>' + changeNumber(item.loadedFileNumber) + '</td>' +
                    '<td>' + changeStatus(item.status) + '</td>' +
                    '<td>' + changeString(item.downloadNow) + '</td>' +
                    '<td>' + changeSize(item.downloadNowSize) + '</td>' +
                    '<td>' + changeSize(item.uploadTotal) + '</td>' +
                    '<td>' + changeSize(item.uploadedTotal) + '</td>' +
                    '<td>' + changeNumber(item.uploadFileNumber) + '</td>' +
                    '<td>' + changeNumber(item.uploadedFileNumber) + '</td>' +
                    '<td>' + changeUploadStatus(item.uploadStatus) + '</td>' +
                    '<td>' + changeString(item.uploadNow) + '</td>' +
                    '<td>' + changeSize(item.uploadNowSize) + '</td>' +
                    '<td><button  onclick="btnDelete(' + item.downName + ')">删除</button>' + '</td>' +
                    '</tr>');
            }
        }
    }
}

function changeString(message) {
    if (null == message) {
        message = "";
    } else {
        var index = message.lastIndexOf("/");
        message = message.substring(index + 1, message.length)
    }
    return message;
}

function changeSize(size) {
    var sizeString;
    if (null == size || size == 0) {
        sizeString = "";
    }
    if (null != size && size > 0 && size < (1024 * 1024)) {
        sizeString = (size / 1024).toFixed(2) + "KB";
    }
    if (null != size && size >= (1024 * 1024) && size < (1024 * 1024 * 1024)) {
        sizeString = (size / 1024 / 1024).toFixed(2) + "MB";
    }
    if (null != size && size >= (1024 * 1024 * 1024)) {
        sizeString = (size / 1024 / 1024 / 1024).toFixed(2) + "GB";
    }
    return sizeString;
}

function changeStatus(status) {
    var statusString;
    if (null == status) {
        return "错误";
    }
    if (status == 0) {
        statusString = "等待下载";
    }
    if (status == 1) {
        statusString = "正在下载";
    }
    if (status == -1) {
        statusString = "下载异常";
    }
    if (status == 2) {
        statusString = "正在核对";
    }
    if (status == -3) {
        statusString = "文件不存在";
    }
    if (status == 3) {
        statusString = "下载完成";
    }
    return statusString;
}

function changeNumber(num) {
    if (null == num || num == 0) {
        return "";
    }
    return num;
}

function changeUploadStatus(uploadStatus) {
    var statusString;
    if (null == status) {
        return "错误";
    }
    if (uploadStatus == 0) {
        statusString = "等待上传";
    }
    if (uploadStatus == 1) {
        statusString = "正在上传";
    }
    if (uploadStatus == -1) {
        statusString = "上传异常";
    }
    if (uploadStatus == 2) {
        statusString = "正在核对";
    }
    if (uploadStatus == -3) {
        statusString = "文件不存在";
    }
    if (uploadStatus == 3) {
        statusString = "上传完成";
    }
    return statusString;
}