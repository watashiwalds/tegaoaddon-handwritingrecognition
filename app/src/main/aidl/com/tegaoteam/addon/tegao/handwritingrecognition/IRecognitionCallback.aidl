// IRecognitionCallback.aidl
package com.tegaoteam.addon.tegao.handwritingrecognition;

import java.util.List;

interface IRecognitionCallback {
    void onRecognized(out List<String> suggestions);
}