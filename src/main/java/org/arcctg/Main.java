package org.arcctg;

import org.arcctg.deepl.client.DeeplClient;
import org.arcctg.deepl.model.Language;
import org.arcctg.deepl.model.SourceTargetLangs;

public class Main {

    public static void main(String[] args) {
        DeeplClient client = new DeeplClient();
        var langPair = new SourceTargetLangs(Language.ENGLISH, Language.UKRAINIAN);
        String text = "Hello world! How are you?";

        String translation = client.translate(text, langPair);

        System.out.println(translation);
    }
}