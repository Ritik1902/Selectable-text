package com.selectabletext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RNSelectableText extends AppCompatTextView {
    private List menuItems = new ArrayList<>();
    private List highlights = new ArrayList<>();
    private int highlightColor = Color.YELLOW;
    private ActionMode actionMode;
    private ReactContext reactContext;
    
    private static class HighlightInfo {
        String id;
        int start;
        int end;
        
        HighlightInfo(String id, int start, int end) {
            this.id = id;
            this.start = start;
            this.end = end;
        }
    }

    public RNSelectableText(Context context) {
        super(context);
        this.reactContext = (ReactContext) context;
        
        setTextIsSelectable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
        setLongClickable(true);
        
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                actionMode = mode;
                menu.clear();
                
                for (int i = 0; i < menuItems.size(); i++) {
                    menu.add(0, i, i, menuItems.get(i));
                }
                
                return menuItems.size() > 0;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int start = getSelectionStart();
                int end = getSelectionEnd();
                String selectedText = getText().subSequence(start, end).toString();
                String eventType = item.getTitle().toString();
                
                WritableMap event = Arguments.createMap();
                event.putString("eventType", eventType);
                event.putString("content", selectedText);
                event.putInt("selectionStart", start);
                event.putInt("selectionEnd", end);
                
                reactContext.getJSModule(RCTEventEmitter.class)
                        .receiveEvent(getId(), "topSelection", event);
                
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        });
    }

    public void setValue(String value) {
        setText(value);
        applyHighlights();
    }

    public void setMenuItems(List items) {
        this.menuItems = items;
    }

    public void setHighlights(@Nullable ReadableArray highlightsArray) {
        highlights.clear();
        
        if (highlightsArray != null) {
            for (int i = 0; i < highlightsArray.size(); i++) {
                ReadableMap highlight = highlightsArray.getMap(i);
                if (highlight != null) {
                    String id = highlight.hasKey("id") ? highlight.getString("id") : null;
                    int start = highlight.getInt("start");
                    int end = highlight.getInt("end");
                    highlights.add(new HighlightInfo(id, start, end));
                }
            }
        }
        
        applyHighlights();
    }

    public void setHighlightColor(String color) {
        if (color != null) {
            try {
                highlightColor = Color.parseColor(color);
            } catch (IllegalArgumentException e) {
                highlightColor = Color.YELLOW;
            }
        }
        applyHighlights();
    }

    private void applyHighlights() {
        CharSequence text = getText();
        if (text == null || text.length() == 0) return;
        
        SpannableString spannable = new SpannableString(text);
        
        for (HighlightInfo highlight : highlights) {
            if (highlight.start >= 0 && highlight.end <= text.length() && highlight.start < highlight.end) {
                spannable.setSpan(
                    new BackgroundColorSpan(highlightColor),
                    highlight.start,
                    highlight.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        
        setText(spannable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            
            x -= getTotalPaddingLeft();
            y -= getTotalPaddingTop();
            
            x += getScrollX();
            y += getScrollY();
            
            Layout layout = getLayout();
            if (layout != null) {
                int line = layout.getLineForVertical(y);
                int offset = layout.getOffsetForHorizontal(line, x);
                
                for (HighlightInfo highlight : highlights) {
                    if (offset >= highlight.start && offset < highlight.end) {
                        WritableMap event2 = Arguments.createMap();
                        event2.putString("id", highlight.id != null ? highlight.id : "");
                        
                        reactContext.getJSModule(RCTEventEmitter.class)
                                .receiveEvent(getId(), "topHighlightPress", event2);
                        return true;
                    }
                }
            }
        }
        
        return super.onTouchEvent(event);
    }
}