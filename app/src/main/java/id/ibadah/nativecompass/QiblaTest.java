package id.ibadah.nativecompass;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by krisrp on 17/07/18.
 */

public class QiblaTest extends Service implements SensorEventListener {
    public static ImageView image, arrow;
    public static TextView tvHeading;

    public static String TAG = "QiblaTest";

    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    // record the compass picture angle turned
    private float currentDegree = 0f;
    private float currentDegreeNeedle = 0f;
    Context context;
    Location userLoc = new Location("service Provider");
    // device sensor manager
    private static SensorManager mSensorManager ;
    private Sensor sensor;

    public QiblaTest(Context context, ImageView compass, ImageView needle, TextView heading, double lng, double lat, double alt) {
        image = compass;
        arrow = needle;


        // TextView that will tell the user what degree is he heading
        tvHeading = heading;
        userLoc.setLongitude(lng);
        userLoc.setLatitude(lat);
        userLoc.setAltitude(alt);

        mSensorManager =  (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(sensor!=null) {
            // for the system's orientation sensor registered listeners
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);//SensorManager.SENSOR_DELAY_Fastest
        } else {
            Log.i(TAG, "Something went wrong!");
        }
        // initialize your android device sensor capabilities
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("HERE", Double.toString(userLoc.getLatitude()));

        float degree = Math.round(sensorEvent.values[0]);

        // The angle that you've rotated your phone from true north.
        float head = Math.round(sensorEvent.values[0]);

        Location destinationLoc = new Location("service Provider");

        destinationLoc.setLatitude(21.422487); //kaaba latitude setting
        destinationLoc.setLongitude(39.826206); //kaaba longitude setting

        // The angle from true north to the destination location from the point we're your currently standing.
        float bearTo = userLoc.bearingTo(destinationLoc);

        GeomagneticField geoField = new GeomagneticField( Double.valueOf( userLoc.getLatitude() ).floatValue(), Double
                .valueOf( userLoc.getLongitude() ).floatValue(),
                Double.valueOf( userLoc.getAltitude() ).floatValue(),
                System.currentTimeMillis() );
        head -= geoField.getDeclination(); // converts magnetic north into true north

        if (bearTo < 0) {
            bearTo = bearTo + 360;
            //bearTo = -100 + 360  = 260;
        }

        //This is where we choose to point it
        float direction = bearTo - head;

        // If the direction is smaller than 0, add 360 to get the rotation clockwise.
        if (direction < 0) {
            direction = direction + 360;
        }

        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees" );

        RotateAnimation raQibla = new RotateAnimation(currentDegreeNeedle, direction, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        raQibla.setDuration(210);
        raQibla.setFillAfter(true);

        arrow.startAnimation(raQibla);

        currentDegreeNeedle = direction;

        // create a rotation animation (reverse turn degree degrees)
//        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        // how long the animation will take place
//        ra.setDuration(210);


        // set the animation after the end of the reservation status
//        ra.setFillAfter(true);

        // Start the animation
//        image.startAnimation(ra);

//        currentDegree = -degree;
        if (sensorEvent.sensor == mAccelerometer) {
            System.arraycopy(sensorEvent.values, 0, mLastAccelerometer, 0, sensorEvent.values.length);
            mLastAccelerometerSet = true;
        } else if (sensorEvent.sensor == mMagnetometer) {
            System.arraycopy(sensorEvent.values, 0, mLastMagnetometer, 0, sensorEvent.values.length);
            mLastMagnetometerSet = true;
        }

        if (mLastAccelerometerSet && mLastMagnetometerSet) {


            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);


            ra.setDuration(250);

            ra.setFillAfter(true);

            image.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
