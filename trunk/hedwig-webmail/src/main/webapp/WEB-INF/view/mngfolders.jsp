<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<div class="mail-header">
</div>
<table id="fldr-table" class="table table-condensed">
	<thead>
		<tr>
			<th><fmt:message key="menu.folder"/></th>
			<th class="text-right"><fmt:message key="label.count"/></th>
			<th class="text-right"><fmt:message key="label.size"/></th>
			<th class="text-right"></th>
		</tr>
	</thead>
	<tbody class="text-right">
        <tr>
        	<td class="text-left">
        		<input type="hidden" name="path" value="${store.inboxInfo.path}"/>
            	<i class="fa fa-inbox"></i> <fmt:message key="prefs.inbox" />
        	</td>
        	<td><fmt:formatNumber value="${store.inboxInfo.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td>
        		<a href="#empty" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.empty'/>"><i class="fa fa-trash-o"></i></a>
        	</td>
        </tr>
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${store.sentMailArchive.path}"/>
            	<i class="fa fa-paper-plane-o"></i> <fmt:message key="prefs.sentmailarchive"/>
        	</td>
        	<td><fmt:formatNumber value="${store.sentMailArchive.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td>
        		<a href="#empty" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.empty'/>"><i class="fa fa-trash-o"></i></a>
        	</td>
        </tr>
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${store.toSendArchive.path}"/>
            	<i class="fa fa-clock-o"></i> <fmt:message key="prefs.tosendfolder"/>
        	</td>
        	<td><fmt:formatNumber value="${store.toSendArchive.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td>
        		<a href="#empty" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.empty'/>"><i class="fa fa-trash-o"></i></a>
        	</td>
        </tr>
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${store.trashInfo.path}"/>
            	<i class="fa fa-trash-o"></i> <fmt:message key="prefs.trashfolder"/>
	        </td>
        	<td><fmt:formatNumber value="${store.trashInfo.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td>
        		<a href="#empty" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.empty'/>"><i class="fa fa-trash-o"></i></a>
        	</td>
	    </tr>
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${store.draftInfo.path}"/>
            	<i class="fa fa-pencil-square-o"></i> <fmt:message key="prefs.draftfolder"/>
        	</td>
        	<td><fmt:formatNumber value="${store.draftInfo.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td>
        		<a href="#empty" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.empty'/>"><i class="fa fa-trash-o"></i></a>
        	</td>
        </tr>
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${store.personalArchive.path}"/>
            	<i class="fa fa-archive"></i> <fmt:message key="prefs.personalfolder"/>
        	</td>
        	<td><fmt:formatNumber value="${store.personalArchive.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td>
        		<a href="#add" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.add'/>"><i class="fa fa-plus"></i></a>
        		<a href="#empty" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.empty'/>"><i class="fa fa-trash-o"></i></a>
        	</td>
    	</tr>
<c:forEach var="folder" items="${folders}">
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${folder.path}"/>
            	<i class="fa fa-folder"></i> <span>${folder.name}</span>
        	</td>
        	<td><fmt:formatNumber value="${folder.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td>
        		<a href="#add" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.add'/>"><i class="fa fa-plus"></i></a>
        		<a href="#remove" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.remove'/>"><i class="fa fa-remove"></i></a>
        		<a href="#rename" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.rename'/>"><i class="fa fa-edit"></i></a>
        		<a href="#empty" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.empty'/>"><i class="fa fa-trash-o"></i></a>
        	</td>
        </tr>
</c:forEach>
        <tr id="template" style="display: none;">
        	<td class="text-left">
          		<input type="hidden" name="path" value=""/>
            	<i class="fa fa-folder"></i> <span></span>
        	</td>
        	<td><fmt:formatNumber value="0"/></td>
        	<td>0 Bytes</td>
        	<td>
        		<a href="#add" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.add'/>"><i class="fa fa-plus"></i></a>
        		<a href="#remove" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.remove'/>"><i class="fa fa-remove"></i></a>
        		<a href="#rename" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.rename'/>"><i class="fa fa-edit"></i></a>
        		<a href="#empty" class="btn btn-default btn-xs" title="<fmt:message key='menu.folder.empty'/>"><i class="fa fa-trash-o"></i></a>
        	</td>
        </tr>
	</tbody>
</table>
<script>
$(function() {
	console.log('mngfolders');

	$('#fldr-table').find('input[name=path]').each(function() {
		var tr = $(this).closest('tr');
		if (tr.css('display') != 'none') {
			$.getJSON('quota', {path:$(this).val()}, function(quotas) {
				if (quotas && quotas.length > 0) {
					if (quotas[0].resources && quotas[0].resources.length > 0) {
						var resource = quotas[0].resources[0];
						tr.find('td').eq(2).text(formatBytes(resource.usage));
					}
				}
			});
		}
	});

	$('#fldr-table').on('click', 'a[href=#empty]', function() {
		var tr = $(this).closest('tr'),
		    path = tr.find('input[name=path]').val();
		$.post('folder/empty', {path:path}, function() {
			tr.find('td').eq(1).text('0');
			tr.find('td').eq(2).text(formatBytes(0));
			if (path == 'INBOX') {
				$('#side-menu').find('#inbox-unread').text('0');
			}
            // refresh quota
            showQuota();
		});
	}).on('click', 'a[href=#add]', function() {
		var tr = $(this).closest('tr'),
		    path = tr.find('input[name=path]').val(),
			tree = $('#tree').fancytree('getTree'),
			node = tree.getNodeByKey(path);
		if (node) {
			eModal.prompt({title:'<fmt:message key="main.folder.promptname"/>'})
				.then(function(name) {
					if (!isValidMboxName(name)) {
						alert('<fmt:message key="main.folder.invalidname"/>\n' + invalid_chars);	
						return;
					}
					if (tree.getNodeByKey(path + wma_separator + name)) {
						alert('<fmt:message key="main.folder.alreadyexist"/>');
						return;
					}
					$.post('folder/create', {name:name,path:path}, function(data) {
						node.addNode(data).makeVisible(true);
						$('#template').clone()
							.attr('id','')
							.css('display', '')
							.find('input[name=path]').val(data.key).end()
							.find('td:first > span').text(data.title).end()
							.insertAfter(tr);
					});
				});
		}
	}).on('click', 'a[href=#remove]', function() {
		var tr = $(this).closest('tr'),
		    path = tr.find('input[name=path]').val(),
			tree = $('#tree').fancytree('getTree'),
			node = tree.getNodeByKey(path);
		if (node) {
			if (!node.hasChildren()) {
				eModal.confirm({
					message: '<fmt:message key="main.folder.confirm.delete"/>',
					label: 'Yes'
				}).then(function() {
					$.post('folder/delete', {path:path}, function() {
						node.remove();
						tr.remove();
					});
				});
			} else eModal.alert('<fmt:message key="main.folder.haschildren"/>');
		}
	}).on('click', 'a[href=#rename]', function() {
		var tr = $(this).closest('tr'),
		    path = tr.find('input[name=path]').val(),
			tree = $('#tree').fancytree('getTree'),
			node = tree.getNodeByKey(path);
		if (node) {
			eModal.prompt({title:'<fmt:message key="main.folder.promptname"/>'})
				.then(function(name) {
					if (!isValidMboxName(name)) {
						alert('<fmt:message key="main.folder.invalidname"/>\n' + invalid_chars);
						return;
					}
					var dest = node.key.substring(0, node.key.lastIndexOf(wma_separator) + 1) + name;
					if (tree.getNodeByKey(dest)) {
						alert('<fmt:message key="main.folder.alreadyexist"/>');
						return;
					}
					$.post('folder/rename', {path:path,destfolder:dest}, function(data) {
						node.key = data.key, node.setTitle(data.title);
						tr.find('input[name=path]').val(data.key),
						tr.find('td:first > span').text(data.title);
					});
				});
		}
	});


});
</script>