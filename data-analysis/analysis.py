def detect_movements(sensor_data):
    for record in sensor_data:
        movement = ""
        if abs(record["accelX"]) > 2.0:
            movement = "Horizontal Shake"
        elif abs(record["accelY"]) > 2.0:
            movement = "Vertical Shake"
        elif abs(record["accelZ"]) > 2.0:
            movement = "Sudden Stop"
        record["movement_detected"] = movement if movement else "No significant movement"
    return sensor_data
