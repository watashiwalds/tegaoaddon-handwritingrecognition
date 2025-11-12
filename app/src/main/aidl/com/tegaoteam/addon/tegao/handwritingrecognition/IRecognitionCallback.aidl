// IRecognitionCallback.aidl
package com.tegaoteam.addon.tegao.handwritingrecognition;

interface IRecognitionCallback {
    void onRecognized(in String[] suggestions);
}