package com.selectabletext;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RNSelectableTextManager extends SimpleViewManager {
    public static final String REACT_CLASS = "RNSelectableText";
    public static final int COMMAND_SETUP_MENU_ITEMS = 1;

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected RNSelectableText createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new RNSelectableText(reactContext);
    }

    @ReactProp(name = "value")
    public void setValue(RNSelectableText view, @Nullable String value) {
        view.setValue(value);
    }

    @ReactProp(name = "menuItems")
    public void setMenuItems(RNSelectableText view, @Nullable ReadableArray menuItems) {
        if (menuItems != null) {
            List items = new ArrayList<>();
            for (int i = 0; i < menuItems.size(); i++) {
                items.add(menuItems.getString(i));
            }
            view.setMenuItems(items);
        }
    }

    @ReactProp(name = "highlights")
    public void setHighlights(RNSelectableText view, @Nullable ReadableArray highlights) {
        view.setHighlights(highlights);
    }

    @ReactProp(name = "highlightColor")
    public void setHighlightColor(RNSelectableText view, @Nullable String color) {
        view.setHighlightColor(color);
    }

    @Nullable
    @Override
    public Map getCommandsMap() {
        return MapBuilder.of("setupMenuItems", COMMAND_SETUP_MENU_ITEMS);
    }

    @Override
    public void receiveCommand(@NonNull RNSelectableText root, String commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);
        int command = Integer.parseInt(commandId);
        
        if (command == COMMAND_SETUP_MENU_ITEMS && args != null) {
            List items = new ArrayList<>();
            ReadableArray menuItems = args.getArray(0);
            if (menuItems != null) {
                for (int i = 0; i < menuItems.size(); i++) {
                    items.add(menuItems.getString(i));
                }
            }
            root.setMenuItems(items);
        }
    }

    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.builder()
                .put("topSelection", MapBuilder.of("registrationName", "onSelection"))
                .put("topHighlightPress", MapBuilder.of("registrationName", "onHighlightPress"))
                .build();
    }
}