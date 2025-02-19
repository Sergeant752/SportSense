import numpy as np

def detect_movements(sensor_data):
    for record in sensor_data:
        movement = ""

        # Beräkna magnitud för accelerometer och gyroskop
        accel_magnitude = np.sqrt(record["accelX"]**2 + record["accelY"]**2 + record["accelZ"]**2)
        gyro_magnitude = np.sqrt(record["gyroX"]**2 + record["gyroY"]**2 + record["gyroZ"]**2)

        # Detektera kraftfull rörelse
        if accel_magnitude > 3000:
            movement = "Strong Movement"
        elif gyro_magnitude > 500:
            movement = "Fast Rotation"
        elif abs(record["accelX"]) > 1500:
            movement = "Horizontal Shake"
        elif abs(record["accelY"]) > 1500:
            movement = "Vertical Shake"
        elif abs(record["accelZ"]) > 1500:
            movement = "Sudden Stop"
        elif gyro_magnitude > 200:
            movement = "Mild Rotation"

        record["movement_detected"] = movement if movement else "No significant movement"
        print(f" Analyzing: {record} -> Movement: {record['movement_detected']}")

    return sensor_data
