package td.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import td.model.domain.HiddenBoardDTO;
import td.service.TDService;

@Controller
public class GeneralController {

	@Autowired
	private TDService service;

	@RequestMapping("/intro")
	public String goToIntro() {
		return "/thymeleaf/intro";
	}

	@RequestMapping("/menu")
	public String goToMenu() {
		return "/thymeleaf/menu";
	}

	@RequestMapping("/mypage")
	public String goToMyPage() {
		return "/thymeleaf/mypage";
	}

	@RequestMapping("/search")
	public String goToSearch() {
		return "/thymeleaf/search";
	}

	@RequestMapping("/writing")
	public String goToWriting() {
		return "/thymeleaf/writing";
	}

	@RequestMapping("/close")
	public String goToCloseBoard() {
		return "/thymeleaf/closeBoard";
	}

	@RequestMapping("/public")
	public String goToPublicBoard() {
		return "/thymeleaf/publicBoard";
	}

	// 세션 확인하는 로직
	@RequestMapping("/index")
	public String sessionCheck(HttpSession session) {
		if (session.getAttribute("id") == null) {
			return "/thymeleaf/intro";
		} else {
			return "/thymeleaf/close";
		}
	}
	
	// 오늘의 메시지 정보 가져오기
	@RequestMapping("/todaymessage")
	public String goToTodayMessage(Model model, HttpSession session) {
		Object test = "";
		try {
			test = service.getTodayMessage(session).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("message", test);
		return "/thymeleaf/todayMessage";
	}

	// 오늘의 메세지 유저에게 보내기
   @Scheduled(cron = "0 0 0 * * *")
	public void sendMessage() {
		System.out.println("오늘의 메세지 기능 작동(스케쥴러)");
		service.sendMessage();
	}

	// 공개 날짜에 맞추어 게시글 데이터 이동 메소드
	@Scheduled(cron = "0 0 0 * * *")
	public void moveToOpen() {
		service.moveToOpen();
	}

	// 닉네임 설정
	@PostMapping("/serviceName")
	public String saveClientDTO(String serviceName, HttpSession session) {
		service.saveClientDTO(serviceName, session);
		return "/thymeleaf/closeBoard";
	}

	// 닉네임 수정
	@PostMapping("/updateServiceName")
	   public String updateServiceName(HttpSession session, String updateName) {
	      service.updateServiceName((String) session.getAttribute("serviceName"), updateName);
	      session.setAttribute("serviceName", updateName);
	      return "/thymeleaf/mypage";
	   }

	// 미공개 게시판 게시글 작성
	@PostMapping("/saveHidden")
	public String saveHiddenBoardDTO(HiddenBoardDTO board) {
		String url = "";
		try {
			if (service.saveHiddenBoardDTO(board)) {
				url = "/thymeleaf/closeBoard";
			} else {
				url = "/thymeleaf/error";
			}
		} catch (ParseException e) {
			e.printStackTrace();
			url = "/thymeleaf/error";
		}
		return url;
	}
	
	// 게시글 삭제 로직
	@PostMapping("/deleteHidden")
	public String deleteHiddenBoardDTO(String boardId) {
		String url = "";
		try {
			if (service.deleteHiddenBoardDTO(boardId)) {
				url = "/thymeleaf/closeBoard";
			} else {
				url = "/thymeleaf/error";
			}
		} catch (ParseException e) {
			e.printStackTrace();
			url = "/thymeleaf/error";
		}
		return url;
	}
	
	// =====================================

	// 로그인 API
	@RequestMapping("/login")
	public String goToLogin(Model model, HttpSession session) {
		String url = "/thymeleaf/error";
		try {
			url = service.login(model, session);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}

	@RequestMapping("/kakaoLogin")
	public String kakaoLogin(String code, HttpSession session) {
		return service.kakaoLogin(code, session);
	}

	@RequestMapping("/naverLogin")
	public String naverLogin(String code, String state, HttpSession session) {
		String url = null;
		try {
			url = service.naverLogin(code, state, session);
		} catch (IOException e) {
			url = "/thymeleaf/error.html";
			e.printStackTrace();
		}
		return url;
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		return service.logout(session);
	}

}