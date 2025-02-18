import sqlite3
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
import joblib

def train_model():
    conn = sqlite3.connect("sensor_data.db")
    df = pd.read_sql_query("SELECT * FROM sensor_data", conn)
    conn.close()

    X = df[['accelX', 'accelY', 'accelZ', 'gyroX', 'gyroY', 'gyroZ']]
    y = df['heartRate']  # Placeholder, kan vara en annan variabel

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    model = RandomForestClassifier(n_estimators=100)
    model.fit(X_train, y_train)

    joblib.dump(model, 'output/model.pkl')
    print("Model trained and saved!")

train_model()
