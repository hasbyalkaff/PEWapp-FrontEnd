package unsri.final_project.pewapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Hasby on 10-Mar-19.
 */

public class CameraHandler implements CameraBridgeViewBase.CvCameraViewListener2 {
    private String TAG = "Camera Handler Class";
    private Context context;

    private ImageView mNetralImageView, mProcessedImageView, mExpressionImageView;
    private TextView mExpressionText;
    private CameraBridgeViewBase mCameraView;
    private Haarcascade mDetection;

    private Mat grayscaleImage, grayTamp, grayTrans;
    private boolean isSetNetral, isCheckExpression;
    private int absoluteFaceSize;
    private FaceRegion mFaceRegion;

    private Requester mRequester;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(context) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    initializeDepedencies();
                } break;
                default: {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraHandler(Context context){
        this.context = context;
        this.mRequester = new Requester(context);
        this.isSetNetral = this.isCheckExpression = false;
    }

    public void startCamera(){
        if(OpenCVLoader.initDebug()) {
            Log.d(TAG, "Lib: Using OpenCV Manager");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, mLoaderCallback);
        } else {
            Log.d(TAG, "Lib: Using Internal OpenCV Library");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void startAsync(){}

    public void disableCamera(){
        if(mCameraView != null) mCameraView.disableView();
    }

    public void setImageView(int netralImage, int processedImage, int expressionImage){
        this.mNetralImageView = ((Activity)context).findViewById(netralImage);
        this.mProcessedImageView = ((Activity)context).findViewById(processedImage);
        this.mExpressionImageView = ((Activity)context).findViewById(expressionImage);
    }
    public void setTextView(int textView){
        this.mExpressionText = ((Activity)context).findViewById(textView);
    }
    public void setCamera(CameraBridgeViewBase mCamera){
        mCamera.setVisibility(SurfaceView.VISIBLE);
        mCamera.setCvCameraViewListener(this);
        this.mCameraView = mCamera;
    }

    private void initializeDepedencies(){
        mDetection = new Haarcascade(this.context);
        mCameraView.enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        grayTamp = new Mat(width, height, CvType.CV_8UC4);
        grayTrans = new Mat(width, height, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


        Imgproc.cvtColor(inputFrame.rgba(), grayscaleImage, Imgproc.COLOR_RGBA2GRAY);

        Core.transpose(grayscaleImage, grayTrans);
        Core.flip(grayTrans, grayTamp, -1);

        MatOfRect faces = new MatOfRect();
        if(mDetection != null)
            faces = mDetection.detectFace(grayTamp, absoluteFaceSize);

        Rect[] facesArray = faces.toArray();
        if(facesArray.length>0) {
            Log.i(TAG, "face was found");
            for (int i = 0; i < facesArray.length; i++) {
                double custom_height = facesArray[i].br().y + 100;
                if(custom_height <= 800) {
                    Imgproc.rectangle(grayTamp, facesArray[i].tl(), new Point(facesArray[i].br().x, custom_height), new Scalar(0, 255, 255), 3);
                    Rect rectCropFace = new Rect(facesArray[i].tl(), new Point(facesArray[i].br().x, custom_height));
                    Mat faceImage = new Mat(grayTamp, rectCropFace);

                    if(this.isCheckExpression){
                        mFaceRegion = new FaceRegion(faceImage);
                        mRequester.jsonPOST(mFaceRegion.get_bitmap_face(), mProcessedImageView, mExpressionImageView, mExpressionText);
                    }
                    else if(this.isSetNetral) {
                        this.isSetNetral = false;
                        mFaceRegion = new FaceRegion(faceImage);
                        mRequester.jsonPOST(mFaceRegion.get_bitmap_face(), mNetralImageView, null, null);
                        this.isCheckExpression = true;
                    }
                }
            }
        }
        else {
            Log.i(TAG, "face not found");
        }

        Imgproc.resize(grayTamp, grayscaleImage, grayscaleImage.size());
        return grayscaleImage;
    }

    public void setNetral(){
        this.isSetNetral = true;
    }
}
