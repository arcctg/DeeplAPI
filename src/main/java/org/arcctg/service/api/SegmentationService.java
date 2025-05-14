package org.arcctg.service.api;

import java.util.List;
import org.arcctg.deepl.model.dto.common.Sentence;

public interface SegmentationService {

    List<Sentence> process(String text);

}
