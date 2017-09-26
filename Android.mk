LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES	:=	activate support  dspread  gson  javamail  ZCSCombo \
					mpandroidchar  pushservice  universal  orglinphone    

LOCAL_STATIC_LIBRARIES	:=	liba01jni libbdpush_V2_2 libserial_port \
				libJNIEMV libJNISerial libwltdecode 

LOCAL_MODULE_TAGS	:=	optional

LOCAL_SRC_FILES	:=	$(call all-subdir-java-files)

LOCAL_JNI_SHARED_LIBRARIES	:=	liba01jni libserial_port libJNIEMV libJNISerial libwltdecode \
					libbdpush_V2_2 liblinphone-armeabi liblinphone-armeabi-v7a \
					libffmpeg-linphone-arm

LOCAL_PACKAGE_NAME	:=	THTFITPOS

LOCAL_CERTIFICATE	:=	platform
LOCAL_PROGUARD_FLAG_FILES	:=	proguard.flags

LOCAL_PROGUARD_ENABLED	:=	disabled

LOCAL_SRC_FILES	:=	$(call all-java-files-under, src)

include $(BUILD_PACKAGE)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES	:=	activate:libs/activation.jar \
						support:libs/android-support-v4.jar \
						dspread:libs/dspread-android-sdk-2.1.4.jar \
						javamail:libs/javax.mail.jar \
						mpandroidchar:libs/mpandroidchartlibrary-1-7-4.jar \
						pushservice:libs/pushservice-4.3.0.4.jar \
						universal:libs/universal-image-loader-1.8.6-with-sources.jar \
						gson:libs/gson-2.2.4.jar \
						ZCSCombo:libs/ZCSComboV2.5.3.jar \
						xUtils:libs/xUtils-2.6.14.jar \
						orglinphone:libs/orglinphone_src.jar 	

LOCAL_PREBUILT_LIBS	:=	liba01jni:libs/armeabi/liba01jni.so \
				libJNIEMV:libs/armeabi/libJNIEMV.so \
				libserial_port:libs/armeabi/libserial_port.so \
				libJNISerial:libs/armeabi/libJNISerial.so \
				libwltdecode:libs/armeabi/libwltdecode.so \
				libbdpush_V2_2:libs/armeabi/libbdpush_V2_2.so \
				libffmpeg-linphone-arm:libs/armeabi-v7a/libffmpeg-linphone-arm.so \
				liblinphone-armeabi-v7a:libs/armeabi-v7a/liblinphone-armeabi-v7a.so\
				liblinphone-armeabi:libs/armeabi/liblinphone-armeabi.so 
include $(BUILD_MULTI_PREBUILT)
include $(call all-makefiles-under,$(LOCAL_PATH))
