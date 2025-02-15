import data_loader
import analysis
import visualization
import requests
import json

def fetch_data_from_android():
    response = requests.post("http://127.0.0.1:5000/export-data")
    if response.status_code == 200:
        data = response.json()
        print("Data fetched successfully from Android.")
        return data
    else:
        print(f"Failed to fetch data: {response.status_code} - {response.text}")
        return None

if __name__ == "__main__":
    # Hämta JSON-data från Android-servern
    sensor_data = fetch_data_from_android()
    if sensor_data:
        # Utför rörelsedetektering och analys
        analyzed_data = analysis.detect_movements(sensor_data)

        # Generera grafer och spara resultat
        visualization.generate_graphs(analyzed_data)

        # Spara analyserat resultat tillbaka till JSON
        data_loader.save_json(analyzed_data, "output/analyzed_data.json")

        print("Analysis complete. Results saved to 'output/analyzed_data.json'.")
    else:
        print("No data to analyze.")
