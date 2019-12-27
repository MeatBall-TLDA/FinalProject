package td.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import td.model.domain.ClientDTO;
import td.model.domain.HiddenBoardDTO;
import td.model.domain.OpenBoardDTO;
import td.model.domain.ReplyDTO;
import td.service.TDService;

@CrossOrigin(origins = "http://localhost:8000")
@RestController
public class TDContoller {

	@Autowired
	private TDService service;
	
	// 고객 정보
	@GetMapping("/clientTest")
	public Optional<ClientDTO> findByIdClientDTO(String id) {
		return service.findByIdClientDTO(id);
	}
	
	@PostMapping("/serviceName")
	public void saveClientDTO(@RequestParam("serviceName")String serviceName, HttpSession session) {
		service.saveClientDTO(serviceName, session);
	}
	// =================================================================
	
	// 미공개 게시판 정보
	// 페이지 넘버에 따라 게시글 조회
	@GetMapping("/getHidden")
	public Slice<HiddenBoardDTO> findAll(@PageableDefault(size = 10) Pageable pageable) {
		return service.findAll(pageable);
	}
	
	// 전체 게시글 수 조회
	@GetMapping("/getCount")
	public long getCount() {
		return service.getCount();
	}
	
	// 게시글 좋아요 누를 때 +1 이미 눌렀으면 -1
	@PostMapping("/plusBoardHeart")
	public Integer plusHiddenBoardHeart(String nickname, String id) {
		return service.plusHiddenBoardHeart(nickname, id);
	}
	
	// 게시글 신고하기 누를때 +1 이미 눌렀으면 이미신고했다는 메시지 반환
	@PostMapping("/plusBoardClaim")
	public String plusHiddenBoardClaim(String nickname, String id) {
		return service.plusHiddenBoardClaim(nickname, id);
	}
	
	@GetMapping("/makeTest")
	public void makeTest() {
		service.makeTest();
	}
	
	// =================================================================
	
	// 공개 게시판 정보
	@GetMapping("/openTest")
	public Optional<OpenBoardDTO> findByIdOpenBoardDTO(String id) {
		return service.findByIdOpenBoardDTO(id);
	}
	
	// 전체 게시물 조회
	@GetMapping("/testst")
	public Iterable<OpenBoardDTO> getAllOpenBoardDTO(){
		return service.getAllOpenBoardDTO();
	}
	
	// =================================================================

	// 리플 정보
	@GetMapping("/replyTest")
	public Optional<ReplyDTO> findByIdOpenReplyDTO(String id) {
		return service.findByIdOpenReplyDTO(id);
	}



	@GetMapping("/getReply")
	public Iterable<ReplyDTO> getReply(String repBoardId) {
		return service.getReply(repBoardId);
	}
	
	@PostMapping("/saveReply")
	public int saveReply(ReplyDTO reply) {
		int message = 0;
		if(service.saveReply(reply)) {
			message = 1;
		}
		return message;
	}


//	댓글 좋아요 누를 때 +1 이미 눌렀으면 -1
	@PostMapping("/plusHeart")
	public Integer plusRepHeart(String repUserId, String repBoardId, @RequestParam(required=false) String nickName) {
		return service.plusRepHeart(repUserId, repBoardId, nickName);
	}
	
	// 유저 ID로 유저가 좋아요한 모든 댓글정보 가져오기
//	@GetMapping("/getPlusHeart")
//	public Iterable<ReplyDTO> getReplyByPlusHeartUserId(String plusHeartUserId){
//		return service.getReplyByPlusHeartUserId(plusHeartUserId);
//	}
	
	// 동범 search =================================================================	
	@GetMapping("/hashtagSearch")
	public Slice<HiddenBoardDTO> hashtagSearch(@PageableDefault(size = 10) Pageable pageable) {
		return service.hashtagSearch(pageable, "c");
	}
	
	@GetMapping("/categorySearch")
	public Slice<HiddenBoardDTO> categorySearch(@PageableDefault(size = 10) Pageable pageable) {
		return service.categorySearch(pageable, "A");
	}
}
