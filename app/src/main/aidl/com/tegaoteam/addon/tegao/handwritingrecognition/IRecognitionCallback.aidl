// IRecognitionCallback.aidl
package com.tegaoteam.addon.tegao.handwritingrecognition;

interface IRecognitionCallback {
    void onRecognized(out String[] suggestions);
}