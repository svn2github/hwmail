<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<%@ taglib prefix="wtree" tagdir="/WEB-INF/tags" %>
<%@ attribute name="node" type="com.hs.mail.webmail.model.impl.WmaFolderImpl" required="true" %>
<li class="folder" data="key:'<c:out value="${node.path}"/>'"><c:out value="${node.name}"/>
<c:if test="${not empty node.subfolders}">
	<ul>
	<c:forEach var="child" items="${node.subfolders}">
		<wtree:dynatree node="${child}"/>
	</c:forEach>
	</ul>
</c:if>
</li>