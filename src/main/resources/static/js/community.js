/*
* 提交回复
*/
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId,1,content);
}

function comment2target(targetId, type, content) {
    if (!content){
        alert("不能回复空内容~")
        return;
    }
    $.ajax({
        url:"/comment",
        type:"POST",
        dataType:"JSON",
        contentType:"application/json",
        data:JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success:function (rs) {
            if (rs.code == 200){
                window.location.reload();
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

function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-"+commentId).val();
    comment2target(commentId,2,content);
}

/*
*  展开/收起 二级评论
*/
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-"+id);
    //获取二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {  //收起
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {  //展开
        var subCommentContainer = $("#comment-"+id);
        if (subCommentContainer.children().length != 1){
            //展开二级评论
            comments.addClass("in");
            //标记评论展开状态
            e.setAttribute("data-collapse","in");
            e.classList.add("active");
        } else {
            $.getJSON("/comment/"+id, function (data) {
                var items = [];
                $.each(data.data.reverse(), function(index, comment){

                    var mediaLeftElement = $("<div/>",{
                        "class":"media-left"
                    }).append($("<img/>",{
                        "class":"media-object img-thumbnail",
                        "src":comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>",{
                        "class":"media-body"
                    }).append($("<h5/>",{
                        "class":"media-middle sessionUserName",
                        "html":comment.user.name
                    })).append($("<div/>",{
                        "html":comment.content
                    })).append($("<div/>",{
                        "class":"menu"
                    }).append($("<span/>",{
                        "class":"pull-right dateTime",
                        "html":moment(comment.gmtCreate).format("YYYY-MM-DD")
                    })));

                    var mediaElement = $("<div/>",{
                        "class":"media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>",{
                        "class":"col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement).append($("<hr/>",{
                        "class":"comment-sp"
                    }));
                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                //标记评论展开状态
                e.setAttribute("data-collapse","in");
                e.classList.add("active");
            });
        }
    }
}

//点击tag
function selectTag(e) {
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    //判断tag的输入框中的值是否包含该tag
    if (previous.indexOf(value) == -1){  //不包含
        if (previous) {  //输入框中有值,拼接逗号
            $("#tag").val(previous + "," + value);
        } else {  //输入框中无值,直接加
            $("#tag").val(value);
        }
    }
}

function showSelectTag() {
    $("#select-tag").show();
}