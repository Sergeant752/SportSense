import matplotlib.pyplot as plt
import os

def generate_graphs(sensor_data):
    timestamps = [record["timestamp"] for record in sensor_data]
    heart_rates = [record["heartRate"] for record in sensor_data]
    accel_x = [record["accelX"] for record in sensor_data]
    accel_y = [record["accelY"] for record in sensor_data]
    accel_z = [record["accelZ"] for record in sensor_data]

    os.makedirs("output", exist_ok=True)

    # Heart Rate Graph
    plt.figure(figsize=(10, 6))
    plt.plot(timestamps, heart_rates, label="Heart Rate (BPM)", color="red")
    plt.xlabel("Timestamp")
    plt.ylabel("Heart Rate")
    plt.title("Heart Rate Over Time")
    plt.legend()
    plt.savefig("output/heart_rate_graph.png")

    # Accelerometer Data Graph
    plt.figure(figsize=(10, 6))
    plt.plot(timestamps, accel_x, label="Accel X", color="blue")
    plt.plot(timestamps, accel_y, label="Accel Y", color="green")
    plt.plot(timestamps, accel_z, label="Accel Z", color="purple")
    plt.xlabel("Timestamp")
    plt.ylabel("Acceleration")
    plt.title("Accelerometer Data Over Time")
    plt.legend()
    plt.savefig("output/acceleration_graph.png")

    # Histogram for Heart Rate Distribution
    plt.figure(figsize=(10, 6))
    plt.hist(heart_rates, bins=20, color="skyblue", edgecolor="black")
    plt.title("Heart Rate Distribution")
    plt.xlabel("Heart Rate (BPM)")
    plt.ylabel("Frequency")
    plt.savefig("output/heart_rate_histogram.png")
