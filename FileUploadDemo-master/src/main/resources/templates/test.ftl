<!DOCTYPE html>
<head>
    <meta charset="UTF-8" />
    <title>图片上传Demo</title>
</head>
<body>
<h1 >图片上传Demo</h1>
<form action="fileUpload" method="post" enctype="multipart/form-data">
    <p>选择文件: <input type="file" name="fileName"/></p>
    <p><input type="submit" value="提交"/></p>
</form>
<#--判断是否上传文件-->
<#if msg??>
    <span>${msg}</span><br>
<#else >
    <span>${msg!("文件未上传")}</span><br>
</#if>
<#--显示图片，一定要在img中的src发请求给controller，否则直接跳转是乱码-->
<#if fileName1??>
<img src="/show?fileName=${fileName1}" style="width: 100px"/>
<#else>
<img src="/show" style="width: 200px"/>
</#if>
<h2>320x400</h2>
<#if fileName2??>
<img src="/show?fileName=${fileName2}" style="width: 100px"/>
<#else>
<img src="/show" style="width: 200px"/>
</#if>
<h2>240x300</h2>
<#if fileName3??>
<img src="/show?fileName=${fileName3}" style="width: 100px"/>
<#else>
<img src="/show" style="width: 200px"/>
</#if>
<h2>160x200</h2>
<#if fileName4??>
<img src="/show?fileName=${fileName4}" style="width: 100px"/>
<#else>
<img src="/show" style="width: 200px"/>
</#if>
<h2>500x280</h2>
<#if fileName5??>
<img src="/show?fileName=${fileName5}" style="width: 100px"/>
<#else>
<img src="/show" style="width: 200px"/>
</#if>
<h2>375x210</h2>
<#if fileName6??>
<img src="/show?fileName=${fileName6}" style="width: 100px"/>
<#else>
<img src="/show" style="width: 200px"/>
</#if>
<h2>246x138</h2>
<#if fileName7??>
<img src="/show?fileName=${fileName7}" style="width: 100px"/>
<#else>
<img src="/show" style="width: 200px"/>
</#if>
<h2>182x102</h2>
<#if fileName8??>
<img src="data:image/png;base64,${fileName8}" style="width: 100px"/>
<#else>
<img src="/show" style="width: 200px"/>
</#if>
</body>
</html>
