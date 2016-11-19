//
//  NativeButton.h
//

#ifndef _NativeButton_h_
#define _NativeButton_h_

#include "cocos2d.h"
#include "ui/CocosGUI.h"

using namespace cocos2d;

#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID || CC_TARGET_PLATFORM == CC_PLATFORM_IOS)

class NativeButton : public ui::Widget
{
public:
    NativeButton();
    virtual ~NativeButton();
    
    CREATE_FUNC(NativeButton);
    
    virtual void setTitle(const std::string &title);
    virtual void setBackgroundColor(const float r, const float g, const float b, const float a);
    
    virtual void setVisible(bool visible) override;
    virtual void draw(Renderer *renderer, const Mat4& transform, uint32_t flags) override;
    
private:
    class Data;
    Data *mData;
};

#endif /* _NativeButton_h_ */
#endif
