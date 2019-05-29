#include <jni.h>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_team5_class004_android_activity_CameraActivity_ConvertRGBtoGray(JNIEnv *env, jobject instance,
                                                                     jlong matAddrInput,
                                                                     jlong matAddrResult) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;

    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
//    cvtColor(matInput, matResult, COLOR_RGBA2RGB);

}

extern "C"
JNIEXPORT void JNICALL
Java_team5_class004_android_activity_CameraActivity_ConvertRGBtoBlue(JNIEnv *env, jobject instance,
                                                                     jlong matAddrInput,
                                                                     jlong matAddrResult) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;

    cvtColor(matInput, matResult, COLORMAP_COOL);
}

extern "C"
JNIEXPORT void JNICALL
Java_team5_class004_android_activity_CameraActivity_ConvertRGBtoRed(JNIEnv *env, jobject instance,
                                                                    jlong matAddrInput,
                                                                    jlong matAddrResult) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;

    cvtColor(matInput, matResult, COLORMAP_HOT);
}


extern "C"
JNIEXPORT void JNICALL
Java_team5_class004_android_activity_CameraActivity_ConvertRGBtoOld(JNIEnv *env, jobject instance,
                                                                    jlong matAddrInput,
                                                                    jlong matAddrResult) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;

    Mat_<float> sepia(3,3);
//    sepia << .131,.534,.272
//            ,.168,.686,.349
//            ,.189,.769,.393;
    sepia << .393,.769,.189  // rgb
        ,.349,.686,.168
        ,.272,.534,.131;
    transform(matInput, matResult, sepia);
}


extern "C"
JNIEXPORT void JNICALL
Java_team5_class004_android_activity_CameraActivity_ConvertRGBtoCartoon(JNIEnv *env, jobject instance,
                                                                    jlong matAddrInput,
                                                                    jlong matAddrResult) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;

    cvtColor(matInput, matResult, COLORMAP_HOT);
}


extern "C"
JNIEXPORT void JNICALL
Java_team5_class004_android_activity_CameraActivity_ConvertRGBtoStrong(JNIEnv *env, jobject instance,
                                                                    jlong matAddrInput,
                                                                    jlong matAddrResult) {

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;

    cvtColor(matInput, matResult, COLORMAP_COOL);
}

extern "C"
JNIEXPORT void JNICALL
Java_team5_class004_android_activity_CameraActivity_overlayImage(JNIEnv *env, jobject instance,
                                                                 jlong backgroundAddr,
                                                                 jlong foregroundAddr,
                                                                 jlong outputAddr, jint _x, jint _y,
                                                                 jint _w, jint _h) {

    Mat background = *(Mat*)backgroundAddr;
    Mat foreground = *(Mat*)foregroundAddr;
    Mat output = *(Mat*)outputAddr;

    background.copyTo(output);


    // start at the row indicated by location, or at row 0 if location.y is negative.
    for (int y = std::max(_y, 0); y < background.rows; ++y)
    {
        int fY = y - _y; // because of the translation

        // we are done of we have processed all rows of the foreground image.
        if (fY >= foreground.rows)
            break;

        // start at the column indicated by location,

        // or at column 0 if location.x is negative.
        for (int x = std::max(_x, 0); x < background.cols; ++x)
        {
            int fX = x - _x; // because of the translation.

            // we are done with this row if the column is outside of the foreground image.
            if (fX >= foreground.cols)
                break;

            // determine the opacity of the foregrond pixel, using its fourth (alpha) channel.
            double opacity =
                    ((double)foreground.data[fY * foreground.step + fX * foreground.channels() + 3])

                    / 255.;


            // and now combine the background and foreground pixel, using the opacity,

            // but only if opacity > 0.
            for (int c = 0; opacity > 0 && c < output.channels(); ++c)
            {
                unsigned char foregroundPx =
                        foreground.data[fY * foreground.step + fX * foreground.channels() + c];
                unsigned char backgroundPx =
                        background.data[y * background.step + x * background.channels() + c];
                output.data[y*output.step + output.channels()*x + c] =
                        backgroundPx * (1. - opacity) + foregroundPx * opacity;
            }
        }
    }


}