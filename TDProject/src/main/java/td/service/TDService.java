package td.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
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
import org.springframework.ui.Model;

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

	// 고객 회원가입
	public void saveClientDTO(String serviceName, HttpSession session) {
		String id = (String) session.getAttribute("id");
		String nickName = session.getAttribute("nickname").toString();
		String email = session.getAttribute("email").toString();
		session.setAttribute("serviceName", serviceName);

		cRepo.save(new ClientDTO(id, nickName, serviceName, email, true, null));
	}

	// 고객 닉네임 수정
	public void updateServiceName(String serviceName, String updateName) {

		ClientDTO clinet = cRepo.findByServiceName(serviceName);
		clinet.setServiceName(updateName);

		cRepo.save(clinet);
	}

	// 오늘의 메시지 가져오기
	public Optional<HiddenBoardDTO> getTodayMessage(HttpSession session) {
		Optional<HiddenBoardDTO> entity = null;
		ClientDTO client = null;
		
		try {
			client = cRepo.findById((String) session.getAttribute("id")).get();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		if (client.getRandomMessageId() != null) {
			entity = hRepo.findById(client.getRandomMessageId());
		}

		return entity;
	}

	// =================================================================
	// 비공개 게시판 정보

	// 모든 비공개 게시글 가져오기
	public Slice<HiddenBoardDTO> hiddenFindAll(Pageable pageable) {
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
		ArrayList<String> hiddenBoardId = new ArrayList<>();
		Random r = new Random();
		int hiddenboardCount = (int) hRepo.count();

		for (HiddenBoardDTO hiddenBoard : hRepo.findAll()) {
			hiddenBoardId.add(hiddenBoard.getId());
		}

		for (ClientDTO client : cRepo.findAll()) {
			client.setRandomMessageId(hiddenBoardId.get(r.nextInt(hiddenboardCount)));
			System.out.println(client);
			cRepo.save(client);
		}
	}

	// 공개 날짜에 맞추어 게시글 공개 메소드
	public void moveToOpen() {
		for (HiddenBoardDTO v : hRepo.findByOpenDate(new SimpleDateFormat("yyyyMMdd").format(new Date()))) {
			oRepo.save(new OpenBoardDTO(v.getId(), v.getTitle(), v.getContents(), v.getHashtag(), v.getOpenDate(),
					v.getHeart(), v.getClaim(), v.getNickname(), v.getCategory(), v.getPlusHeartUserId(), v.getPlusClaimUserId()));
			 hRepo.deleteById(v.getId());
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

	// 비공개 게시판 게시글 삭제
	public boolean deleteHiddenBoardDTO(String boardId) throws ParseException {
		boolean result = false;
		if (hRepo.findById(boardId).isPresent()) {
			hRepo.deleteById(boardId);
			result = true;
		}
		return result;
	}

	// =================================================================

	// 공개 게시판 정보
	public Optional<OpenBoardDTO> findByIdOpenBoardDTO(String id) {
		return oRepo.findById(id);
	}

	// 공개 게시판 전체 게시글 수 가져오기
	public long getOpenCount() {
		return oRepo.count();
	}

	// 전체 카테고리 게시글 수 가져오기
	public long getOpenCategoryCount(String category) {
		return oRepo.countByCategoryContaining(category);
	}

	// 전체 해쉬태그 게시글 수 가져오기
	public long getOpenHashtagCount(String hashtag) {
		return oRepo.countByHashtagContaining(hashtag);
	}

	// 모든 공개 게시글 가져오기
	public Slice<OpenBoardDTO> openFindAll(Pageable pageable) {
		return oRepo.findAll(pageable);
	}

	// hashtag검색
	public Slice<OpenBoardDTO> openHashtagSearch(Pageable pageable, String hashtag) {
		return oRepo.findByHashtagContaining(pageable, hashtag);
	}

	// category검색
	public Slice<OpenBoardDTO> openCategorySearch(Pageable pageable, String category) {
		return oRepo.findByCategoryContaining(pageable, category);
	}

	// 공개 게시글 신고 추가
	public String plusOpenBoardClaim(String nickname, String id) {
		OpenBoardDTO boardEntity = oRepo.findById(id).get();
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
			oRepo.save(boardEntity);
			message = "신고되었습니다";
		} else {
			message = "이미 신고하였습니다";
		}
		return message;
	}

	// 공개 게시글 좋아요 추가
	public Integer plusOpenBoardHeart(String nickname, String id) {
		OpenBoardDTO boardEntity = oRepo.findById(id).get();
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
			oRepo.save(boardEntity);
		} else {
			plusHeartUserList.remove(nickname);
			boardEntity.setHeart(boardEntity.getHeart() - 1);
			boardEntity.setPlusHeartUserId(plusHeartUserList);
			oRepo.save(boardEntity);
		}
		return boardEntity.getHeart();
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

	public long getCountReply(String repBoardId) {
		return rRepo.countByRepBoardId(repBoardId);
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
	
	// 로그인 url 만들어서 전달하기
	public String login(Model model, HttpSession session) throws UnsupportedEncodingException {
		
		String clientId = "YgSTzaDFAOIL6DsaS9Cy";//애플리케이션 클라이언트 아이디값";
		String redirectURI = URLEncoder.encode("http://localhost:8000/naverLogin", "UTF-8");
		SecureRandom random = new SecureRandom();
		String state = new BigInteger(130, random).toString();
		String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
		apiURL += "&client_id=" + clientId;
		apiURL += "&redirect_uri=" + redirectURI;
		apiURL += "&state=" + state;
		session.setAttribute("state", state);
		
		model.addAttribute("naverURL", apiURL);
		model.addAttribute("kakaoURL", "https://kauth.kakao.com/oauth/authorize?client_id=48bdb629c0cc1beac9d7d5f5cdead8b2&redirect_uri=http://localhost:8000/kakaoLogin&response_type=code");
		
		return "/thymeleaf/login";
	}

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