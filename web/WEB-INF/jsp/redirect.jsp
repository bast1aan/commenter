<html>
<body>
<h1>Jsp!</h1>
<% 
Object value = request.getAttribute("var");
if (value != null && value instanceof String) {
%>
<%= value %>
<% } else { %>
Value not found
<% } %> 

</body>
</html>