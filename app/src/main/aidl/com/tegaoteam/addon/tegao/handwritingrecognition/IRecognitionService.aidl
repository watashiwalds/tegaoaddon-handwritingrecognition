package com.tegaoteam.addon.tegao.handwritingrecognition;
import java.io.ByteArrayOutputStream;

interface IRecognitionService {
    List<String> requestInputSuggestions(ByteArrayOutputStream input);
    void requestFinished(IRecognitionCallback callback);
}