package org.arcctg;

import org.arcctg.deepl.DeeplClient;
import org.arcctg.deepl.Language;
import org.arcctg.deepl.SourceTargetLangs;

public class Main {

    public static void main(String[] args) {
        DeeplClient client = new DeeplClient();
        var langPair = new SourceTargetLangs(Language.ENGLISH, Language.UKRAINIAN);
        String text = "Hello world! How are you?";

        String translation = client.translate(text, langPair);

        System.out.println(translation);
    }
}