<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- Import taglibs --%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<script>
$(function() {
  $("#msg-pagination").pagination(<c:out value="${pager.itemCount}"/>,{
    items_per_page:<c:out value="${pager.pageSize}"/>,
    current_page:<c:out value="${pager.pageNumber}"/>,
    callback:function(page){
      $("#message-list").load("folder/display",{path:$("input[name=path]").val(),page:page});
    }
  });
  $("#checkall").on("click",function(){
    var b = this.checked;
    $("input[name=numbers]").each(function(){
      this.checked = b;
    });
  });
  $("select[name=criteria]").val("<c:out value="${param['criteria']}"/>");
  $("select[name=criteria]").change(function(){
    if ($.inArray($(this).val(),["all","unread","flagged"])>=0){
      $("input[name=term]").val("");
      $("#search").trigger("click");
    }
  });
  $("#delete,#purge").on("click",function(){
    if ($("input[name=numbers]:checked").length==0){
      alert("<fmt:message key="message.select.message"/>");
      return;
    }
    if (!confirm("<fmt:message key="message.confirm.delete"/>")){
      return;
    }
    var data = $("form[name=msg-form]").serializeArray();
    data.push({name:"purge",value:$(this).attr("id")=="purge"});
    $.post("message/delete",data,function(){
        $("#message-list").load("folder/display",{path:$("input[name=path]").val(),page:$("input[name=page]").val()});
    });
  });
  $("#read").on("click",function(){
    var numbers = $("input[name=numbers]:checked").map(function(){return $(this).val();}).get();
    if (numbers.length>0){
      $.post("message/setflag",{path:$("input[name=path]").val(),numbers:numbers.join(','),flag:"seen",set:true},
        function(data){
          $.each(numbers,function(i,num){
            $("#"+num).find("img[src^=images\\/msg]:first").attr("src","images/msg-seen.png");
          });
      },"json").fail(function(){alert("error");});
    }else{
      alert("<fmt:message key="message.select.message"/>");
    }
  });
  $("span[name=star]").on("click",function(){
    var that = $(this);
    $.post("message/setflag",{path:$("input[name=path]").val(),numbers:that.parents("tr:first").attr("id"),flag:"flagged",set:that.hasClass("on")},function(data){
      that.toggleClass("on");
    });
  });
  $("#search").on("click",function(){
    $("input[name=page]").val(0);
    $("#message-list").load("folder/display",$("form[name=msg-form]").serializeArray());
  });
  $("a[href=#open]").on("click",function(){
    var id = $(this).parents("tr:first").attr("id");
    addtab(id,$(this).text(),"message/display",{path:$("input[name=path]").val(),number:id});
  });
  $("a[href=#mime]").on("click",function(){
    $("input[name=number]").val($(this).parents("tr:first").attr("id"));
    $("form[name=msg-form]").attr("action","message/displaymime").submit();
  });
  $("#move").on("click",function(){
    if ($("input[name=numbers]:checked").length == 0){
      alert('<fmt:message key="message.select.message"/>');
      return;
    }
    if (!$(this).hasClass("open")){
      var menu = $(this).siblings(".dropdown-menu");
      menu.empty();
      var path = $tree.getActiveNode().data.key;
      $tree.visit(function(dtnode){
        if (dtnode.data.key === path) menu.append("<li class=\"disabled\"><a href=\"#"+dtnode.data.key+"\">"+dtnode.data.title+"</a></li>\n");
        else menu.append("<li><a href=\"#"+dtnode.data.key+"\">"+dtnode.data.title+"</a></li>\n");
      },false);
    }
  }).parent().on("click",".dropdown-menu a",function(){
    var data = $("form[name=msg-form]").serializeArray();
    data.push({name:"destination",value:$(this).attr("href").substring(1)});
    $.post("message/move",data,function(){
        $("#message-list").load("folder/display",{path:$("input[name=path]").val(),page:$("input[name=page]").val()});
    });
  });
});
</script>
<form name="msg-form" method="post" target="_blank">
  <input type="hidden" name="init" value="true"/>
  <input type="hidden" name="path" value="<c:out value="${param['path']}"/>"/>
  <input type="hidden" name="page" value="<c:out value="${pager.pageNumber}"/>"/>
  <input type="hidden" name="number"/>
  <div class="btn-toolbar">
    <div class="btn-group">
      <a id="delete" href="#" class="btn"><fmt:message key="menu.delete"/></a>
      <a id="purge" href="#" class="btn"><fmt:message key="menu.purge"/></a>
    </div>
    <div class="btn-group">
      <a id="read" href="#" class="btn"><fmt:message key="menu.read"/></a>
      <a id="upload" href="#" class="btn"><fmt:message key="menu.upload"/></a>
    </div>
    <div class="btn-group">
      <a id="move" href="#" class="btn dropdown-toggle" data-toggle="dropdown">
        <fmt:message key="menu.move"/>
        <span class="caret"></span>
      </a>
      <ul class="dropdown-menu">
      </ul>
    </div>
    <a href="#" class="btn"><i class="icon-refresh"></i></a>
    <div class="input-append input-prepend pull-right">
      <select name="criteria">
        <option value="subject"><fmt:message key="message.subject"/></option>
        <option value="from"><fmt:message key="message.from"/></option>
        <option value="to"><fmt:message key="message.to"/></option>
        <option value="all"><fmt:message key="menu.filter.all"/></option>
        <option value="unread"><fmt:message key="menu.filter.unread"/></option>
        <option value="flagged"><fmt:message key="menu.filter.flagged"/></option>
      </select>
      <input class="input-medium search-query" type="text" placeholder="Search" name="term" value="<c:out value="${param['term']}"/>"/>
      <a href="#" class="btn" id="search"><i class="icon-search"></i></a>
    </div>
  </div><!--/.btn-toolbar-->
  <table class="table table-hover table-condensed">
    <thead>
    <tr>
      <th><input type="checkbox" id="checkall"></th>
      <th><fmt:message key="message.status"/></th>
      <th><fmt:message key="message.from"/></th>
      <th><a href="#"><fmt:message key="message.subject"/></a></th>
      <th><span class="star"></span></th>
      <th><fmt:message key="message.receiveddate"/></th>
      <th><fmt:message key="message.size"/></th>
    </tr>
    </thead>
    <tbody>
      <c:forEach var="msg" items="${messages}">
        <tr class="strong" id="<c:out value="${msg.number}"/>">
          <td><input type="checkbox" name="numbers" value="<c:out value="${msg.number}"/>"/></td>
          <td>
            <c:choose>
              <c:when test="${msg.recent}"><img src="images/msg-new.png"/></c:when>
              <c:when test="${msg.read}"><img src="images/msg-seen.png"/></c:when>
              <c:otherwise><img src="images/msg.png"/></c:otherwise>
            </c:choose>
            <c:if test="${msg.multipart}"><img src="images/attach.gif" alt="<fmt:message key="message.attach"/>"/></c:if>
            <c:if test="${msg.priority < 3}"><img src="images/urgent.gif" alt="<fmt:message key="message.urgent"/>"/></c:if>
            <c:if test="${msg.secure}"><img src="images/secure.gif" alt="<fmt:message key="message.secure"/>"/></c:if>
            <c:if test="${msg.answered}"><img src="images/reply.gif" alt="<fmt:message key="message.reply"/>"/></c:if>
          </td>
          <td>
            <c:out value='${msg.from}'/>
          </td>
          <td><a href="#open"><c:out value="${msg.subject}"/></a></td>
          <td><span name="star" class="star<c:if test="${msg.flagged}"> on</c:if>"></span></td>
          <td><fmt:formatDate value='${msg.receivedDate}' pattern="yyyy.MM.dd HH:mm"/></td>
          <td class="text-right"><a href="#mime"><c:out value="${msg.size}"/></a></td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  <hr/>
  <div id="msg-pagination" class="pagination pagination-right">
  </div>
</form>