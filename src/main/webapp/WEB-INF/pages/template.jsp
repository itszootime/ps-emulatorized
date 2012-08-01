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
      <h1>${param.header}</h1>
    </div>
    <jsp:include page="/WEB-INF/pages/${param.content}.jsp"/>
  </div>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script src="js/lib/mustache.js"></script>
<c:forEach items="${param.scripts}" var="script">
<script src="js/${script}.js"></script>
</c:forEach>

</body>

</html>