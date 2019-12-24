package td.model.domain;


import java.util.ArrayList;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Document(indexName = "hidden_board", type = "_doc")
public class HiddenBoardDTO {
	  @Id
	  private String id;
	  private String contents;
	  private String hashtag;
	  private String postingDate;
	  private String openDate;
	  private Integer heart;
	  private Integer claim;
	  private String nickname;
	  private String category;
	  private ArrayList<String> plusHeartUserId;
	  private ArrayList<String> plusClaimUserId;
	  
	  public HiddenBoardDTO(String hashtag) {
			super();
			this.hashtag = hashtag;
		}
}

