<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- Import taglibs --%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="wtree" tagdir="/WEB-INF/tags"%>
<fmt:setBundle basename="messages/viewcontent" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Hedwig Webmail</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="css/smoothness/jquery-ui-1.9.2.custom.css" rel="stylesheet">
<link href="css/skin-vista/ui.dynatree.css" rel="stylesheet">
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/default.css" rel="stylesheet">
<style>
#tabs {
  border: none;
}
#tabs .ui-widget-header {
  border: none;
  background: no-repeat;
}
</style>
<script src="js/jquery/jquery.js"></script>
<script src="js/jquery/jquery.mb.browser.min.js"></script>
<script src="js/jquery/jquery-ui-1.9.2.custom.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/jquery/jquery.cookie.js"></script>
<script src="js/jquery/jquery.dynatree.js"></script>
<script src="js/jquery/jquery.pagination.js"></script>
<script src="js/jquery/jquery.MultiFile.js"></script>
<script>
var $tabs, $tree;
function addtab(id,label,url,data){
  if ($("a[href=#tabs-"+id+"]").length == 0){
    var template = "<li><a href='__href__'>__label__</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>",li = $(template.replace(/__href__/g,"#tabs-"+id).replace(/__label__/g,label));
    $tabs.find(".ui-tabs-nav").append(li).end().append($("<div id='tabs-"+id+"'></div>").load(url,data));
    $tabs.tabs("refresh");
  }
  $("a[href=#tabs-"+id+"]").trigger("click");
}
function removetab(id){
  $("a[href=#tabs-"+id+"]").parent().remove();
  $("#tabs-"+id).remove();
  $tabs.tabs("refresh");
}
$(function(){
	$("#tree").dynatree({
		clickFolderMode:1,
		onActivate:function(dtnode){
			if (dtnode.data.isFolder){
				$("#message-list").load("folder/display",{path:dtnode.data.key,init:true});
        $tabs.find("a[href=#message-list]").text(dtnode.data.title);
        if ($tabs.tabs("option","active") != 0){
          $tabs.tabs("option","active",0);
        }
			}
		},
    onFocus:function(dtnode){
      if ($tabs.tabs("option","active") != 0){
        $tabs.tabs("option","active",0);
      }
    }
	});
  $tree = $("#tree").dynatree("getTree");
  $tabs = $("#tabs").tabs();
  $tabs.delegate("span.ui-icon-close", "click", function(){
    removetab($(this).closest("li").attr("aria-controls").substring(5));  // trim tabs-
  });
  $("#compose").click(function(){
    addtab(this.id,$(this).text(),"message/compose",{});
  });
});
</script>
</head>
<body>
    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <a class="brand" href="#">Hedwig Webmail</a>
          <p class="navbar-text pull-right">
            Welcome <a href="#" class="navbar-link">Username</a>
	        <a href="logout"><i class="icon-white icon-off"></i>Logout</a>
          </p>
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">

        <div class="span2">
          <div class="aside-buttons">
            <a href="#" id="compose" title class="btn btn-primary"><i class="icon-white icon-envelope"></i> <fmt:message key="menu.compose"/></a>
          </div>

          <div id="tree" class="sidebar-nav">
            <ul>
              <!-- special folders -->
              <li class="folder" data="key:'<c:out value="${store.inboxInfo.path}" />'"><fmt:message key="inbox.name" /></li>
              <li class="folder" data="key:'<c:out value="${store.sentMailArchive.path}" />'"><c:out value="${store.sentMailArchive.name}" /></li>
              <li class="folder" data="key:'<c:out value="${store.toSendArchive.path}" />'"><c:out value="${store.toSendArchive.name}" /></li>
              <li class="folder" data="key:'<c:out value="${store.trashInfo.path}" />'"><c:out value="${store.trashInfo.name}" /></li>
              <li class="folder" data="key:'<c:out value="${store.draftInfo.path}"/>'"><c:out value="${store.draftInfo.name}"/></li>
              <!-- personal archive -->
              <wtree:dynatree node="${folder}"/>
            </ul>
          </div><!--/#tree -->

          <div class="divider"></div>

          <div id="quota">
            <div><strong>Quota</strong><strong class="pull-right">60.1/100MB</strong></div>
            <div class="progress slim">
              <div class="bar" style="width: 60%;"></div>
            </div>
          </div>

        </div><!--/span-->

        <div id="tabs" class="content-page span10">
    			<ul>
    				<li><a href="#message-list">INBOX</a></li>
    			</ul>
			    <div id="message-list">
          </div><!--/#message-list-->
        </div><!--/.content-page-->

      </div><!--/row-->

      <hr/>

      <footer>
        <p><!-- Insert footer here --></p>
      </footer>

    </div><!--/.fluid-container-->

</body>
</html>