#import <UIKit/UIKit.h>
#import <React/RCTComponent.h>

@interface RNSelectableText : UIView <UITextViewDelegate>

@property (nonatomic, copy) NSString *value;
@property (nonatomic, copy) NSArray *menuItems;
@property (nonatomic, copy) NSArray *highlights;
@property (nonatomic, copy) NSString *highlightColor;
@property (nonatomic, copy) RCTDirectEventBlock onSelection;
@property (nonatomic, copy) RCTDirectEventBlock onHighlightPress;

@end