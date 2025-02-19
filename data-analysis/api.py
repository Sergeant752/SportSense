from flask import Flask, request, jsonify, send_file
import json
import sqlite3
import os
import analysis
import data_loader
import joblib

app = Flask(__name__)

# 🔹 Skapa en databasanslutning
def get_db_connection():
    conn = sqlite3.connect("sensor_data.db")
    conn.row_factory = sqlite3.Row
    return conn

# 🔹 Skapa tabell om den inte finns
def create_table():
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS sensor_data (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        timestamp INTEGER,
                        accelX REAL, accelY REAL, accelZ REAL,
                        gyroX REAL, gyroY REAL, gyroZ REAL,
                        heartRate INTEGER)''')
    conn.commit()
    conn.close()


# 🔹 Endpoint för att ta emot data från Android
@app.route('/analyze', methods=['POST'])
def analyze_data():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"status": "error", "message": "No data received"}), 400

        print("📡 Received Data:", json.dumps(data, indent=4))  # 🔥 Logga inkommande data

        # 🔹 Spara data i SQLite
        conn = get_db_connection()
        cursor = conn.cursor()
        for entry in data:
            cursor.execute('''INSERT INTO sensor_data (timestamp, accelX, accelY, accelZ, gyroX, gyroY, gyroZ, heartRate)
                              VALUES (?, ?, ?, ?, ?, ?, ?, ?)''',
                           (entry['timestamp'], entry['accelX'], entry['accelY'], entry['accelZ'],
                            entry['gyroX'], entry['gyroY'], entry['gyroZ'], entry['heartRate']))
        conn.commit()
        conn.close()

        # 🔹 Utför analys
        analyzed_data = analysis.detect_movements(data)
        data_loader.save_json(analyzed_data, "output/analyzed_data.json")

        return jsonify(analyzed_data), 200
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500


# 🔹 Endpoint för att hämta analyserad data
@app.route('/get-analysis', methods=['GET'])
def get_analysis():
    try:
        conn = get_db_connection()
        df = conn.execute("SELECT * FROM sensor_data ORDER BY timestamp DESC LIMIT 10").fetchall()
        conn.close()

        # Skapa en lista med analyserad data
        analyzed_data = []
        for row in df:
            record = dict(row)
            # Lägg till rörelseanalys
            record["movement_detected"] = analysis.detect_movements([record])[0]["movement_detected"]
            analyzed_data.append(record)

        return jsonify(analyzed_data), 200
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500

# 🔹 Endpoint för att hämta den tränade ML-modellen
@app.route('/get-model', methods=['GET'])
def get_model():
    try:
        return send_file("output/model.pkl", as_attachment=True)
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    create_table()
    app.run(host='0.0.0.0', port=5000, ssl_context=('cert.pem', 'key.pem'))
