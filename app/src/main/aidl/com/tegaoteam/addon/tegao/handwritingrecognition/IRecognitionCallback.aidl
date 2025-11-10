package com.tegaoteam.addon.tegao.handwritingrecognition;

interface IRecognitionCallback {
    void requestFinished(List<String> suggestion);
}