package td.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import td.login.LoginAPI;
import td.model.dao.ClientRepository;
import td.model.dao.HiddenBoardRepository;
import td.model.dao.OpenBoardRepository;
import td.model.dao.ReplyRepository;
import td.model.domain.ClientDTO;
import td.model.domain.HiddenBoardDTO;
import td.model.domain.OpenBoardDTO;
import td.model.domain.ReplyDTO;

@Service
public class TDService {
	@Autowired
	private ClientRepository cRepo;

	@Autowired
	private HiddenBoardRepository hRepo;

	@Autowired
	private OpenBoardRepository oRepo;

	@Autowired
	private ReplyRepository rRepo;

	@Autowired
	private LoginAPI login;

	// 좋아요나 신고하기를 눌렀던 사람인지 파악
	public boolean judge(ArrayList<String> userList, String userId) {
		for (String user : userList) {
			if (user.equals(userId)) {
				return false;
			}
		}
		return true;
	}

	// 고객 정보
	public Optional<ClientDTO> findByIdClientDTO(String id) {
		return cRepo.findById(id);
	}

	// 고객 회원가입
	public void saveClientDTO(String serviceName, HttpSession session) {
		String id = (String) session.getAttribute("id");
		String nickName = session.getAttribute("nickname").toString();
		String serviceName1 = serviceName.toString();
		session.setAttribute("serviceName", serviceName1);
		String email = session.getAttribute("email").toString();
		boolean gg = true;

		cRepo.save(new ClientDTO(id, nickName, serviceName1, email, gg, 0));
	}

	public Optional<HiddenBoardDTO> findByIdClientDTO(HttpSession session) {
		ClientDTO client = cRepo.findById((String) session.getAttribute("id")).get();
		return hRepo.findById(client.getRandomMessageId());
	}

	// =================================================================
	// 비공개 게시판 정보

	// 모든 비공개 게시글 가져오기
	public Slice<HiddenBoardDTO> findAll(Pageable pageable) {
		return hRepo.findAll(pageable);
	}

	// hashtag검색
	public Slice<HiddenBoardDTO> hashtagSearch(Pageable pageable, String hashtag) {
		return hRepo.findByHashtagContaining(pageable, hashtag);
	}

	// category검색
	public Slice<HiddenBoardDTO> categorySearch(Pageable pageable, String category) {
		return hRepo.findByCategoryContaining(pageable, category);
	}

	// tagCloud
	public HashMap<String, Integer> tagCloud() {
		String[] tagList = null;
		HashMap<String, Integer> tagMap = new HashMap<>();

		for (HiddenBoardDTO entity : hRepo.findAll()) {
			tagList = entity.getHashtag().split(" ");

			for (String tag : tagList) {
				if (tagMap.containsKey(tag)) {
					tagMap.compute(tag, (k, v) -> v + 1);
				} else {
					tagMap.put(tag, 1);
				}
			}
		}

		return tagMap;
	}

	// 오늘의 메세지
	public void sendMessage() {
		Random r = new Random();

		int hiddenboardCount = (int) hRepo.count();
		int randomMessageId;

		for (ClientDTO client : cRepo.findAll()) {
			randomMessageId = r.nextInt(hiddenboardCount) + 1;
			client.setRandomMessageId(randomMessageId);
			System.out.println(client);
			cRepo.save(client);
		}
	}

	// 공개 날짜에 맞추어 게시글 공개 메소드
	public void moveToOpen() {
		String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

		for (HiddenBoardDTO v : hRepo.findByOpenDate(today)) {
			oRepo.save(new OpenBoardDTO(v.getId(), v.getTitle(), v.getContents(), v.getHashtag(), v.getOpenDate(),
					v.getHeart(), v.getClaim(), v.getNickname(), v.getCategory(), null, null));
			// hRepo.deleteById(v.getId());
		}
	}

	// 전체 게시글 수 가져오기
	public long getCount() {
		return hRepo.count();
	}

	// 전체 카테고리 게시글 수 가져오기
	public long getCategoryCount(String category) {
		return hRepo.countByCategoryContaining(category);
	}

	// 전체 해쉬태그 게시글 수 가져오기
	public long getHashtagCount(String hashtag) {
		return hRepo.countByHashtagContaining(hashtag);
	}

	// 테스트 데이터 삽입
	public int randomRange(int n1, int n2) {
		return (int) (Math.random() * (n2 - n1 + 1)) + n1;
	}

	public void makeTest() {
		String[] category = { "시", "감성글" };
		String[] hashtag = { "#SCA", "#PL", "#MLB", "#NBA" };

		for (int i = 1; i <= 99; i++) {
			String a = String.valueOf(i);
			HiddenBoardDTO v = new HiddenBoardDTO();

			v.setTitle(a);
			v.setCategory(category[randomRange(0, 1)]);
			v.setClaim(0);
			v.setContents(a);
			v.setHashtag(hashtag[randomRange(0, 3)]);
			v.setHeart(0);
			v.setId(a);
			v.setNickname(a);
			v.setOpenDate(i < 10 ? "2020010" + i : "2020010" + i);
			v.setPostingDate(i < 10 ? "2019120" + i : "201912" + i);

			hRepo.save(v);
		}
	}

	// 게시글 신고 추가
	public String plusHiddenBoardClaim(String nickname, String id) {
		HiddenBoardDTO boardEntity = hRepo.findById(id).get();
		ArrayList<String> plusClaimUserList = null;
		String message = null;

		if (boardEntity.getPlusClaimUserId() != null && boardEntity.getPlusClaimUserId().size() != 0) {
			plusClaimUserList = boardEntity.getPlusClaimUserId();
		} else {
			plusClaimUserList = new ArrayList<>();
		}

		if (judge(plusClaimUserList, nickname)) {
			plusClaimUserList.add(nickname);
			boardEntity.setClaim(boardEntity.getClaim() + 1);
			boardEntity.setPlusClaimUserId(plusClaimUserList);
			hRepo.save(boardEntity);
			message = "신고되었습니다";
		} else {
			message = "이미 신고하였습니다";
		}
		return message;
	}

	// 게시글 좋아요 추가
	public Integer plusHiddenBoardHeart(String nickname, String id) {
		HiddenBoardDTO boardEntity = hRepo.findById(id).get();
		ArrayList<String> plusHeartUserList = null;

		if (boardEntity.getPlusHeartUserId() != null && boardEntity.getPlusHeartUserId().size() != 0) {
			plusHeartUserList = boardEntity.getPlusHeartUserId();
		} else {
			plusHeartUserList = new ArrayList<>();
		}

		if (judge(plusHeartUserList, nickname)) {
			plusHeartUserList.add(nickname);
			boardEntity.setHeart(boardEntity.getHeart() + 1);
			boardEntity.setPlusHeartUserId(plusHeartUserList);
			hRepo.save(boardEntity);
		} else {
			plusHeartUserList.remove(nickname);
			boardEntity.setHeart(boardEntity.getHeart() - 1);
			boardEntity.setPlusHeartUserId(plusHeartUserList);
			hRepo.save(boardEntity);
		}
		return boardEntity.getHeart();
	}

	// 비공개 게시판 게시글 작성
	public boolean saveHiddenBoardDTO(HiddenBoardDTO board) throws ParseException {
		boolean result = false;
		String openDate = board.getOpenDate().replace("-", "");

		if (new SimpleDateFormat("yyyyMMdd").parse(openDate).after(new Date())) {
			board.setOpenDate(openDate);
			board.setContents(board.getContents().replace("\n", "<br>"));
			hRepo.save(board);
			result = true;
		}

		return result;
	}

	// =================================================================

	// 공개 게시판 정보
	public Optional<OpenBoardDTO> findByIdOpenBoardDTO(String id) {
		return oRepo.findById(id);
	}

	// 전체 게시물 조회
	public Iterable<OpenBoardDTO> getAllOpenBoardDTO() {
		return oRepo.findAll();
	}

	// =================================================================

	// 리플 정보
	public Optional<ReplyDTO> findByIdOpenReplyDTO(String id) {
		return rRepo.findById(id);
	}

	// 유저 ID와 게시판ID로 리플 가져오기
	public ReplyDTO getReply(String repUserId, String repBoardId) {
		return rRepo.findByUserIdAndRepBoardId(repUserId, repBoardId);
	}

	// 게시판 ID로만 리플 가져오기
	public Slice<ReplyDTO> getReplyInOpen(Pageable pageable, String repBoardId) {
		return rRepo.findByRepBoardId(pageable, repBoardId);
	}

	// 댓글 저장하기
	public boolean saveReply(ReplyDTO reply) {
		boolean result = false;
		String content = reply.getRepContents();
		if (content != null && content.trim().length() >= 5) {
			result = true;
			rRepo.save(reply);
		}
		return result;
	}

	// 리플 좋아요 추가
	public Integer plusRepHeart(String repUserId, String repBoardId, String nickName) {
		ReplyDTO entity = rRepo.findByUserIdAndRepBoardId(repUserId, repBoardId);
		repUserId = nickName == null ? repUserId : nickName;

		ArrayList<String> plusHeartUserList = null;
		if (entity.getPlusHeartUserId() != null && entity.getPlusHeartUserId().size() != 0) {
			plusHeartUserList = entity.getPlusHeartUserId();
		} else {
			plusHeartUserList = new ArrayList<>();
		}

		if (judge(plusHeartUserList, repUserId)) {
			plusHeartUserList.add(repUserId);
			entity.setRepHeart(entity.getRepHeart() + 1);
			entity.setPlusHeartUserId(plusHeartUserList);
			rRepo.save(entity);
		} else {
			plusHeartUserList.remove(repUserId);
			entity.setRepHeart(entity.getRepHeart() - 1);
			entity.setPlusHeartUserId(plusHeartUserList);
			rRepo.save(entity);
		}
		return entity.getRepHeart();
	}

	// 리플 신고 추가
	public String plusRepClaim(String repUserId, String repBoardId) {
		ReplyDTO entity = rRepo.findByUserIdAndRepBoardId(repUserId, repBoardId);
		ArrayList<String> plusClaimUserList = null;
		String message = null;

		if (entity.getPlusClaimUserId() != null && entity.getPlusClaimUserId().size() != 0) {
			plusClaimUserList = entity.getPlusClaimUserId();
		} else {
			plusClaimUserList = new ArrayList<>();
		}

		if (judge(plusClaimUserList, repUserId)) {
			plusClaimUserList.add(repUserId);
			entity.setRepClaim(entity.getRepClaim() + 1);
			entity.setPlusClaimUserId(plusClaimUserList);
			rRepo.save(entity);
			message = "신고되었습니다";
		} else {
			message = "이미 신고하였습니다";
		}
		return message;
	}

	// 리플에 좋아요 누른 사람들 가져오기
	public Iterable<ReplyDTO> getReplyByPlusHeartUserId(String plusHeartUserId) {
		return rRepo.findByPlusHeartUserId(plusHeartUserId);
	}
	// =================================================================

	// 로그인 API
	public String kakaoLogin(String code, HttpSession session) {
		String url = "/thymeleaf/error.html";
		String access_Token = login.getKakaoAccessToken(code);
		HashMap<String, Object> userInfo = login.getKakaoUserInfo(access_Token);
		session.setAttribute("id", userInfo.get("id"));
		session.setAttribute("nickname", userInfo.get("nickname"));
		session.setAttribute("email", userInfo.get("email"));
		session.setAttribute("platform", "kakao");

		if (cRepo.findById((String) userInfo.get("id")).isPresent()) {
			session.setAttribute("serviceName", cRepo.findById((String) userInfo.get("id")).get().getServiceName());
			url = "/thymeleaf/closeBoard.html";
		} else {
			url = "/thymeleaf/session.html";
		}

		// 클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
		if (userInfo.get("email") != null) {
			session.setAttribute("userId", userInfo.get("email"));
			session.setAttribute("access_Token", access_Token);
		}
		return url;
	}

	public String naverLogin(String code, String state, HttpSession session) throws IOException {
		String url = "/thymeleaf/error.html";
		if (login.token(session, state) == true) {
			String access_Token = login.getNaverAccessToken(code);
			HashMap<String, Object> userInfo = login.getNaverUserInfo(access_Token);
			session.setAttribute("id", userInfo.get("id"));
			session.setAttribute("nickname", userInfo.get("nickName"));
			session.setAttribute("email", userInfo.get("email"));
			session.setAttribute("platform", "naver");

			if (cRepo.findById((String) userInfo.get("id")).isPresent()) {
				session.setAttribute("serviceName", cRepo.findById((String) userInfo.get("id")).get().getServiceName());
				url = "/thymeleaf/closeBoard.html";
			} else {
				url = "/thymeleaf/session.html";
			}

			// 클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
			if (userInfo.get("email") != null) {
				session.setAttribute("userId", userInfo.get("email"));
				session.setAttribute("access_Token", access_Token);
			}
		}
		return url;
	}

	public String logout(HttpSession session) {
		String url = "/thymeleaf/error.html";

		if (session.getAttribute("platform") == "kakao") {
			login.kakaoLogout((String) session.getAttribute("access_Token"));
			session.removeAttribute("access_Token");
			session.removeAttribute("userId");
			url = "/thymeleaf/intro.html";
		} else if (session.getAttribute("platform") == "naver") {
			session.removeAttribute("access_Token");
			session.removeAttribute("userId");
			session.removeAttribute("state");
			url = "/thymeleaf/intro.html";
		}

		session.invalidate();
		return url;
	}

}