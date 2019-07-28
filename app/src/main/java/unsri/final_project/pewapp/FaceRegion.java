package unsri.final_project.pewapp;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by Hasby on 10-Mar-19.
 */

public class FaceRegion {
    Mat face;

    public FaceRegion(Mat face){
        this.face = face;
    }

    public void set_face(Mat face){
        this.face = face;
    }

    public Bitmap get_bitmap_face(){
        Bitmap mBitmap = Bitmap.createBitmap(this.face.cols(), this.face.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(this.face, mBitmap);
        return mBitmap;
    }
}
