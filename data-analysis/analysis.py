import numpy as np

def detect_movements(sensor_data):
    for record in sensor_data:
        movement = ""

        # Beräkna magnitud för accelerometer och gyroskop
        accel_magnitude = np.sqrt(record["accelX"]**2 + record["accelY"]**2 + record["accelZ"]**2)
        gyro_magnitude = np.sqrt(record["gyroX"]**2 + record["gyroY"]**2 + record["gyroZ"]**2)

        if accel_magnitude > 5.0:
            movement = "Strong Movement"
        elif gyro_magnitude > 3.0:
            movement = "Fast Rotation"
        elif abs(record["accelX"]) > 2.0:
            movement = "Horizontal Shake"
        elif abs(record["accelY"]) > 2.0:
            movement = "Vertical Shake"
        elif abs(record["accelZ"]) > 2.0:
            movement = "Sudden Stop"

        record["movement_detected"] = movement if movement else "No significant movement"

    return sensor_data
