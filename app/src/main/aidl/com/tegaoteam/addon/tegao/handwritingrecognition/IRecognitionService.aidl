// IRecognitionService.aidl
package com.tegaoteam.addon.tegao.handwritingrecognition;

import com.tegaoteam.addon.tegao.handwritingrecognition.IRecognitionCallback;

interface IRecognitionService {
    void requestInputSuggestions(in byte[] input);
    void registerCallback(IRecognitionCallback callback);
}