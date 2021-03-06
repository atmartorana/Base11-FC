package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.events.FlightStateListener;
import org.rocketproplab.marginalstability.flightcomputer.events.PositionListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.Solenoid;
import org.rocketproplab.marginalstability.flightcomputer.math.InterpolatingVector3;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;
import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightMode;

/**
 * A subsystem that controls the solenoids for deploying the parachutes.
 * 
 * @author Max Apodaca
 *
 */
public class ParachuteSubsystem
    implements FlightStateListener, PositionListener, Subsystem {

  private Solenoid             mainChute;
  private Solenoid             drogueChute;
  private FlightMode           lastMode;
  private InterpolatingVector3 position;
  private Time                 time;

  /**
   * Create a new parachute subsystem
   * 
   * @param mainChute   the solenoid to deploy the main chute
   * @param drogueChute the solenoid to deploy the drogue chute
   * @param time        the rocket time
   */
  public ParachuteSubsystem(Solenoid mainChute, Solenoid drogueChute,
      Time time) {
    this.mainChute   = mainChute;
    this.drogueChute = drogueChute;
    this.lastMode    = FlightMode.Sitting;
    this.time        = time;
  }

  @Override
  public void onFlightModeChange(FlightMode newMode) {
    if (newMode == FlightMode.Apogee) {
      drogueChute.set(true);
    }
    if (newMode == FlightMode.Falling) {
      drogueChute.set(true);
    }
    this.lastMode = newMode;
  }

  @Override
  public void onPositionEstimate(InterpolatingVector3 positionEstimate) {
    this.position = positionEstimate;
  }

  @Override
  public void update() {
    if (this.position == null) {
      return;
    }
    if (this.lastMode != FlightMode.Falling) {
      return;
    }
    
    Vector3 currentPos = this.position.getAt(time.getSystemTime());
    if (currentPos.getZ() < Settings.MAIN_CHUTE_HEIGHT) {
      this.mainChute.set(true);
    }
  }

}
