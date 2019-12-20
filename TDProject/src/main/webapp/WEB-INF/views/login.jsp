<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.security.SecureRandom"%>
<%@ page import="java.math.BigInteger"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
   <c:if test="${userId eq null}">
      <!-- 카카오 로그인 배너 -->
      <a
         href="https://kauth.kakao.com/oauth/authorize?client_id=48bdb629c0cc1beac9d7d5f5cdead8b2&redirect_uri=http://localhost:8000/kakaoLogin&response_type=code">
         <img src="/img/kakao_account_login_btn_medium_wide_ov.png">
      </a>
      
      <!-- 네이버 로그인 배너 -->
      <%
         String clientId = "YgSTzaDFAOIL6DsaS9Cy";//애플리케이션 클라이언트 아이디값";
            String redirectURI = URLEncoder.encode("http://localhost:8000/naverLogin", "UTF-8");
            SecureRandom random = new SecureRandom();
            String state = new BigInteger(130, random).toString();
            String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
            apiURL += "&client_id=" + clientId;
            apiURL += "&redirect_uri=" + redirectURI;
            apiURL += "&state=" + state;
            session.setAttribute("state", state);
      %>
      <a href="<%=apiURL%>"><img height="50"
         src="http://static.nid.naver.com/oauth/small_g_in.PNG" /></a>


      <!-- <a
         href="https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=YgSTzaDFAOIL6DsaS9Cy&redirect_uri=http://localhost:8000/naverLogin&state=hLiDdL2uhPtsftcU">
         <img height="50"
         src="http://static.nid.naver.com/oauth/small_g_in.PNG" />
      </a> -->
   </c:if>
   <c:if test="${userId ne null}">
      <h1>로그인 성공입니다</h1>
      <input type="button" value="로그아웃" onclick="location.href='/logout'">
   </c:if>

</body>
</html>