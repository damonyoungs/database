package org.damon.database.util;

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
public class CsvModelData implements Serializable {

    private static final long serialVersionUID = -6809900700104976350L;

    private List<String> head;

    private List<String> title;

    private List<List<String>> row;
}
