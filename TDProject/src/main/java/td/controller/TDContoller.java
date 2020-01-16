package td.controller;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import td.model.domain.HiddenBoardDTO;
import td.model.domain.OpenBoardDTO;
import td.model.domain.ReplyDTO;
import td.service.TDService;

@CrossOrigin(origins = "http://localhost:8000")
@RestController
public class TDContoller {

	@Autowired
	private TDService service;

	// 미공개 게시판 정보
	// 페이지 넘버에 따라 게시글 조회
	@GetMapping("/getHidden")
	public Slice<HiddenBoardDTO> findAll(@PageableDefault(size = 10) Pageable pageable) {
		return service.hiddenFindAll(pageable);
	}

	// 전체 게시글 수 조회
	@GetMapping("/getCount")
	public long getCount() {
		return service.getCount();
	}

	// 전체 카테고리 게시글 수 가져오기
	@GetMapping("/getCategoryCount")
	public long getCategoryCount(String category) {
		return service.getCategoryCount(category);
	}

	// 전체 해쉬태그 게시글 수 가져오기
	@GetMapping("/getHashtagCount")
	public long getHashtagCount(String hashtag) {
		return service.getHashtagCount(hashtag);
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

	// =================================================================

	// 공개 게시글 전체 가져오기
	@GetMapping("/getOpen")
	public Slice<OpenBoardDTO> findOpenAll(@PageableDefault(size = 10) Pageable pageable) {
		return service.openFindAll(pageable);
	}

	// 공개 게시판 정보
	@GetMapping("/openTest")
	public Optional<OpenBoardDTO> findByIdOpenBoardDTO(String id) {
		return service.findByIdOpenBoardDTO(id);
	}

	// 공개 게시판 전체 게시글 수 가져오기
	@GetMapping("/getOpenCount")
	public long getOpenCount() {
		return service.getOpenCount();
	}

	// 전체 카테고리 게시글 수 가져오기
	@GetMapping("/getOpenCategoryCount")
	public long getOpenCategoryCount(String category) {
		return service.getOpenCategoryCount(category);
	}

	// 전체 해쉬태그 게시글 수 가져오기
	@GetMapping("/getOpenHashtagCount")
	public long getOpenHashtagCount(String hashtag) {
		return service.getOpenHashtagCount(hashtag);
	}

	// 공개게시글 좋아요 누를 때 +1 이미 눌렀으면 -1
	@PostMapping("/plusOpenBoardHeart")
	public Integer plusOpenBoardHeart(String nickname, String id) {
		return service.plusOpenBoardHeart(nickname, id);
	}

	// 공개게시글 신고하기 누를때 +1 이미 눌렀으면 이미신고했다는 메시지 반환
	@PostMapping("/plusOpenBoardClaim")
	public String plusOpenBoardClaim(String nickname, String id) {
		return service.plusOpenBoardClaim(nickname, id);
	}

	// =================================================================

	// 리플 정보
	@GetMapping("/replyTest")
	public Optional<ReplyDTO> findByIdOpenReplyDTO(String id) {
		return service.findByIdOpenReplyDTO(id);
	}

	@GetMapping("/getReply")
	public ReplyDTO getReply(String repUserId, String repBoardId) {
		return service.getReply(repUserId, repBoardId);
	}

	@GetMapping("/getCountReply")
	public long getCountReply(String repBoardId) {
		return service.getCountReply(repBoardId);
	}

	@GetMapping("/getReplyInOpen")
	public Slice<ReplyDTO> getReplyInOpen(
			@PageableDefault(size = 5, sort = "repHeart", direction = Sort.Direction.DESC, page = 0) Pageable pageable,
			String repBoardId) {
		return service.getReplyInOpen(pageable, repBoardId);
	}

	@PostMapping("/saveReply")
	public int saveReply(ReplyDTO reply) {
		int message = 0;
		if (service.saveReply(reply)) {
			message = 1;
		}
		return message;
	}

//	댓글 좋아요 누를 때 +1 이미 눌렀으면 -1
	@PostMapping("/plusHeart")
	public Integer plusRepHeart(String repUserId, String repBoardId, @RequestParam(required = false) String nickName) {
		return service.plusRepHeart(repUserId, repBoardId, nickName);
	}

	@PostMapping("/plusRepClaim")
	public String plusRepClaim(String repUserId, String repBoardId) {
		return service.plusRepClaim(repUserId, repBoardId);
	}

	// 유저 ID로 유저가 좋아요한 모든 댓글정보 가져오기
//	@GetMapping("/getPlusHeart")
//	public Iterable<ReplyDTO> getReplyByPlusHeartUserId(String plusHeartUserId){
//		return service.getReplyByPlusHeartUserId(plusHeartUserId);
//	}

	// 동범 search =================================================================
	@GetMapping("/hashtagSearch")
	public Slice<HiddenBoardDTO> hashtagSearch(@PageableDefault(size = 10) Pageable pageable, String hashtag) {
		return service.hashtagSearch(pageable, hashtag);
	}

	@GetMapping("/categorySearch")
	public Slice<HiddenBoardDTO> categorySearch(@PageableDefault(size = 10) Pageable pageable, String category) {
		return service.categorySearch(pageable, category);
	}

	// 태그 클라우드 값 가져오기
	@GetMapping("/tagCloud")
	public HashMap<String, Integer> tagCloud() {
		return service.tagCloud();
	}

	@GetMapping("/openHashtagSearch")
	public Slice<OpenBoardDTO> openHashtagSearch(@PageableDefault(size = 10) Pageable pageable, String hashtag) {
		return service.openHashtagSearch(pageable, hashtag);
	}

	@GetMapping("/openCategorySearch")
	public Slice<OpenBoardDTO> openCategorySearch(@PageableDefault(size = 10) Pageable pageable, String category) {
		return service.openCategorySearch(pageable, category);
	}
}
