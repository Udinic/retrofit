package android.hardware;

/**
 * copy of Android's SensorEvent with a public constructor
 */
public class SensorEvent {
  public final float[] values;

  public Sensor sensor;
  public int accuracy;

  public long timestamp;

  public SensorEvent(int size) {
    values = new float[size];
  }
}
