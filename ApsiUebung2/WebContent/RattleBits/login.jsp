<%@ page import="java.util.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>RattleBits</title>
<style>
.error {
	color: red;
}
.error input {
	border: 1px solid red;
}
</style>
</head>
<body bgcolor="LightGray">
<form method="POST" action='/ApsiUebung2/RattleBits/Login' name="login">
<ul class="error">
<%
	@SuppressWarnings("unchecked")
    List<String> messages = (List<String>)request.getAttribute("messages");
	Iterator<String> it = messages.iterator();
    while (it.hasNext()) {
%>
	<li><%= it.next() %></li>
<% } %>
</ul>
<table>
	<tr>
		<th>Username:</th>
		<td><input type="text" name="username" value="<%= request.getAttribute("username") %>"/></td>
	</tr>
	<tr>
		<th>Password:</th>
		<td><input type="password" name="password" value="" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" name="login" value="Einloggen" /></td>
	</tr>
</table>
</form>
</body>
</html>