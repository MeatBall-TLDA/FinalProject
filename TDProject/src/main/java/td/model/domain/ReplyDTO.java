package td.model.domain;


import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "reply", type = "_doc")
public class ReplyDTO {
   @Id
   private String id;
   private String userId;
   private String repBoardId;
   private String repContents;
   private String repPostingDate;
   private Integer repHeart;
   private Integer repClaim;
   private ArrayList<String> plusHeartUserId;
   private ArrayList<String> plusClaimUserId;
}