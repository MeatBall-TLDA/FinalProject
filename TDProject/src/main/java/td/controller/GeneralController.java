package td.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import td.model.domain.HiddenBoardDTO;
import td.service.TDService;

@Controller
public class GeneralController {

	@Autowired
	private TDService service;
	
	@RequestMapping("/hidden")
	public String goToHidden() {
		return "/thymeleaf/HiddenBoard";
	}
	
	@RequestMapping("/travel")
	public String goTotravel() {
		return "/thymeleaf/travel.html";
	}
	
	
	// 미공개 게시판 게시글 작성
	@PostMapping("/saveHidden")
	public String saveHiddenBoardDTO(HiddenBoardDTO board) {
		String url = "";
		if(service.saveHiddenBoardDTO(board)) {
			url = "HiddenBoard";
		}else {
			url = "Error";
		}
		return url;
	}
	
	// 공개 날짜에 맞추어 게시글 데이터 이동 메소드
	@Scheduled(cron = "0 0 0 * * *")
	public void moveToOpen() {
		service.moveToOpen();
	}

//	@Scheduled(initialDelay = 30000, fixedDelay = 10000)
//	public void sendMessage() {
//		service.sendMessage();
//	}
	
	// =====================================
	// 로그인 API
	@RequestMapping("/")
	public String view(ModelMap model) {
		return "/login";
	}
	
	@RequestMapping(value = "/kakaoLogin")
	public String login(@RequestParam("code") String code, HttpSession session) {
		return service.login(code, session);
	}
	
	@RequestMapping(value = "/naverLogin")
	public String naverLogin(@RequestParam("code") String code, @RequestParam String state, HttpSession session)
			throws IOException {
		return service.naverLogin(code, state, session);
	}
	
	@RequestMapping(value = "/logout")
	public String logout(HttpSession session) {
		return service.logout(session);
	}
	
}
