package jaringobi.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchPage implements Serializable {

    final static int DEFAULT_PAGE = 0;
    final static int DEFAULT_SIZE = 10;
    final static int FIRST_PAGE = 1;
    final static int MAXIMUM_SIZE = 100;

    @Range(min = DEFAULT_SIZE, max = MAXIMUM_SIZE)
    private Integer size;

    @Range(min = FIRST_PAGE)
    private Integer page;

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}
