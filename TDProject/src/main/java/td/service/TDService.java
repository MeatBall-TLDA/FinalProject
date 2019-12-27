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

	public void saveClientDTO(String serviceName, HttpSession session) {
		int id = Integer.parseInt((String) session.getAttribute("id"));
		String nickName = session.getAttribute("nickname").toString();
		String serviceName1 = serviceName.toString();
		session.setAttribute("serviceName", serviceName1);
		String email = session.getAttribute("email").toString();
		boolean gg = true;

		ClientDTO client = new ClientDTO(id, nickName, serviceName1, email, gg);
		cRepo.save(client);
		System.out.println(client);
	}
	// 세션 만드는 로직
	@RequestMapping({ "/session" })
	String index(HttpSession session) {
		session.setAttribute("id", "yyy2410");
		session.setAttribute("pw", "123456");
		return "/thymeleaf/session.html";
	}
	
	// =================================================================
	// 미공개 게시판 정보

	public Slice<HiddenBoardDTO> findAll(Pageable pageable) {
		return hRepo.findAll(pageable);
	}

	// hashtagSearch
	public Slice<HiddenBoardDTO> hashtagSearch(Pageable pageable, String hashtag) {
		return hRepo.findByHashtagContaining(pageable, hashtag);
	}

	// categorySearch
	public Slice<HiddenBoardDTO> categorySearch(Pageable pageable, String category) {
//      System.out.println(hRepo.findAllHashtag());
		return hRepo.findByCategoryContaining(pageable, category);
	}

	// 오늘의 메세지 구현 중
//	public void sendMessage() {
//		Random r = new Random();
//
//		// hiddenboard 카운팅
//		int hiddenboardCount = (int) getCount();
//		int a [] = new int[hiddenboardCount];
//		
//		// 회원 수 카운팅
//		int clientCount = 10;
//		
//		// 회원수 만큼 for을 돌린다. 
//		for(int i=0; i<clientCount; i++) {
//			a[i] = r.nextInt(hiddenboardCount)+1;
//			while(true) {
//				
//			}
//			if(10 == a[i])
//		}
//		
//		for(HiddenBoardDTO aa : hRepo.findByid(1)) {
//			System.out.println(aa);
//		}
//	}

	// 공개 날짜에 맞추어 게시글 공개 메소드
	public void moveToOpen() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		Date time = new Date();
		String today = format1.format(time);

		for (HiddenBoardDTO v : hRepo.findByOpenDate(today)) {
			oRepo.save(new OpenBoardDTO(v.getId(), v.getContents(), v.getHashtag(), v.getOpenDate(), v.getHeart(),
					v.getClaim(), v.getNickname(), v.getCategory(), null, null));
			// hRepo.deleteById(v.getId());
		}
	}

	// 오늘의 메세지 구현 중
	public void sendMessage() {
		Random r = new Random();

		int hiddenboardCount = (int) hRepo.count();
		int clientCount = (int) cRepo.count();

		int a[] = new int[hiddenboardCount];

		// 회원수 만큼 for을 돌린다.
		for (int i = 0; i < clientCount; i++) {
			a[i] = r.nextInt(hiddenboardCount) + 1;
			System.out.println(hRepo.findById(a[i]).get().getId());
		}
	}

	public long getCount() {
		return hRepo.count();
	}

	// 테스트 데이터 삽입
	public int randomRange(int n1, int n2) {
		return (int) (Math.random() * (n2 - n1 + 1)) + n1;
	}

	public void makeTest() {
		String[] category = { "A", "B", "C", "D" };
		String[] hashtag = { "#a", "#b", "#c", "#d" };

		for (int i = 32; i <= 53; i++) {
			String a = String.valueOf(i);
			HiddenBoardDTO v = new HiddenBoardDTO();

			v.setCategory(category[randomRange(0, 3)]);
			v.setClaim(0);
			v.setContents(a);
			v.setHashtag(hashtag[randomRange(0, 3)]);
			v.setHeart(0);
			v.setId(a);
			v.setNickname(a);
			v.setOpenDate(i < 10 ? "2019120" + i : "201912" + i);
			v.setPostingDate(i < 10 ? "2019120" + i : "201912" + i);

			hRepo.save(v);
		}
	}

	// 게시글 신고하기 추가
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

	// 미공개 게시판 게시글 작성
	public boolean saveHiddenBoardDTO(HiddenBoardDTO board) throws ParseException {
		boolean result = false;
		if (new SimpleDateFormat("yyyyMMdd").parse(board.getOpenDate()).after(new Date())) {
			hRepo.save(board);
			result = true;
		}

//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//		if (Integer.parseInt(board.getOpenDate()) > Integer.parseInt(format.format(new Date()))) {
//			hRepo.save(board);
//			result = true;
//		}
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
	public Iterable<ReplyDTO> getReply(String repBoardId) {
		return rRepo.findByRepBoardId(repBoardId);
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
	public Integer plusRepHeart(String userId, String repBoardId) {
		// 댓글 찾아오는거 닉네임으로 바꿔야됨 // 유저 아이디도 닉네임으로 => 좋아요누르는사람 닉네임 + 댓글 단 사람의 닉네임 2개필요
		ReplyDTO entity = rRepo.findByUserIdAndRepBoardId(userId, repBoardId);
		System.out.println(entity);
		ArrayList<String> PlusHeartUserList = null;
		if (entity.getPlusHeartUserId() != null && entity.getPlusHeartUserId().size() != 0) {
			PlusHeartUserList = entity.getPlusHeartUserId();
		} else {
			PlusHeartUserList = new ArrayList<>();
		}

		if (judge(PlusHeartUserList, userId)) {
			PlusHeartUserList.add(userId);
			entity.setRepHeart(entity.getRepHeart() + 1);
			entity.setPlusHeartUserId(PlusHeartUserList);
			rRepo.save(entity);
		} else {
			PlusHeartUserList.remove(userId);
			entity.setRepHeart(entity.getRepHeart() - 1);
			entity.setPlusHeartUserId(PlusHeartUserList);
			rRepo.save(entity);
		}
		return entity.getRepHeart();
	}

	// 리플에 좋아요 누른 사람들 가져오기
	public Iterable<ReplyDTO> getReplyByPlusHeartUserId(String plusHeartUserId) {
		return rRepo.findByPlusHeartUserId(plusHeartUserId);
	}
	// =================================================================

	// 로그인 API
	public String kakaoLogin(String code, HttpSession session) {
		String access_Token = login.getKakaoAccessToken(code);
		HashMap<String, Object> userInfo = login.getKakaoUserInfo(access_Token);
		session.setAttribute("id", userInfo.get("id"));
		session.setAttribute("nickname", userInfo.get("nickname"));
		session.setAttribute("email", userInfo.get("email"));
		session.setAttribute("platform", "kakao");

		// 클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
		if (userInfo.get("email") != null) {
			session.setAttribute("userId", userInfo.get("email"));
			session.setAttribute("access_Token", access_Token);
		}
		return "/thymeleaf/session.html";
	}

	public String naverLogin(String code, String state, HttpSession session) throws IOException {
		if (login.token(session, state) == true) {
			String access_Token = login.getNaverAccessToken(code);
			HashMap<String, Object> userInfo = login.getNaverUserInfo(access_Token);
			session.setAttribute("id", userInfo.get("id"));
			session.setAttribute("nickname", userInfo.get("nickName"));
			session.setAttribute("email", userInfo.get("email"));
			session.setAttribute("platform", "naver");

			// 클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
			if (userInfo.get("email") != null) {
				session.setAttribute("userId", userInfo.get("email"));
				session.setAttribute("access_Token", access_Token);
			}
			return "/thymeleaf/session.html";
		}
		// 추후 fail 뷰로 던짐
		return "/thymeleaf/session.html";
	}

	public String logout(HttpSession session) {
		if (session.getAttribute("platform") == "kakao") {
			login.kakaoLogout((String) session.getAttribute("access_Token"));
			session.removeAttribute("access_Token");
			session.removeAttribute("userId");
			// session.invalidate();
			return "login";
		} else if (session.getAttribute("platform") == "naver") {
			session.removeAttribute("access_Token");
			session.removeAttribute("userId");
			session.removeAttribute("state");
			// session.invalidate();
			System.out.println("로그아웃 성공");
			return "login";
		}
		session.invalidate();
		// 추후 fail 뷰로 던짐
		return "login";
	}

}