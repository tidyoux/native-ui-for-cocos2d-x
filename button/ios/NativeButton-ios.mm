//
//  NativeButton-ios.mm
//

#include "NativeButton.h"

#if CC_TARGET_PLATFORM == CC_PLATFORM_IOS

#include "platform/ios/CCEAGLView-ios.h"


@interface UIButtonWrapper : NSObject
@property (strong,nonatomic) UIButton * mButton;

-(id) init:(NativeButton *)nativeButton;

-(void) setFrame:(int) left :(int) top :(int) width :(int) height;
-(void) setVisible:(bool) visible;
-(void) setTitle:(const std::string &) title;
-(void) setBackgroundColor:(float) r :(float) g :(float) b :(float) a;

-(void) onClick:(UIButton *) button;

@end

@implementation UIButtonWrapper
{
    NativeButton *mNativeButton;
}

-(id)init:(NativeButton *)nativeButton
{
    if (self = [super init])
    {
        mNativeButton = nativeButton;
        
        auto glView = Director::getInstance()->getOpenGLView();
        auto eaglview = static_cast<CCEAGLView *>(glView->getEAGLView());
        
        self.mButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [self.mButton setFrame:CGRectMake(0, 0, 50, 20)];
        [self.mButton setBackgroundColor:[UIColor colorWithRed:0 green:0 blue:0 alpha:0.5f]];
        [self.mButton setTitle:@"click" forState:UIControlStateNormal];
        [self.mButton setTitleColor:[UIColor colorWithRed:1 green:1 blue:1 alpha:1] forState:UIControlStateNormal];
        [self.mButton addTarget:self action:@selector(onClick:) forControlEvents:UIControlEventTouchUpInside];
        [eaglview addSubview:self.mButton];
    }
    
    return self;
}

-(void) dealloc
{
    if (self.mButton != nullptr)
    {
        [self.mButton removeFromSuperview];
        self.mButton = nullptr;
    }
    
    [super dealloc];
}

-(void) setFrame:(int)left :(int)top :(int)width :(int)height
{
    if (self.mButton != nullptr)
    {
        [self.mButton setFrame:CGRectMake(left, top, width, height)];
    }
}

-(void) setVisible:(bool)visible
{
    if (self.mButton != nullptr)
    {
        [self.mButton setHidden:!visible];
    }
}

-(void) setTitle:(const std::string &)title
{
    if (self.mButton != nullptr)
    {
        [self.mButton setTitle:@(title.c_str()) forState:UIControlStateNormal];
    }
}

-(void) setBackgroundColor:(float) r :(float) g :(float) b :(float) a
{
    if (self.mButton != nullptr)
    {
        [self.mButton setBackgroundColor:[UIColor colorWithRed:r green:g blue:b alpha:a]];
    }
}

-(void) onClick:(UIButton *)button
{
    //
}

@end

////////////////////////////////////////////////////
//
//
class NativeButton::Data
{
public:
    Data(NativeButton *nativeButton)
    {
        buttonWrapper = [[UIButtonWrapper alloc] init:nativeButton];
    }
    
    ~Data()
    {
        if (buttonWrapper != nullptr)
        {
            [buttonWrapper dealloc];
        }
    }
    
    UIButtonWrapper *buttonWrapper;
};
NativeButton::NativeButton()
{
    mData = new Data(this);
}

NativeButton::~NativeButton()
{
    if(mData != nullptr)
    {
        delete mData;
    }
}

void NativeButton::setTitle(const std::string &title)
{
    [mData->buttonWrapper setTitle:title];
}

void NativeButton::setBackgroundColor(const float r, const float g, const float b, const float a)
{
    [mData->buttonWrapper setBackgroundColor:r :g :b :a];
}

void NativeButton::setVisible(bool visible)
{
    Widget::setVisible(visible);
    [mData->buttonWrapper setVisible:visible];
}

void NativeButton::draw(Renderer *renderer, const Mat4 &transform, uint32_t flags)
{
    ui::Widget::draw(renderer, transform, flags);
    
    if (flags & FLAGS_TRANSFORM_DIRTY)
    {
        auto directorInstance = Director::getInstance();
        auto glView = directorInstance->getOpenGLView();
        auto frameSize = glView->getFrameSize();
        auto scaleFactor = [static_cast<CCEAGLView *>(glView->getEAGLView()) contentScaleFactor];
        
        auto winSize = directorInstance->getWinSize();
        
        auto leftBottom = convertToWorldSpace(Vec2::ZERO);
        auto rightTop = convertToWorldSpace(Vec2(_contentSize.width,_contentSize.height));
        
        auto uiLeft = (frameSize.width / 2 + (leftBottom.x - winSize.width / 2) * glView->getScaleX()) / scaleFactor;
        auto uiTop = (frameSize.height / 2 - (rightTop.y - winSize.height / 2) * glView->getScaleY()) / scaleFactor;
        
        [mData->buttonWrapper setFrame :uiLeft :uiTop
                                                       :(rightTop.x - leftBottom.x) * glView->getScaleX() / scaleFactor
                                                       :(rightTop.y - leftBottom.y) * glView->getScaleY() / scaleFactor];
    }
}

#endif
