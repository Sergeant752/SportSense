import sqlite3
import pandas as pd

# Anslut till databasen
conn = sqlite3.connect("sensor_data.db")

# Hämta de senaste 10 raderna
df = pd.read_sql_query("SELECT * FROM sensor_data ORDER BY timestamp DESC LIMIT 10;", conn)

# Stäng anslutningen
conn.close()

# Visa innehållet i terminalen
print("\nSensor Data (senaste 10 raderna):\n")
print(df)
