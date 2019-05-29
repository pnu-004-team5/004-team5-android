package team5.class004.android.activity;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Random;

import team5.class004.android.R;
import team5.class004.android.databinding.ActivityCameraBinding;
import team5.class004.android.utils.StorageHelper;
import team5.class004.android.widget.CvCameraPreview;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MPEG4;
import static org.bytedeco.opencv.global.opencv_core.CV_8U;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC4;
import static org.bytedeco.opencv.global.opencv_core.addWeighted;
import static org.bytedeco.opencv.global.opencv_core.bitwise_and;
import static org.bytedeco.opencv.global.opencv_core.transform;
import static org.bytedeco.opencv.global.opencv_imgproc.COLORMAP_AUTUMN;
import static org.bytedeco.opencv.global.opencv_imgproc.COLORMAP_COOL;
import static org.bytedeco.opencv.global.opencv_imgproc.COLORMAP_HOT;
import static org.bytedeco.opencv.global.opencv_imgproc.COLORMAP_JET;
import static org.bytedeco.opencv.global.opencv_imgproc.COLORMAP_PARULA;
import static org.bytedeco.opencv.global.opencv_imgproc.COLORMAP_SPRING;
import static org.bytedeco.opencv.global.opencv_imgproc.COLORMAP_SUMMER;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_RGB2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_RGBA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_RGBA2YUV_I420;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_GRAY2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_GRAY2RGBA;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_INTER_AREA;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RGB2RGBA;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RGBA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.INTER_CUBIC;
import static org.bytedeco.opencv.global.opencv_imgproc.INTER_LINEAR;
import static org.bytedeco.opencv.global.opencv_imgproc.LINE_8;
import static org.bytedeco.opencv.global.opencv_imgproc.applyColorMap;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;


public class CameraActivity extends AppCompatActivity implements CvCameraPreview.CvCameraViewListener {

    private final static String CLASS_LABEL = "RecordActivity";
    private final static String LOG_TAG = CLASS_LABEL;
    CameraActivity mActivity = this;
    ActivityCameraBinding activityBinding;
    private PowerManager.WakeLock wakeLock;
    private boolean isRecording;
    //    Random random = new Random();
    private File savePath = new File(Environment.getExternalStorageDirectory(), new Random().nextInt(99999) + ".mp4");
    private FFmpegFrameRecorder recorder;
    private long startTime = 0;
    private OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
    OpenCVFrameConverter.ToOrgOpenCvCoreMat converterToCvMat = new OpenCVFrameConverter.ToOrgOpenCvCoreMat();
    private final Object semaphore = new Object();
    private CascadeClassifier faceDetector;
    private int absoluteFaceSize = 0;
    Mat maskImage;
    Mat dogImage;
    //    Mat maskImage;
    int[] faceSize = {0, 0};
    enum PreviewOptions {
        DEFAULT, DOG, MASK, GRAY, RED, BLUE, OLD, CARTOON, STRONG
    }
    PreviewOptions currentPreviewOptions = PreviewOptions.DEFAULT;

    double weight = 0.9;
    Mat overlayColor;

//    private CascadeClassifier faceDetector;
//    private int absoluteFaceSize = 0;
//    opencv_face.FaceRecognizer faceRecognizer = opencv_face.EigenFaceRecognizer.create();

    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);
    public native void overlayImage(long backgroundAddr, long foregroundAddr, long outputAddr, int _x, int _y, int _w, int _h);
    public native void ConvertRGBtoBlue(long matAddrInput, long matAddrResult);
    public native void ConvertRGBtoRed(long matAddrInput, long matAddrResult);
    public native void ConvertRGBtoOld(long matAddrInput, long matAddrResult);
    public native void ConvertRGBtoCartoon(long matAddrInput, long matAddrResult);
    public native void ConvertRGBtoStrong(long matAddrInput, long matAddrResult);


    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_camera);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        wakeLock.acquire();

        // = new OpenCVFrameConverter.ToMat()

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                faceDetector = StorageHelper.loadClassifierCascade(mActivity, R.raw.frontalface);
                return null;
            }
        }.execute();

        initLayout();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
            wakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }

        if (recorder != null) {
            try {
                recorder.release();
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isRecording) {
                stopRecording();
            }

            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initLayout() {
//        btnRecorderControl = (Button) findViewById(R.id.recorder_control);
//        btnRecorderControl.setText("Start");
//        btnRecorderControl.setOnClickListener(this);

        activityBinding.btnMaskDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    overlayColor = converterToMat.convert(converterToMat.convert(Utils.loadResource(mActivity, R.drawable.bluegreen)));
                    Imgproc.cvtColor(converterToCvMat.convert(converterToCvMat.convert(overlayColor)), converterToCvMat.convert(converterToCvMat.convert(overlayColor)), Imgproc.COLOR_BGR2RGB, Imgcodecs.IMREAD_UNCHANGED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentPreviewOptions = PreviewOptions.DEFAULT;
            }
        });
        activityBinding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording();
                    isRecording = true;
                    Log.w(LOG_TAG, "Start Button Pushed");
//                    activityBinding.btnRecord.setText("Stop");
                    activityBinding.btnRecord.setBackgroundResource(R.drawable.bg_red_circle_button);
                } else {
                    // This will trigger the audio recording loop to stop and then set isRecorderStart = false;
                    stopRecording();
                    isRecording = false;
                    Log.w(LOG_TAG, "Stop Button Pushed");
//                    activityBinding.btnRecord.setText("Start");
                    activityBinding.btnRecord.setBackgroundResource(R.drawable.bg_green_circle_button);
                    Toast.makeText(mActivity, "녹화된 비디오 파일이 저장되었습니다. 경로: \"" + savePath + "\"", Toast.LENGTH_LONG).show();
                }
            }
        });
        activityBinding.btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Camera.CameraInfo info = new Camera.CameraInfo();
//                Camera.getCameraInfo(activityBinding.cameraView.getCameraId(), info);
//                boolean isFrontFaceCamera = info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
//                if(isFrontFaceCamera)
//                    activityBinding.cameraView.cameraDevice.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//                else
//                    activityBinding.cameraView.cameraDevice.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
//                activityBinding.cameraView.cameraDevice.startPreview();

                activityBinding.cameraView.disconnectCamera();
                activityBinding.cameraView.toggleCameraType();
                activityBinding.cameraView.connectCamera();
//                currentCameraIndex = currentCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK ? CameraBridgeViewBase.CAMERA_ID_FRONT : CameraBridgeViewBase.CAMERA_ID_BACK;
//                mOpenCvCameraView.setCameraIndex(currentCameraIndex);
//                mOpenCvCameraView.enableView();
            }
        });

        activityBinding.btnMaskDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    maskImage = converterToMat.convert(converterToMat.convert(Utils.loadResource(mActivity, R.drawable.mask_dog)));
                    Imgproc.cvtColor(converterToCvMat.convert(converterToCvMat.convert(maskImage)), converterToCvMat.convert(converterToCvMat.convert(maskImage)), Imgproc.COLOR_RGB2BGRA, Imgcodecs.IMREAD_UNCHANGED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentPreviewOptions = PreviewOptions.DOG;
            }
        });
        activityBinding.btnMaskMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    maskImage = converterToMat.convert(converterToMat.convert(Utils.loadResource(mActivity, R.drawable.mask_mask)));
                    Imgproc.cvtColor(converterToCvMat.convert(converterToCvMat.convert(maskImage)), converterToCvMat.convert(converterToCvMat.convert(maskImage)), Imgproc.COLOR_RGB2BGRA, Imgcodecs.IMREAD_UNCHANGED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentPreviewOptions = PreviewOptions.MASK;
            }
        });
        activityBinding.btnMaskGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPreviewOptions = PreviewOptions.GRAY;
            }
        });
        activityBinding.btnMaskOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    weight = 0.9;
                    overlayColor = converterToMat.convert(converterToMat.convert(Utils.loadResource(mActivity, R.drawable.red)));
                    Imgproc.cvtColor(converterToCvMat.convert(converterToCvMat.convert(overlayColor)), converterToCvMat.convert(converterToCvMat.convert(overlayColor)), Imgproc.COLOR_RGB2GRAY, Imgcodecs.IMREAD_GRAYSCALE);

                }catch (Exception e) {
                    e.printStackTrace();
                }
                currentPreviewOptions = PreviewOptions.OLD;

            }
        });
        activityBinding.btnMaskWarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    weight = 0.85;
                    overlayColor = converterToMat.convert(converterToMat.convert(Utils.loadResource(mActivity, R.drawable.bluegreen)));
                    Imgproc.cvtColor(converterToCvMat.convert(converterToCvMat.convert(overlayColor)), converterToCvMat.convert(converterToCvMat.convert(overlayColor)), Imgproc.COLOR_RGB2GRAY, Imgcodecs.IMREAD_GRAYSCALE);

                }catch (Exception e) {
                    e.printStackTrace();
                }

                currentPreviewOptions = PreviewOptions.BLUE;
            }
        });
        activityBinding.btnMaskBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    weight = 0.85;
                    overlayColor = converterToMat.convert(converterToMat.convert(Utils.loadResource(mActivity, R.drawable.yellow)));
                    Imgproc.cvtColor(converterToCvMat.convert(converterToCvMat.convert(overlayColor)), converterToCvMat.convert(converterToCvMat.convert(overlayColor)), Imgproc.COLOR_RGB2GRAY, Imgcodecs.IMREAD_GRAYSCALE);

                }catch (Exception e) {
                    e.printStackTrace();
                }

                currentPreviewOptions = PreviewOptions.BLUE;
            }
        });
        activityBinding.btnMaskRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    weight = 0.9;
                    overlayColor = converterToMat.convert(converterToMat.convert(Utils.loadResource(mActivity, R.drawable.blue)));
                    Imgproc.cvtColor(converterToCvMat.convert(converterToCvMat.convert(overlayColor)), converterToCvMat.convert(converterToCvMat.convert(overlayColor)), Imgproc.COLOR_BGR2RGB, Imgcodecs.IMREAD_UNCHANGED);

                }catch (Exception e) {
                    e.printStackTrace();
                }

                currentPreviewOptions = PreviewOptions.RED;
            }
        });
        activityBinding.btnMaskCartoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPreviewOptions = PreviewOptions.CARTOON;
            }
        });
        activityBinding.btnMaskStrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPreviewOptions = PreviewOptions.STRONG;
            }
        });
        activityBinding.cameraView.setCvCameraViewListener(this);
    }

    private void initRecorder(int width, int height) {
        int degree = getRotationDegree();
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(activityBinding.cameraView.getCameraId(), info);
        boolean isFrontFaceCamera = info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
        Log.i(LOG_TAG, "init recorder with width = " + width + " and height = " + height + " and degree = "
                + degree + " and isFrontFaceCamera = " + isFrontFaceCamera);
        int frameWidth, frameHeight;
        /*
         0 = 90CounterCLockwise and Vertical Flip (default)
         1 = 90Clockwise
         2 = 90CounterClockwise
         3 = 90Clockwise and Vertical Flip
         */
        switch (degree) {
            case 0:
                frameWidth = width;
                frameHeight = height;
                break;
            case 90:
                frameWidth = height;
                frameHeight = width;
                break;
            case 180:
                frameWidth = width;
                frameHeight = height;
                break;
            case 270:
                frameWidth = height;
                frameHeight = width;
                break;
            default:
                frameWidth = width;
                frameHeight = height;
        }

        Log.i(LOG_TAG, "saved file path: " + savePath.getAbsolutePath());
        recorder = new FFmpegFrameRecorder(savePath, frameWidth, frameHeight, 0);
        recorder.setFormat("mp4");
        recorder.setVideoCodec(AV_CODEC_ID_MPEG4);
        recorder.setVideoQuality(1);
        // Set in the surface changed method
        recorder.setFrameRate(60);

        Log.i(LOG_TAG, "recorder initialize success");
    }

    private int getRotationDegree() {
        int result;

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        if (Build.VERSION.SDK_INT >= 9) {
            // on >= API 9 we can proceed with the CameraInfo method
            // and also we have to keep in mind that the camera could be the front one
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(activityBinding.cameraView.getCameraId(), info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {
                // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
        } else {
            // TODO: on the majority of API 8 devices, this trick works good
            // and doesn't produce an upside-down preview.
            // ... but there is a small amount of devices that don't like it!
            result = Math.abs(degrees - 90);
        }
        return result;
    }

    public void startRecording() {
        try {
            synchronized (semaphore) {
                recorder.start();
            }
            startTime = System.currentTimeMillis();
            isRecording = true;
        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (recorder != null && isRecording) {
            isRecording = false;
            Log.v(LOG_TAG, "Finishing recording, calling stop and release on recorder");
            try {
                recorder.stop();
                synchronized (semaphore) {
                    recorder.release();
                }
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            recorder = null;
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(savePath));
            sendBroadcast(intent);
        }
    }

//    @Override
//    public void onClick(View v) {
//        if (!isRecording) {
//            startRecording();
//            isRecording = true;
//            Log.w(LOG_TAG, "Start Button Pushed");
//            btnRecorderControl.setText("Stop");
//            btnRecorderControl.setBackgroundResource(R.drawable.bg_red_circle_button);
//        } else {
//            // This will trigger the audio recording loop to stop and then set isRecorderStart = false;
//            stopRecording();
//            isRecording = false;
//            Log.w(LOG_TAG, "Stop Button Pushed");
////            btnRecorderControl.setText("Start");
//            btnRecorderControl.setVisibility(View.GONE);
//            Toast.makeText(this, "Video file was saved to \"" + savePath + "\"", Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        absoluteFaceSize = (int) (width * 0.32f);
        initRecorder(width, height);
        try {
//            dogImage = converterToMat.convert(converterToMat.convert(Utils.loadResource(mActivity, R.drawable.mask_dog)));
//            Imgproc.cvtColor(converterToCvMat.convert(converterToCvMat.convert(dogImage)), converterToCvMat.convert(converterToCvMat.convert(dogImage)), Imgproc.COLOR_RGB2BGRA, Imgcodecs.IMREAD_UNCHANGED);
//            maskImage = converterToMat.convert(converterToMat.convert(Utils.loadResource(mActivity, R.drawable.mask2)));
//            Imgproc.cvtColor(converterToCvMat.convert(converterToCvMat.convert(maskImage)), converterToCvMat.convert(converterToCvMat.convert(maskImage)), Imgproc.COLOR_RGB2BGRA, Imgcodecs.IMREAD_UNCHANGED);
            overlayColor = converterToMat.convert(converterToMat.convert(Utils.loadResource(mActivity, R.drawable.bluegreen)));
            Size size = new Size();
            size.width(width);
            size.height(height);
            resize(overlayColor, overlayColor, size, 0, 0, INTER_CUBIC);
            Imgproc.cvtColor(converterToCvMat.convert(converterToCvMat.convert(overlayColor)), converterToCvMat.convert(converterToCvMat.convert(overlayColor)), Imgproc.COLOR_RGB2GRAY, Imgcodecs.IMREAD_GRAYSCALE);
//            cvtColor(overlayColor, overlayColor, COLOR_BGR2RGB);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraViewStopped() {
        stopRecording();
    }

    @Override
    public Mat onCameraFrame(Mat mat) {
        switch (currentPreviewOptions) {
            case DEFAULT:
                break;
            case DOG:
//                if (faceDetector != null) {
//                    Mat grayMat = new Mat(mat.rows(), mat.cols());
//
//                    cvtColor(mat, grayMat, CV_BGR2GRAY);
//
//                    RectVector faces = new RectVector();
//                    faceDetector.detectMultiScale(grayMat, faces, 1.25f, 3, 1,
//                            new Size(absoluteFaceSize, absoluteFaceSize),
//                            new Size(4 * absoluteFaceSize, 4 * absoluteFaceSize));
//                    if (faces.size() >= 1) {
//                        int x = faces.get(0).x();
//                        int y = faces.get(0).y();
//                        int w = faces.get(0).width();
//                        int h = faces.get(0).height();
//                        Rect faceI = faces.get(0);
////                        rectangle(mat, faceI, new Scalar(0, 255, 0, 1));
//
//                        faceSize[0] = (faceSize[0] + w) / 2;
//                        faceSize[1] = (faceSize[1] + h) / 2;
//
//                        if(Math.abs(faceI.size().width() - dogImage.size().width()) > 30) {
//                            Size size = new Size();
//                            size.width(faceI.size().width() + 30);
//                            size.height((int)Math.ceil((float)faceI.size().width() / (float)dogImage.size().width() * dogImage.size().height()) + 30);
//                            resize(dogImage, dogImage, size, 0, 0, INTER_CUBIC);
//                        }
//                        overlayImage(mat.address(), dogImage.address(), mat.address(), x - 15 , y - 70 , 0 ,0);
//                    }
//
//                    grayMat.release();
//                }
            case MASK:
                if (faceDetector != null) {
                    Mat grayMat = new Mat(mat.rows(), mat.cols());

                    cvtColor(mat, grayMat, CV_BGR2GRAY);

                    RectVector faces = new RectVector();
                    faceDetector.detectMultiScale(grayMat, faces, 1.01f, 3, 1,
                            new Size(absoluteFaceSize, absoluteFaceSize),
                            new Size(4 * absoluteFaceSize, 4 * absoluteFaceSize));
                    if (faces.size() >= 1) {
                        int x = faces.get(0).x();
                        int y = faces.get(0).y();
                        int w = faces.get(0).width();
                        int h = faces.get(0).height();
                        Rect faceI = faces.get(0);
//                        rectangle(mat, faceI, new Scalar(0, 255, 0, 1));

                        faceSize[0] = (faceSize[0] + w) / 2;
                        faceSize[1] = (faceSize[1] + h) / 2;

                        if(currentPreviewOptions == PreviewOptions.DOG) {
                            int width = faceI.size().width() + 30;
                            int height = (int)Math.floor(((float)width / (float)maskImage.size().width()) * maskImage.size().height());
                            Log.e("dog", String.valueOf(((float)width / (float)maskImage.size().width())));
                            if(Math.abs(width - maskImage.size().width()) > 10) {
                                Size size = new Size();
                                size.width(width);
                                size.height(height);
                                if(width > maskImage.size().width())
                                    resize(maskImage, maskImage, size, 0, 0, INTER_CUBIC);
                                else
                                    resize(maskImage, maskImage, size, 0, 0, INTER_CUBIC);
                            }
                            overlayImage(mat.address(), maskImage.address(), mat.address(),  x - 15, y - 70, 0, 0);
                        } else if(currentPreviewOptions == PreviewOptions.MASK) {
                            int width = faceI.size().width();
                            int height = (int)Math.floor(((float)width / (float)maskImage.size().width()) * maskImage.size().height());
                            if(Math.abs(width - maskImage.size().width()) > 10) {
                                Size size = new Size();
                                size.width(width);
                                size.height(height);
                                if(width > maskImage.size().width())
                                    resize(maskImage, maskImage, size, 0, 0, INTER_CUBIC);
                                else
                                    resize(maskImage, maskImage, size, 0, 0, INTER_CUBIC);
                            }
                            overlayImage(mat.address(), maskImage.address(), mat.address(), x, y - 30, 0, 0);
                        }
                    }

                    grayMat.release();
                }
                break;
            case GRAY:
                cvtColor(mat, mat, COLOR_RGBA2GRAY);
//                ConvertRGBtoGray(mat.address(), mat.address());
                break;
            case RED:
                try {
//                    applyColorMap(overlayColor, overlayColor, COLORMAP_HOT);
//                ConvertRGBtoRed(mat.address(), mat.address());
                    addWeighted(overlayColor, 1 - weight, mat, weight, 0, mat);
                } catch (Exception e) {
                    Size size = new Size();
                    size.width(mat.size().width());
                    size.height(mat.size().height());
                    resize(overlayColor, overlayColor, size, 0, 0, INTER_CUBIC);
                    e.printStackTrace();
                }
                break;
            case BLUE:
                try {
//                    applyColorMap(overlayColor, overlayColor, COLORMAP_COOL);
                    addWeighted(overlayColor, 1 - weight, mat, weight, 0, mat);
                } catch (Exception e) {
                    Size size = new Size();
                    size.width(mat.size().width());
                    size.height(mat.size().height());
                    resize(overlayColor, overlayColor, size, 0, 0, INTER_CUBIC);
                    e.printStackTrace();
                }
                break;
            case OLD:
//                applyColorMap(overlayColor, overlayColor, COLORMAP_SUMMER);
//                cvtColor(mat, mat, COLORMAP_SPRING);
//                ConvertRGBtoOld(mat.address(), mat.address());
                try {
//
//                    Log.e("", "overlayColor: width: " + overlayColor.size().width() + ", width: " + overlayColor.size().width());
//                    Log.e("", "mat         : width: " + mat.size().width() + ", width: " + mat.size().width());
//                    cvtColor(overlayColor, overlayColor, COLOR_RGB2GRAY);
//                    applyColorMap(overlayColor, overlayColor, COLORMAP_SUMMER);
//                    cvtColor(overlayColor, overlayColor, COLOR_BGR2RGB);
                    addWeighted(overlayColor, 1 - weight, mat, weight, 0, mat);
                } catch (Exception e) {
//            Log.e("eRROR", e.getMessage());
                    Size size = new Size();
                    size.width(mat.size().width());
                    size.height(mat.size().height());
                    resize(overlayColor, overlayColor, size, 0, 0, INTER_CUBIC);
                    e.printStackTrace();
                }

//                try {
//                    Size size = new Size();
//                    size.width(mat.size().width());
//                    size.height(mat.size().height());
//                    resize(overlayColor, overlayColor, size, 0, 0, INTER_CUBIC);
//                    addWeighted(overlayColor, 1 - weight, mat, weight, 0, mat);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;
            case CARTOON:
//                ConvertRGBtoCartoon(mat.address(), mat.address());
                break;
            case STRONG:
//                ConvertRGBtoStrong(mat.address(), mat.address());
                break;
        }


        if (isRecording && mat != null) {
            synchronized (semaphore) {
                try {
                    Frame frame = converterToMat.convert(mat);
                    long t = 1000 * (System.currentTimeMillis() - startTime);
                    if (t > recorder.getTimestamp()) {
                        recorder.setTimestamp(t);
                    }
                    recorder.record(frame);
                } catch (FFmpegFrameRecorder.Exception e) {
                    Log.v(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        }




//        try {
//            Size size = new Size();
//            size.width(mat.size().width());
//            size.height(mat.size().height());
//            resize(overlayColor, overlayColor, size, 0, 0, INTER_CUBIC);
//
//
////            Mat test = mat.clone();
////            test.convertTo(test, CV_8UC3);
////            cvtColor(test, test, CV_BGR2RGB);
////            applyColorMap(test, test, COLORMAP_PARULA);
////
////            mat.convertTo(mat, CV_8UC3);
////            cvtColor(mat, mat, CV_BGR2RGB);
////
//            addWeighted(overlayColor, 1 - weight, mat, weight, 0, mat);
//
//
//            return mat;
//        } catch (Exception e) {
////            Log.e("eRROR", e.getMessage());
//            e.printStackTrace();
//        }




//            Size size = new Size();
//            size.width(mat.size().width());
//            size.height(mat.size().height());
//            resize(red, red, size, 0, 0, INTER_CUBIC);


//            Mat test = mat.clone();
//            test.convertTo(test, CV_8UC3);
//            cvtColor(test, test, CV_BGR2RGB);
//            applyColorMap(test, test, COLORMAP_PARULA);
//
//            mat.convertTo(mat, CV_8UC3);
//            cvtColor(mat, mat, CV_BGR2RGB);
//

        return mat;
    }
}

