package td.model.domain;

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
   private String id;

   @Column(length = 100, nullable = false)
   private String nickName;
   
   @Column(length = 100, nullable = false)
   private String serviceName;

   //   메일주소
   @Column(length = 100, nullable = false)
   private String email;
   
// 푸쉬알림 동의 여부 체크
   @Column(nullable = false)
   private boolean alram;
   
   @Column(length = 100, nullable = true)
   private String randomMessageId;
}