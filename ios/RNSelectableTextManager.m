#import "RNSelectableTextManager.h"
#import "RNSelectableText.h"
#import <React/RCTUIManager.h>
#import <React/RCTBridge.h>

@implementation RNSelectableTextManager

RCT_EXPORT_MODULE(RNSelectableText)

- (UIView *)view {
    return [[RNSelectableText alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(value, NSString)
RCT_EXPORT_VIEW_PROPERTY(menuItems, NSArray)
RCT_EXPORT_VIEW_PROPERTY(highlights, NSArray)
RCT_EXPORT_VIEW_PROPERTY(highlightColor, NSString)
RCT_EXPORT_VIEW_PROPERTY(onSelection, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onHighlightPress, RCTDirectEventBlock)

@end