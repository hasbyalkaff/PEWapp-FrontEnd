package unsri.final_project.pewapp;

import android.content.Context;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Hasby on 10-Mar-19.
 */

public class Haarcascade {
    private String TAG = "Harrcascade Class";
    private CascadeClassifier cascadeClassifier;

    Haarcascade(Context context){
        try{
            InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while((bytesRead = is.read(buffer)) != -1){
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            this.cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            this.cascadeClassifier.load(mCascadeFile.getAbsolutePath());
            cascadeDir.delete();
        } catch (Exception e){
            Log.e(TAG, "Error loading cascade");
        }
    }

    public MatOfRect detectFace(Mat image, int absoluteFaceSize){
        MatOfRect faces = new MatOfRect();
        this.cascadeClassifier.detectMultiScale(image, faces, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        return faces;
    }
}
