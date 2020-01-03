package td.model.domain;


import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "hidden_board", type = "_doc")
public class HiddenBoardDTO {
	  @Id
	  private String id;
	  private String title;
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
