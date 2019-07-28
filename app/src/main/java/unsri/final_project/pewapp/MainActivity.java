package unsri.final_project.pewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Main Activity Class";
    private ImageView imageView;
    private TextView expressionText;
    private CameraHandler cameraHandler;
    private CameraBridgeViewBase cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cameraView = findViewById(R.id.camera_view);
        imageView = findViewById(R.id.netralImageView);
        expressionText = findViewById(R.id.expressionText);

        cameraHandler = new CameraHandler(this);
        cameraHandler.setCamera(cameraView);
        cameraHandler.setImageView(R.id.netralImageView, R.id.processedImageView, R.id.expressionImageView);
        cameraHandler.setTextView(R.id.expressionText);
    }

    @Override
    public void onPause(){
        super.onPause();
        cameraHandler.disableCamera();
    }

    @Override
    public void onResume(){
        super.onResume();
        cameraHandler.startCamera();
    }

    public void onDestroy(){
        super.onDestroy();
        cameraHandler.disableCamera();
    }

    public void set_netral(View view){
        cameraHandler.setNetral();
    }
}
