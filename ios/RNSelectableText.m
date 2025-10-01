#import "RNSelectableText.h"
#import <React/RCTUtils.h>

@interface RNSelectableText()
@property (nonatomic, strong) UITextView *textView;
@property (nonatomic, strong) NSMutableArray *highlightViews;
@end

@implementation RNSelectableText

- (instancetype)init {
    if (self = [super init]) {
        _textView = [[UITextView alloc] init];
        _textView.delegate = self;
        _textView.editable = NO;
        _textView.scrollEnabled = NO;
        _textView.backgroundColor = [UIColor clearColor];
        _textView.textContainerInset = UIEdgeInsetsZero;
        _textView.textContainer.lineFragmentPadding = 0;
        
        [self addSubview:_textView];
        
        _highlightViews = [NSMutableArray array];
        
        // Add long press gesture for custom menu
        UILongPressGestureRecognizer *longPress = [[UILongPressGestureRecognizer alloc] 
                                                    initWithTarget:self 
                                                    action:@selector(handleLongPress:)];
        [_textView addGestureRecognizer:longPress];
        
        // Add tap gesture for highlights
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] 
                                       initWithTarget:self 
                                       action:@selector(handleTap:)];
        [self addGestureRecognizer:tap];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    _textView.frame = self.bounds;
    [self updateHighlights];
}

- (void)setValue:(NSString *)value {
    _value = value;
    _textView.text = value;
    [self setNeedsLayout];
}

- (void)setHighlights:(NSArray *)highlights {
    _highlights = highlights;
    [self updateHighlights];
}

- (void)setHighlightColor:(NSString *)highlightColor {
    _highlightColor = highlightColor;
    [self updateHighlights];
}

- (void)updateHighlights {
    // Remove old highlight views
    for (UIView *view in _highlightViews) {
        [view removeFromSuperview];
    }
    [_highlightViews removeAllObjects];
    
    if (!_highlights || _highlights.count == 0) return;
    
    UIColor *color = [self colorFromHexString:_highlightColor] ?: [UIColor yellowColor];
    
    for (NSDictionary *highlight in _highlights) {
        NSInteger start = [highlight[@"start"] integerValue];
        NSInteger end = [highlight[@"end"] integerValue];
        
        if (start < 0 || end > _textView.text.length || start >= end) continue;
        
        NSRange range = NSMakeRange(start, end - start);
        UITextPosition *startPos = [_textView positionFromPosition:_textView.beginningOfDocument offset:start];
        UITextPosition *endPos = [_textView positionFromPosition:_textView.beginningOfDocument offset:end];
        
        if (!startPos || !endPos) continue;
        
        UITextRange *textRange = [_textView textRangeFromPosition:startPos toPosition:endPos];
        NSArray *rects = [_textView selectionRectsForRange:textRange];
        
        for (UITextSelectionRect *selectionRect in rects) {
            UIView *highlightView = [[UIView alloc] initWithFrame:selectionRect.rect];
            highlightView.backgroundColor = color;
            highlightView.alpha = 0.3;
            highlightView.userInteractionEnabled = NO;
            highlightView.tag = start; // Store start position as tag
            
            [_textView addSubview:highlightView];
            [_highlightViews addObject:highlightView];
        }
    }
}

- (UIColor *)colorFromHexString:(NSString *)hexString {
    if (!hexString) return nil;
    
    NSString *cleanString = [hexString stringByReplacingOccurrencesOfString:@"#" withString:@""];
    if (cleanString.length != 6) return nil;
    
    unsigned int rgbValue = 0;
    NSScanner *scanner = [NSScanner scannerWithString:cleanString];
    [scanner scanHexInt:&rgbValue];
    
    return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 
                           green:((rgbValue & 0x00FF00) >> 8)/255.0 
                            blue:(rgbValue & 0x0000FF)/255.0 
                           alpha:1.0];
}

- (void)handleLongPress:(UILongPressGestureRecognizer *)gesture {
    if (gesture.state == UIGestureRecognizerStateBegan) {
        [self showCustomMenu];
    }
}

- (void)handleTap:(UITapGestureRecognizer *)gesture {
    CGPoint location = [gesture locationInView:_textView];
    
    for (NSDictionary *highlight in _highlights) {
        NSInteger start = [highlight[@"start"] integerValue];
        NSInteger end = [highlight[@"end"] integerValue];
        NSRange range = NSMakeRange(start, end - start);
        
        UITextPosition *startPos = [_textView positionFromPosition:_textView.beginningOfDocument offset:start];
        UITextPosition *endPos = [_textView positionFromPosition:_textView.beginningOfDocument offset:end];
        
        if (!startPos || !endPos) continue;
        
        UITextRange *textRange = [_textView textRangeFromPosition:startPos toPosition:endPos];
        CGRect rect = [_textView firstRectForRange:textRange];
        
        if (CGRectContainsPoint(rect, location)) {
            if (_onHighlightPress) {
                _onHighlightPress(@{@"id": highlight[@"id"] ?: @""});
            }
            break;
        }
    }
}

- (void)showCustomMenu {
    UITextRange *selectedRange = _textView.selectedTextRange;
    if (!selectedRange || [selectedRange isEmpty]) return;
    
    NSInteger start = [_textView offsetFromPosition:_textView.beginningOfDocument 
                                         toPosition:selectedRange.start];
    NSInteger end = [_textView offsetFromPosition:_textView.beginningOfDocument 
                                       toPosition:selectedRange.end];
    NSString *selectedText = [_textView textInRange:selectedRange];
    
    UIMenuController *menuController = [UIMenuController sharedMenuController];
    NSMutableArray *menuItems = [NSMutableArray array];
    
    for (NSString *itemTitle in _menuItems) {
        UIMenuItem *item = [[UIMenuItem alloc] initWithTitle:itemTitle 
                                                      action:@selector(customMenuAction:)];
        [menuItems addObject:item];
    }
    
    menuController.menuItems = menuItems;
    
    // Store selection info for later use
    objc_setAssociatedObject(self, @"selectionStart", @(start), OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    objc_setAssociatedObject(self, @"selectionEnd", @(end), OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    objc_setAssociatedObject(self, @"selectedText", selectedText, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (void)customMenuAction:(id)sender {
    UIMenuItem *item = (UIMenuItem *)sender;
    NSString *eventType = item.title;
    
    NSNumber *start = objc_getAssociatedObject(self, @"selectionStart");
    NSNumber *end = objc_getAssociatedObject(self, @"selectionEnd");
    NSString *content = objc_getAssociatedObject(self, @"selectedText");
    
    if (_onSelection && content) {
        _onSelection(@{
            @"eventType": eventType,
            @"content": content,
            @"selectionStart": start ?: @0,
            @"selectionEnd": end ?: @0
        });
    }
}

- (BOOL)canBecomeFirstResponder {
    return YES;
}

@end