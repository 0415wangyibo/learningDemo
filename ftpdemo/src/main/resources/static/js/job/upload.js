function addUploadJob() {
    var downName = document.getElementById("downName").value;
    console.info(downName);
    if (null == downName || downName=="") {
        toastr.info("文件夹名称不能为空");
    } else {
        $.ajax({
            type: "post",
            url: "/upload/job",
            dataType: "json",
            data: {
                "downName": downName
            },
            success: function (resp) {
                var data = resp.data;
                if (data.ifOk == true) {
                    toastr.success(data.reason);
                } else {
                    toastr.info(data.reason);
                }
            },
            error: function () {
                toastr.error('请求失败');
            }
        });
    }
}

function selectUploadResult() {
    var jobName = document.getElementById("jobName").value;
    if (null == jobName || jobName=="") {
        toastr.info("文件夹名称不能为空");
    } else {
        $.ajax({
            type: "get",
            url: "/upload",
            dataType: "json",
            data: {
                "downName": jobName
            },
            success: function (resp) {
                setResult(resp.data);
                toastr.success("success");
            },
            error: function () {
                toastr.error('请求失败');
            }
        });
    }
}

function setResult(data) {
    var result = $("#selectResult");
    result.empty();
    result.append('<h3>核查结果</h3>');
    result.append('<p>文件夹名：' + data.downName + '</p>');
    result.append('<p>查询结果:' + changeStatus(data.uploadStatus) + '</p>');
    if (data.uploadStatus == -2) {
        result.append('<p>缺失路径列表：' + '</p>');
        var list = data.uploadMissPath;
        console.info(list);
        if (null != list && list.length>0) {
            var i = 0;
            var end = list.length;
            for (;i<end;i++){
                result.append('<li>'+list[i]+'</li>');
            }
        }
    }
}

function changeStatus(status) {
    var statusString;
    if (null == status) {
        return "错误";
    }
    if (status == 0) {
        statusString = "等待上传";
    }
    if (status == 1) {
        statusString = "正在上传";
    }
    if (status == -1) {
        statusString = "登录ftp失败";
    }
    if (status == 2) {
        statusString = "正在核对";
    }
    if (status == -2) {
        statusString = "文件不完整";
    }
    if (status == -3) {
        statusString = "ftp中不存在该文件夹";
    }
    if (status == 3) {
        statusString = "文件上传完整";
    }
    return statusString;
}