package top.damon.database.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Damon
 * @Date 2021/4/29 9:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CsvModel implements Serializable {

    private static final long serialVersionUID = -6521229339125040106L;

    private String fileName;

    private List<CsvModelData> data;
}
