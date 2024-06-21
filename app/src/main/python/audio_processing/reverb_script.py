import librosa
import soundfile as sf
import numpy as np
from scipy.signal import fftconvolve

def apply_reverb(y, reverb_amount):
    impulse_response = np.zeros(int(reverb_amount * len(y)))
    impulse_response[::reverb_amount] = 1
    y_reverb = fftconvolve(y, impulse_response, mode='full')[:len(y)]
    return y_reverb

def process_audio_reverb(file_path, reverb_change):
    y, sr = librosa.load(file_path, sr=None)
    y_reverb = apply_reverb(y, reverb_change)

    output_path = file_path.replace(".wav", "_reverb_changed.wav")
    sf.write(output_path, y_reverb, sr, format='WAV')
    return output_path
