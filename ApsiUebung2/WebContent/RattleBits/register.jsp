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
<h1>RattleBits AG</h1>
<form method="POST" action='/ApsiUebung2/RattleBits/Register' name="register">
<ul class="error">
<%
    List<String> messages = (List<String>)request.getAttribute("messages");
	Iterator<String> it = messages.iterator();
    while (it.hasNext()) {
%>
	<li><%= it.next() %></li>
<% } %>
</ul>
<table>
	<tr>
		<th>Firma:</th>
		<td><input type="text" name="firma" value="<%= request.getAttribute("firma") %>"/></td>
	</tr>
	<tr>
		<th>Adresse:</th>
		<td><input type="text" name="address" value="<%= request.getAttribute("address") %>" /></td>
	</tr>
	<tr>
		<th>PLZ:</th>
		<td><input type="text" name="plz" value="<%= request.getAttribute("plz") %>" /></td>
	</tr>
	<tr>
		<th>Stadt:</th>
		<td><input type="text" name="town" value="<%= request.getAttribute("town") %>" /></td>
	</tr>
	<tr>
		<th>E-Mail:</th>
		<td><input type="text" name="mail" value="<%= request.getAttribute("mail") %>" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" name="register" value="Registrieren" /></td>
	</tr>
</table>
</form>
</body>
</html>