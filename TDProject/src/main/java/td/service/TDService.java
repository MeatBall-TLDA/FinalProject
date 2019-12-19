package td.service;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

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
	// 고객 정보

	public Optional<ClientDTO> findByIdClientDTO(String id) {
		return cRepo.findById(id);
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
		return hRepo.findByCategoryContaining(pageable, category);
	}

	// 공개 날짜에 맞추어 게시글 공개 메소드
	public void moveToOpen() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		Date time = new Date();
		String today = format1.format(time);

		for (HiddenBoardDTO v : hRepo.findByOpenDate(today)) {
			oRepo.save(new OpenBoardDTO(v.getId(), v.getContents(), v.getHashtag(), v.getOpenDate(), v.getHeart(),
					v.getClaim(), v.getNickname(), v.getCategory()));
			//hRepo.deleteById(v.getId());
		}
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

		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		Date time = new Date();
		String time1 = format1.format(time);

		for (int i = 0; i < 502; i++) {
			String a = String.valueOf(i);
			HiddenBoardDTO v = new HiddenBoardDTO();

			v.setCategory(category[randomRange(0, 3)]);
			v.setClaim(0);
			v.setContents(a);
			v.setHashtag(hashtag[randomRange(0, 3)]);
			v.setHeart(0);
			v.setId(a);
			v.setNickname(a);
			v.setOpenDate(time1);
			v.setPostingDate(time1);

			hRepo.save(v);
		}
	}

	// 미공개 게시판 게시글 작성
	public boolean saveHiddenBoardDTO(HiddenBoardDTO board) {
		boolean result = false;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		if (Integer.parseInt(board.getOpenDate()) > Integer.parseInt(format.format(new Date()))) {
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
	public ReplyDTO getReply(String userId, String boardId) {
		return rRepo.findByUserIdAndRepBoardId(userId, boardId);
	}

	public boolean saveReply(ReplyDTO reply) {
		boolean result = false;
		String content = reply.getRepContents();
		if (content != null && content.trim().length() > 5) {
			result = true;
			rRepo.save(reply);
		}
		return result;
	}

	// =================================================================

	// 로그인 API

	public String login(String code, HttpSession session) {
		String access_Token = login.getAccessToken(code);
		HashMap<String, Object> userInfo = login.getUserInfo(access_Token);
		System.out.println("login Controller : " + userInfo);
		session.setAttribute("platform", "kakao");

		// 클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
		if (userInfo.get("email") != null) {
			session.setAttribute("userId", userInfo.get("email"));
			session.setAttribute("access_Token", access_Token);
		}
		return "login";
	}

	public String naverLogin(String code, String state, HttpSession session) throws IOException {

		if (login.token(session, state) == true) {
			String access_Token = login.getNaverAccessToken(code);
			HashMap<String, Object> userInfo = login.getNaverUserInfo(access_Token);
			System.out.println("login Controller : " + userInfo);
			session.setAttribute("platform", "naver");
			// 클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
			if (userInfo.get("email") != null) {
				session.setAttribute("userId", userInfo.get("email"));
				session.setAttribute("access_Token", access_Token);
			}
			return "login";
		}
		// 추후 fail 뷰로 던짐
		return "login";
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
