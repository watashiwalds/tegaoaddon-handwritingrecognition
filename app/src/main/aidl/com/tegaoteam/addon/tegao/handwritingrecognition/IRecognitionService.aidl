// IRecognitionService.aidl
package com.tegaoteam.addon.tegao.handwritingrecognition;

import java.util.List;
//import com.tegaoteam.addon.tegao.handwritingrecognition.IRecognitionCallback;

interface IRecognitionService {
    List<String> requestInputSuggestions(in byte[] input);
//    void requestFinished(IRecognitionCallback callback);
}

// IRecognitionCallback.aidl
//package com.tegaoteam.addon.tegao.handwritingrecognition;
//
//import java.util.List;
//
//interface IRecognitionCallback {
//    void requestFinished(in List<String> suggestion);
//}