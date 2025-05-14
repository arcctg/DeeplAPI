package org.arcctg.deepl.service.interfaces;

import java.util.List;
import org.arcctg.model.common.Sentence;

public interface SegmentationService {

    List<Sentence> process(String text);

}
