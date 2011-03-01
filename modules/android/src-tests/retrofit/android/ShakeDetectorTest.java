// Copyright 2010 Square, Inc.
package retrofit.android;

import android.hardware.SensorEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;

/** @author Eric Burke (eric@squareup.com) */
public class ShakeDetectorTest extends TestCase {

  private AtomicBoolean heardShake;
  private ShakeDetector detector;
  private SensorEvent event;

  @Override protected void setUp() throws Exception {
    super.setUp();
    heardShake = new AtomicBoolean();
    detector = new ShakeDetector(new ShakeDetector.Listener() {
      @Override public void hearShake() {
        heardShake.set(true);
      }
    });
    event = new SensorEvent(3);
  }

  public void testBadAccelerometerAtRest() {

    for (int i = 0; i < 200; i++) {
      nextEvent(detector, event, 10.11f, 0.08f, 18.34f, i*10);
    }

    assertFalse("should not have heard shake", heardShake.get());
  }

  public void testBadAccelerometerShaking() {
    for (int i = 0; i < 200; i++) {
      nextEvent(detector, event, (0.11f + 9*(i%4)), 0.08f, 18.34f, 10);
    }

    assertTrue("should have heard shake", heardShake.get());
  }

  /** small queue size currently yields incorrect 75% calculation */
  public void testLowRefreshRateFalseShake_QueueSizeFour() {

    // This is an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, 100);

    // This is an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 12.8f, 100);

    // This is not an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 12.8f, 100);

    // This is not an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 12.8f, 100);

    assertFalse("should not have heard shake", heardShake.get());
  }

  /** small queue size currently yields incorrect 75% calculation */
  public void testLowRefreshRateFalseShake_QueueSizeFive() {

    // This is an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, 70);

    // This is an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 12.8f, 70);

    // This is an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 15.8f, 70);

    // This is not an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 15.8f, 70);

    // This is not an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 15.8f, 70);

    assertFalse("should not have heard shake", heardShake.get());
  }

  /**
   * Low sampling rate (.2 sec), lots of false shake positives with
   * old algorithm.
   */
  public void testShakingLgAlly(){
    int sampleRate = 200; // 5 per second

    // at rest
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    assertFalse("should not have heard shake", heardShake.get());

    // shake vigorously
    nextEvent(detector, event, 0.1f, 0.1f, 19.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 0.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 19.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 0.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 19.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 0.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 19.8f, sampleRate);
    assertTrue("should have heard shake", heardShake.get());
  }

  /**
   * Low sampling rate (.2 sec), lots of false shake positives with
   * old algorithm.
   */
  public void testShakingLgAlly_BackAndForthShake(){
    int sampleRate = 200; // 5 per second

    // at rest
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    assertFalse("should not have heard shake", heardShake.get());

    // shake vigorously (no change in magnitude, only direction)
    nextEvent(detector, event, 0.1f, 0.1f, -9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, -9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, -9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, -9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    assertTrue("should have heard shake", heardShake.get());
  }

  /**
   * Low sampling rate (.2 sec), lots of false shake positives with
   * old algorithm.
   */
  public void testShakingLgAlly_NoChangeInAccelerationMagnitude(){
    int sampleRate = 200; // 5 per second

    // at rest
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    assertFalse("should not have heard shake", heardShake.get());

    // shake vigorously (no change in magnitude, only direction)
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 9.8f, 0.1f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, -9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, -9.8f, 0.1f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, 9.8f, 0.1f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, -9.8f, sampleRate);
    nextEvent(detector, event, 0.1f, -9.8f, 0.1f, sampleRate);
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, sampleRate);
    assertTrue("should have heard shake", heardShake.get());
  }

  public void testAllAccelerationsHeardAsShake() {
    // This is an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 9.8f, 200);

    // This is an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 12.8f, 200);

    // This is an acceleration
    nextEvent(detector, event, 0.1f, 0.1f, 15.8f, 200);

    assertTrue("should have heard shake", heardShake.get());
  }

  private void nextEvent(ShakeDetector detector, SensorEvent event, float x, float y, float z, int deltaMillis) {
    event.values[0] = x;
    event.values[1] = y;
    event.values[2] = z;
    event.timestamp += deltaMillis * 1000000;
    detector.onSensorChanged(event);
  }

}
