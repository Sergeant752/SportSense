import sqlite3
import numpy as np
import pandas as pd
import tensorflow as tf
import joblib
from tensorflow import keras
from tensorflow.keras import layers
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
import os

# 📌 Hämta sensor-data och skapa timeserier med fönsterstorlek 600
def prepare_timeseries_data(db_path="sensor_data.db", window_size=600):
    conn = sqlite3.connect(db_path)
    df = pd.read_sql_query("SELECT * FROM sensor_data ORDER BY timestamp ASC", conn)
    conn.close()

    features = ["accelX", "accelY", "accelZ", "gyroX", "gyroY", "gyroZ", "heartRate"]
    sequences, labels = [], []

    for i in range(len(df) - window_size):
        seq = df.iloc[i:i+window_size][features].values
        label = df.iloc[i+window_size]["heartRate"]  # Detta kan bytas mot en mer relevant label
        sequences.append(seq)
        labels.append(label)

    return np.array(sequences), np.array(labels)

# 📌 Bygg och träna en LSTM-modell för rörelsedetektering
def train_movement_model():
    print("🚀 Tränar LSTM-modellen...")
    X, y = prepare_timeseries_data()

    # 🔹 Konvertera etiketter till 0-n klasser
    label_encoder = LabelEncoder()
    y = label_encoder.fit_transform(y)

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    model = keras.Sequential([
        layers.Input(shape=(X_train.shape[1], X_train.shape[2])),  # 🔹 Korrekt Input Layer
        layers.LSTM(64, return_sequences=True),
        layers.LSTM(32),
        layers.Dense(len(label_encoder.classes_), activation="softmax")  # 🔹 Dynamiskt antal klasser
    ])
    model.compile(optimizer="adam", loss="sparse_categorical_crossentropy", metrics=["accuracy"])
    model.fit(X_train, y_train, epochs=10, validation_data=(X_test, y_test))

    # 🔹 Spara modellen i Keras-format
    model.save("output/movement_model.keras")
    joblib.dump(label_encoder, "output/label_encoder.pkl")

    print("✅ LSTM-modell tränad och sparad!")

# 📌 Konvertera modellen till TensorFlow Lite för Android
def convert_model_to_tflite():
    print("🔄 Konverterar modellen till TFLite...")

    try:
        # 🔹 Ladda modellen från Keras-format
        model = keras.models.load_model("output/movement_model.keras")

        # 🔹 Konvertera modellen till TFLite direkt från Keras-modellen
        converter = tf.lite.TFLiteConverter.from_keras_model(model)
        converter.target_spec.supported_ops = [
            tf.lite.OpsSet.TFLITE_BUILTINS, 
            tf.lite.OpsSet.SELECT_TF_OPS  # 🔹 Tillåter komplexa TF-operationer i TFLite
        ]
        converter.experimental_enable_resource_variables = True  # 🔹 Aktivera resurser i TFLite-konverteringen
        converter.experimental_new_converter = True  # 🔹 Använd den senaste TFLite-konverteraren

        tflite_model = converter.convert()

        # 🔹 Spara TFLite-modellen
        with open("output/movement_model.tflite", "wb") as f:
            f.write(tflite_model)

        print("✅ Model converted to TensorFlow Lite!")

    except Exception as e:
        print(f"❌ TFLite-konvertering misslyckades: {e}")

if __name__ == "__main__":
    # 🔹 Skapa output-mapp om den inte finns
    os.makedirs("output", exist_ok=True)

    print("🚀 Startar ML-pipeline...")
    
    # 📌 Träna modellen
    train_movement_model()

    # 📌 Konvertera modellen till TFLite
    convert_model_to_tflite()

    print("🎯 Klar! Modellen är sparad som 'movement_model.tflite'.")