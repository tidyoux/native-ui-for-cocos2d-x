//
//  NativeButton-android.cpp
//

#include "NativeButton.h"

#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
#include <jni.h>
#include <unordered_map>
#include "jni/JniHelper.h"

#define  CLASS_NAME "org/cocos2dx/lib/Cocos2dxButtonHelper"

static int createButtonJNI()
{
	JniMethodInfo t;
    int ret = -1;
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "createButton", "()I")) {
        ret = t.env->CallStaticIntMethod(t.classID, t.methodID);

        t.env->DeleteLocalRef(t.classID);
    }

    return ret;
}

static void removeButtonJNI(int index)
{
	JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "removeButton", "(I)V")) {
        t.env->CallStaticVoidMethod(t.classID, t.methodID, index);

        t.env->DeleteLocalRef(t.classID);
    }
}

static void setPositionJNI(int index, int x, int y)
{
	JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setPosition", "(III)V")) {
        t.env->CallStaticVoidMethod(t.classID, t.methodID, index, x, y);

        t.env->DeleteLocalRef(t.classID);
    }
}

static void setVisibleJNI(int index, bool visible)
{
	JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setVisible", "(IZ)V")) {
        t.env->CallStaticVoidMethod(t.classID, t.methodID, index, visible);

        t.env->DeleteLocalRef(t.classID);
    }
}

static void setTitleJNI(int index, const std::string &title)
{
	JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setTitle", "(ILjava/lang/String;)V")) {
        jstring stringArg = t.env->NewStringUTF(title.c_str());
        t.env->CallStaticVoidMethod(t.classID, t.methodID, index, stringArg);

        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg);
    }
}

static void setBackgroundColorJNI(int index, const float r, const float g, const float b, const float a)
{
	JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setBackgroundColor", "(IFFFF)V")) {
        t.env->CallStaticVoidMethod(t.classID, t.methodID, index, r, g, b, a);

        t.env->DeleteLocalRef(t.classID);
    }
}

////////////////////////////////////////////////////
//
//
static std::unordered_map<int, NativeButton*> s_allNativeButtons;

class NativeButton::Data
{
public:
    Data(NativeButton *nativeButton)
    {
		index = createButtonJNI();
    	s_allNativeButtons[index] = nativeButton;
    }

    ~Data()
    {
		s_allNativeButtons.erase(index);
    	removeButtonJNI(index);
    }

	int index;
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
    setTitleJNI(mData->index, title);
}

void NativeButton::setBackgroundColor(const float r, const float g, const float b, const float a)
{
    setBackgroundColorJNI(mData->index, r, g, b, a);
}

void NativeButton::setVisible(bool visible)
{
    Widget::setVisible(visible);
	setVisibleJNI(mData->index, visible);
}

void NativeButton::draw(Renderer *renderer, const Mat4 &transform, uint32_t flags)
{
    if (flags & FLAGS_TRANSFORM_DIRTY)
    {
        auto rect = ui::Helper::convertBoundingBoxToScreen(this);
		setPositionJNI(mData->index, (int)rect.origin.x, (int)rect.origin.y);
    }
}

//////////////////////////////////////////////////////
//
//
extern "C" {
    void Java_org_cocos2dx_lib_Cocos2dxButtonHelper_onButtonClick(JNIEnv * env, jobject obj, jint index) {
		auto it = s_allNativeButtons.find(index);
		if (it != s_allNativeButtons.end())
		{
			//
		}
	}
}

#endif
