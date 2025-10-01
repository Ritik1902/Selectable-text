package com.selectabletext;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RNSelectableTextPackage implements ReactPackage {
    @Override
    public List createNativeModules(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.asList(new RNSelectableTextManager());
    }
}