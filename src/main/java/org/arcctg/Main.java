package org.arcctg;

import org.arcctg.deepl.DeeplClient;
import org.arcctg.deepl.Language;
import org.arcctg.deepl.SourceTargetLangs;

public class Main {

    public static void main(String[] args) {
        DeeplClient client = new DeeplClient();
        var langPair = new SourceTargetLangs(Language.ENGLISH, Language.UKRAINIAN);
        String translation = client.translate("Hello world! How are you?", langPair);

        System.out.println(translation);
    }
}