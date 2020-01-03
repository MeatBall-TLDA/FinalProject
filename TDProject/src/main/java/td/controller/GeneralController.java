package td.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import td.service.TDService;

@Controller
public class GeneralController {

   @Autowired
   private TDService service;

   @RequestMapping("/intro")
   public String goToIntro() {
      return "/thymeleaf/intro";
   }

   @RequestMapping("/hidden")
   public String goToHidden() {
      return "/thymeleaf/HiddenBoard";
   }

   @RequestMapping("/open")
   public String goToOpen() {
      return "/thymeleaf/OpenBoard";
   }

   @RequestMapping("/menu")
   public String goToMenu() {
      return "/thymeleaf/menu";
   }

   @RequestMapping("/mypage")
   public String goToMyPage(HttpSession session) {
      return "/thymeleaf/mypage";
   }

   @RequestMapping("/search")
   public String goToSearch() {
      return "/thymeleaf/search";
   }

   @RequestMapping("/todaymessage")
   public String goToTodayMessage() {
      return "/thymeleaf/todayMessage";
   }
   
   @RequestMapping("/writing")
   public String goToWriting() {
      return "/thymeleaf/writing";
   }

   // 세션 확인하는 로직 index.html
   @RequestMapping("/index")
   public String sessionCheck(HttpSession session) {
      System.out.println(session.getAttribute("id"));
      if (session.getAttribute("id") == null) {

         return "/thymeleaf/intro";
      } else {
         return "/thymeleaf/CloseBoard";
      }
   }


   // 세션 삭제 로직
   @RequestMapping("/session2")
   String index2(HttpSession session) {
      session.invalidate();
      return "/thymeleaf/session.html";
   }

   // 공개 날짜에 맞추어 게시글 데이터 이동 메소드
   @Scheduled(cron = "0 0 0 * * *")
   public void moveToOpen() {
      service.moveToOpen();
   }


//   @Scheduled(initialDelay = 10000, fixedDelay = 10000)
//   public void sendMessage() {
//      System.out.println("gggg");
//      service.sendMessage();
//   }


   
   @RequestMapping("/close")
   public String goToCloseBoard() {
      return "/thymeleaf/closeBoard";
   }
   
   @RequestMapping("/public")
   public String goToPublicBoard() {
      return "/thymeleaf/publicBoard";
   }
   
   @PostMapping("/serviceName")
   public String saveClientDTO(@RequestParam("serviceName")String serviceName, HttpSession session) {
      service.saveClientDTO(serviceName, session);
      return "/thymeleaf/closeBoard";
   }

//   @Scheduled(initialDelay = 10000, fixedDelay = 10000)
//   public void sendMessage() {
//      System.out.println("gggg");
//      service.sendMessage();
//   }

   // 미공개 게시판 게시글 작성
//   @PostMapping("/saveHidden")
//   public String saveHiddenBoardDTO(HiddenBoardDTO board) {
//      String url = "";
//      if(service.saveHiddenBoardDTO(board)) {
//         url = "HiddenBoard";
//      }else {
//         url = "Error";
//      }
//      return url;
//   }
   // =====================================

   // 로그인 API
   @RequestMapping("/login")
   public String view(ModelMap model) {
      return "/login";
   }

   @RequestMapping(value = "/kakaoLogin")
   public String login(@RequestParam("code") String code, HttpSession session) {
      return service.kakaoLogin(code, session);
   }

   @RequestMapping(value = "/naverLogin")
   public String naverLogin(@RequestParam("code") String code, @RequestParam String state, HttpSession session) {
      try {
         return service.naverLogin(code, state, session);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         // 에러 페이지로 보내야함
         return "/intro";
      }
   }

   @RequestMapping(value = "/logout")
   public String logout(HttpSession session) {
      return service.logout(session);
   }

}