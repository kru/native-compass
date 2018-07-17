package id.ibadah.nativecompass;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private QiblaTest qiblaTest;
    private ImageView compass;
    private ImageView needle;
    private TextView heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compass = findViewById(R.id.imageCompass);
        needle = findViewById(R.id.needle);
        heading = findViewById(R.id.heading);

        // QiblaTest(Context context, ImageView compass, ImageView needle, TextView heading, double longi, double lati, double alti)

        QiblaTest qiblaTest = new QiblaTest(this, compass, needle, heading, 40, 30, 30);
    }
}
