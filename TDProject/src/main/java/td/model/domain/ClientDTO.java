package td.model.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "client")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
	
//  네이버와 카카오 로그인 API과는 별도로 고객으로부터 받아올 정보들
//  닉네임 설정 후 PK값으로 사용
	@Id
	@Column(length = 20)
	private String nickname;
	
//	푸쉬알림 동의 여부 체크
	@Column(nullable = false)
	private boolean alram;
	
	
//	네이버와 카카오 로그인 API 이용 시 받아오는 고객들의 정보
//	이름
	@Column(length = 20, nullable = false)
	private String name;
	
//	메일주소
	@Column(length = 100, nullable = false)
	private String mail;
	
//	주소
	@Column(length = 200, nullable = false)
	private String address;
	
//	생일
	@Column(nullable = false)
	private Date birthday;
	
//	나이
	@Column(length = 4, nullable = false)
	private Integer age;
	
//	성별
	@Column(length = 10, nullable = false)
	private String gender;
}
