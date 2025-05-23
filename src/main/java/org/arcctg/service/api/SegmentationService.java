package org.arcctg.service.api;

import com.google.inject.ImplementedBy;
import java.util.List;
import org.arcctg.deepl.model.dto.common.Sentence;
import org.arcctg.service.impl.translation.DefaultSegmentationService;

@ImplementedBy(DefaultSegmentationService.class)
public interface SegmentationService {

    List<Sentence> process(String text);

}
