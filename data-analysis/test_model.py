import tensorflow.lite as tflite
import numpy as np

# Ladda modellen
model_path = "output/movement_model.tflite"
interpreter = tflite.Interpreter(model_path=model_path)
interpreter.allocate_tensors()

# Hämta input- och output-detaljer
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# Skapa dummy-input baserat på modellens inputform
input_shape = input_details[0]['shape']
input_dtype = input_details[0]['dtype']
dummy_input = np.random.rand(*input_shape).astype(input_dtype)

# Kör inferens
interpreter.set_tensor(input_details[0]['index'], dummy_input)
interpreter.invoke()
output_data = interpreter.get_tensor(output_details[0]['index'])

print("Model Output:", output_data)
