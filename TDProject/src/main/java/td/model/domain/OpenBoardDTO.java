package td.model.domain;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "open_board", type = "_doc")
public class OpenBoardDTO {
	  @Id
	  private String id;
	  private String contents;
	  private String hashtag;
	  private String postingDate;
	  private Integer heart;
	  private Integer claim;
	  private String nickname;
	  private String category;
	  private ArrayList<String> plusHeartUserId;
	  private ArrayList<String> claimHeartUserId;
}
