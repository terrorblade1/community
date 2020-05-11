function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();7
    $.ajax({
        url:"/comment",
        type:"POST",
        dataType:"JSON",
        contentType:"application/json",
        data:JSON.stringify({
            "parentId":questionId,
            "content":content,
            "type":1
        }),
        success:function (rs) {
            if (rs.code == 200){
                $("#comment_section").hide();
            } else {
                if (rs.code == 2003){
                    var isAccepted = confirm(rs.message);
                    if (isAccepted){
                        //登录
                        window.open("https://github.com/login/oauth/authorize?client_id=35e70ded9fbe17eed68f&redirect_uri=http://localhost:8888/callback&scope=user&state=1");
                        window.localStorage.setItem("closable",true);
                    }
                } else {
                    alert(rs.message);
                }
            }
        },
        error:function (rs) {
            console.log(rs.message);
        }
    });
}