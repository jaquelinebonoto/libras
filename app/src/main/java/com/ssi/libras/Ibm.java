package com.ssi.libras;

import com.ibm.cloud.sdk.core.http.HttpConfigOptions;
import com.ibm.cloud.sdk.core.security.BasicAuthenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class Ibm {

    String text, text2, text3;
    public  String setUpIbmService(String pathSave, String recordFile){
        BasicAuthenticator authenticator = new BasicAuthenticator("email", "senha");
        IamAuthenticator authenticatorUrl = new IamAuthenticator("chaveibm");
        SpeechToText speechToText = new SpeechToText(authenticatorUrl);
        speechToText.setServiceUrl("https://api.us-south.speech-to-text.watson.cloud.ibm.com/instances/iddasuainstancia");

        HttpConfigOptions configOptions = new HttpConfigOptions.Builder()
                .disableSslVerification(true)
                .build();
        speechToText.configureClient(configOptions);
        try {
            RecognizeOptions recognizeOptions = new RecognizeOptions.Builder()
                    .audio(new FileInputStream(pathSave + "/" + recordFile))
                    .contentType("audio/ogg")
                    .model("pt-BR_BroadbandModel")
                    .smartFormatting(true)
                    .keywords(Arrays.asList("bebê", "calmo", "noite"))
                    .keywordsThreshold((float) 0.5)
                    .maxAlternatives(3)
                    .build();

            BaseRecognizeCallback baseRecognizeCallback =
                    new BaseRecognizeCallback() {

                        @Override
                        public void onTranscription
                                (SpeechRecognitionResults speechRecognitionResults) {
                            System.out.println(speechRecognitionResults);
                            if(speechRecognitionResults.getResults() != null && !speechRecognitionResults.getResults().isEmpty()){
                                text2 = speechRecognitionResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                                text3 = speechRecognitionResults.getResults().get(0).getAlternatives().get(2).getTranscript();
                                text = speechRecognitionResults.getResults().get(0).getAlternatives().get(1).getTranscript();
                                System.out.println("Result 1: " + text2);
                                System.out.println("Result 2: " + text);
                                System.out.println("Result 3: " + text3);
                            }
                        }

                        @Override
                        public void onDisconnected() {
                            System.out.println("Funcionou");
                        }

                    };

            speechToText.recognizeUsingWebSocket(recognizeOptions,
                    baseRecognizeCallback);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return text2;
    }
}
