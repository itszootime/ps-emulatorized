<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
  <title>${param.title} - emulatorized</title>
  <link href="css/bootstrap.css" rel="stylesheet">
  <link href="css/main.css" rel="stylesheet">
</head>

<body>

<div class="container">
  <div class="main-content">
    <div class="page-header">
      <h1><a href=".">emulatorized</a> &gt; ${param.header}</h1>
    </div>
    <jsp:include page="/WEB-INF/pages/${param.content}.jsp"/>
  </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>
<script src="js/lib/mustache.js"></script>
<script src="js/lib/spin.min.js"></script>
<script src="js/lib/jquery.spin.js"></script>
<script src="js/application.js"></script>
<c:forEach items="${param.scripts}" var="script">
<script src="js/${script}.js"></script>
</c:forEach>

</body>

</html>