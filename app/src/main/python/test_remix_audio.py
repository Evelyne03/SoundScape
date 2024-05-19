import requests
import numpy as np
import soundfile as sf
import os

# Generate a test audio file (3 seconds: 1 second of 440 Hz, 1 second of 880 Hz, 1 second of 660 Hz)
sample_rate = 44100
t1 = np.linspace(0, 1, int(sample_rate * 1.0), endpoint=False)
t2 = np.linspace(1, 2, int(sample_rate * 1.0), endpoint=False)
t3 = np.linspace(2, 3, int(sample_rate * 1.0), endpoint=False)
x1 = 0.5 * np.sin(2 * np.pi * 440 * t1)  # 440 Hz sine wave
x2 = 0.5 * np.sin(2 * np.pi * 880 * t2)  # 880 Hz sine wave
x3 = 0.5 * np.sin(2 * np.pi * 660 * t3)  # 660 Hz sine wave
x = np.concatenate([x1, x2, x3])
test_file_path = 'generated_test_audio.wav'
sf.write(test_file_path, x, sample_rate)

# Define the URL and the intervals
url = 'http://192.168.19.1:5000/remix_audio'
# Intervals that should clearly alter the audio
# For example, [(0, 44100), (88200, 132300)] means keep the first second, skip the second second, and keep the third second
intervals = '[(0, 44100), (88200, 132300)]'

try:
    # Perform the request
    with open(test_file_path, 'rb') as f:
        files = {'file': f}
        data = {
            'intervals': intervals,
            'align_zeros': 'true'  # or 'false' based on your requirement
        }
        response = requests.post(url, files=files, data=data)

    # Handle the response
    if response.status_code == 200:
        with open('remixed_output.wav', 'wb') as f:
            f.write(response.content)
        print('Remix successful, output saved as remixed_output.wav')
    else:
        print('Failed to remix audio:', response.json())

except Exception as e:
    print(f"An error occurred: {e}")

finally:
    # Clean up the generated test file
    if os.path.exists(test_file_path):
        os.remove(test_file_path)

print("Test script completed.")
