$(function() {
    function sendFileToServer(formData,status){
        var uploadURL ="/generateQR";
        $.ajax({
            url: uploadURL,
            type: "POST",
            contentType:false,
            processData: false,
            cache: false,
            data: formData,
            timeout: 30000,
            beforeSend: function(xhr, settings) {
                //送信前の処理
            },
            complete: function(xhr, textStatus) {
                //通信完了
            },
            success: function(result, textStatus, xhr) {
                //ajax通信が成功した
                $("#status1").append("File upload Done<br>");
                $('#status1').append('<a href="qr_code">QRコード</a><br>');
            },
            error: function(xhr, textStatus, error) {
                //ajax通信が失敗した
                $('#status1').append('送信に失敗しました<br>');
            }
        });
    }
    function handleFileUpload(files,obj){
            var fd = new FormData();
            for (var i = 0; i < files.length; i++){
                fd.append('files', files[i]);
            }
            var schema = document.forms.schemaField.schema.value;
            fd.append('schema',schema)
            sendFileToServer(fd,status);
    }

    $(document).ready(function(){
        var obj = $("#dragdroparea");
        obj.on('dragenter', function (e) {
            e.stopPropagation();
            e.preventDefault();
        });
        obj.on('dragover', function (e) {
            e.stopPropagation();
            e.preventDefault();
        });
        obj.on('drop', function (e) {
            e.preventDefault();
            var files = e.originalEvent.dataTransfer.files;
            handleFileUpload(files);
        });
        $(document).on('dragenter', function (e){
            e.stopPropagation();
            e.preventDefault();
        });
        $(document).on('dragover', function (e){
            e.stopPropagation();
            e.preventDefault();
        });
        $(document).on('drop', function (e){
            e.stopPropagation();
            e.preventDefault();
        });
    });
});