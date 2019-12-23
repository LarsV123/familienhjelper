<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Beneficiaries</title>
    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/common.css" rel="stylesheet">
    <script defer src="${contextPath}/resources/js/all.js"></script>

</head>
<body>

<div class="container">
  	<div class="row">
      <div class="col-sm-2">
          <jsp:include page="includes/menu.jsp" />
      </div>
      <div class="col-sm-10">
          <header>
              <div class="container">
                  <h4>Beneficiaries</h4>
                  <p>
                      <a class="btn btn-primary btn-sm" href="/beneficiaries/new">Add New Beneficiary</a>
                  </p>
              </div>
          </header>
          <form action="<c:url value="/beneficiaries"/>" method="POST">
              <input type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>

              <table id="beneficiaryTable" class="table-grid">
                  <c:if test="${list.size()>0}">
                      <tr>
                          <th onclick="sortTable(0,'beneficiaryTable')"></th>
                          <th onclick="sortTable(1,'beneficiaryTable')">Name</th>
                          <th onclick="sortTable(2,'beneficiaryTable')">Income</th>
                          <th onclick="sortTable(3,'beneficiaryTable')">Field Contact</th>
                      </tr>
                  </c:if>
                  <c:forEach items="${list}" var="list">
                      <tr>
                          <td>
                              <a href="/beneficiaries/${list.id}">
                                  <img src="${contextPath}/resources/img/icons8-edit-16.png">
                                  <!--span><i class="far fa-hand-pointer"></i></span-->
                              </a>
                          </td>
                          <td>${list.name}</td>
                          <td>${list.income}</td>
                          <td>${list.user.username} ${list.user.lastName} ${list.user.firstName}</td>
                      </tr>
                  </c:forEach>
              </table>
          </form>
      </div>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${contextPath}/resources/js/app.js"></script>

</body>
</html>