from flask import Flask, request, jsonify
import json
import os

app = Flask(__name__)

@app.route('/export-data', methods=['POST'])
def export_data():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"status": "error", "message": "No data received"}), 400

        # Spara datan som JSON
        file_path = "exported_data.json"
        with open(file_path, 'w') as f:
            json.dump(data, f, indent=4)

        return jsonify({"status": "success", "message": f"Data saved at {file_path}"})
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500

@app.route('/get-analysis', methods=['GET'])
def get_analysis():
    try:
        with open("output/analyzed_data.json", "r") as file:
            analyzed_data = json.load(file)
        return jsonify(analyzed_data), 200
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
