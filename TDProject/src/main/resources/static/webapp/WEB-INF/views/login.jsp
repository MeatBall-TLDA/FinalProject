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
<meta name="viewport" content="width=device-width, initial-scale=1">
<!--===============================================================================================-->
<link rel="icon" type="image/png" href="images/icons/favicon.ico" />
<!--===============================================================================================-->
<link rel="stylesheet" type="text/css"
	href="vendor/bootstrap/css/bootstrap.min.css">
<!--===============================================================================================-->
<link rel="stylesheet" type="text/css"
	href="fonts/font-awesome-4.7.0/css/font-awesome.min.css">
<link
	href="https://fonts.googleapis.com/css?family=Nanum+Myeongjo|Noto+Serif+KR&display=swap"
	rel="stylesheet">
<!--===============================================================================================-->
<link rel="stylesheet" type="text/css"
	href="fonts/Linearicons-Free-v1.0.0/icon-font.min.css">
<!--===============================================================================================-->
<link rel="stylesheet" type="text/css" href="vendor/animate/animate.css">
<!--===============================================================================================-->
<link rel="stylesheet" type="text/css"
	href="vendor/css-hamburgers/hamburgers.min.css">
<!--===============================================================================================-->
<link rel="stylesheet" type="text/css"
	href="vendor/animsition/css/animsition.min.css">
<!--===============================================================================================-->
<link rel="stylesheet" type="text/css"
	href="vendor/select2/select2.min.css">
<!--===============================================================================================-->
<link rel="stylesheet" type="text/css"
	href="vendor/daterangepicker/daterangepicker.css">
<!--===============================================================================================-->
<link rel="stylesheet" type="text/css" href="css/login_util.css">
<link rel="stylesheet" type="text/css" href="css/login_main.css">
<link rel="stylesheet" type="text/css" href="css/style-login.css">
<!--===============================================================================================-->
<title>LoginPage_After</title>
</head>
<body>
	<c:if test="${userId eq null}">
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


		<div class="limiter">
			<div class="container-login100"
				style="background-image: url(images/image_14.jpg);">
				<div class="wrap-login100 p-t-90 p-b-30">
					<form class="login100-form validate-form">
						<span class="login100-form-title p-b-40"> 틀다 </span>

						<div class="loginContainer">
							<!-- 카카오 로그인 배너 -->
							<a
								href="https://kauth.kakao.com/oauth/authorize?client_id=48bdb629c0cc1beac9d7d5f5cdead8b2&redirect_uri=http://localhost:8000/kakaoLogin&response_type=code">
								<img class="loginbtn"
								src="/img/kakao_account_login_btn_medium_wide_ov.png">
							</a> <br>
							<!-- 네이버 로그인 배너 -->
							<a href="<%=apiURL%>"><img class="loginbtn"
								src="http://static.nid.naver.com/oauth/small_g_in.PNG" /></a>
						</div>

						<div class="text-center p-t-55 p-b-30">
							<span class="txt1"> Not a wrong just different </span>
						</div>
					</form>
				</div>
			</div>
		</div>
		<script>
			jQuery(function($) {
				$("body").css("display", "none");
				$("body").fadeIn(2000);
				$("a.transition").click(function(event) {
					event.preventDefault();
					linkLocation = this.href;
					$("body").fadeOut(1000, redirectPage);
				});
				function redirectPage() {
					window.location = linkLocation;
				}
			});
		</script>
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
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
</body>
</html>