package td.model.domain;

import java.util.Date;

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
	private String repBoardId;
	private String repContents;
	private Date repPostingDate;
	private Integer repHeart;
	private Integer repClaim;
	private String[] plusHeartUserId;
}
