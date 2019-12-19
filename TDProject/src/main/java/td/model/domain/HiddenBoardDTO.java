package td.model.domain;

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
	  private String contents;
	  private String hashtag;
	  private String postingDate;
	  private String openDate;
	  private Integer heart;
	  private Integer claim;
	  private String nickname;
	  private String category;
}

