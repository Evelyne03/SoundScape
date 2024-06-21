from flask import Flask, request, Response, stream_with_context, send_file, jsonify
import librosa
import soundfile as sf
from pydub import AudioSegment
import os
import noisereduce as nr
import sys
import matplotlib.pyplot as plt
from io import BytesIO
import tempfile
import numpy as np
import requests
import noisereduce as nr
from scipy.signal import fftconvolve
import logging


from speed_script import process_audio_speed
from reverb_script import process_audio_reverb
logging.basicConfig(level=logging.DEBUG)

app = Flask(__name__)

@app.route('/change_pitch', methods=['POST'])
def change_pitch():
    file = request.files['file']
    pitch_change = int(request.form['pitch_change'])

    file_path = 'temp_input_file.wav'
    file.save(file_path)

    if librosa.get_samplerate(file_path) is None:
        audio = AudioSegment.from_mp3(file_path)
        audio.export(file_path, format="wav")

    y, sr = librosa.load(file_path, sr=None)
    y_shifted = librosa.effects.pitch_shift(y, sr=sr, n_steps=pitch_change)

    output_file = 'modified_output.wav'
    sf.write(output_file, y_shifted, sr, format='WAV')

    @stream_with_context
    def generate():
        try:
            with open(output_file, 'rb') as f:
                data = f.read(4096)
                while data:
                    yield data
                    data = f.read(4096)
        finally:
            os.remove(output_file)

    response = Response(generate(), mimetype="audio/wav")
    response.headers['Content-Disposition'] = 'attachment; filename=modified_output.wav'
    response.headers['Transfer-Encoding'] = 'chunked'

    os.remove(file_path)

    return response

@app.route('/denoise_audio', methods=['POST'])
def denoise_audio():
    file = request.files['file']
    file_path = 'temp_denoise_input_file.wav'
    file.save(file_path)

    # Load audio
    y, sr = librosa.load(file_path, sr=None)
    # Reduce noise
    reduced_noise = nr.reduce_noise(y=y, sr=sr)

    # Save the denoised audio
    output_file = 'denoised_output.wav'
    sf.write(output_file, reduced_noise, sr, format='wav')

    @stream_with_context
    def generate():
        try:
            with open(output_file, 'rb') as f:
                data = f.read(4096)
                while data:
                    yield data
                    data = f.read(4096)
        finally:
            os.remove(output_file)
            os.remove(file_path)

    response = Response(generate(), mimetype="audio/wav")
    response.headers['Content-Disposition'] = 'attachment; filename=denoised_output.wav'
    return response

@app.route('/apply_changes', methods=['POST'])
def apply_changes():
    try:
        file = request.files['file']
        pitch_change = int(request.form['pitch_change'])
        speed_change = float(request.form['speed_change'])
        reverb_change = int(request.form['reverb_change'])

        logging.debug(f"Received pitch_change: {pitch_change}, speed_change: {speed_change}, reverb_change: {reverb_change}")

        file_path = 'temp_input_file.wav'
        file.save(file_path)

        if librosa.get_samplerate(file_path) is None:
            audio = AudioSegment.from_mp3(file_path)
            audio.export(file_path, format="wav")

        y, sr = librosa.load(file_path, sr=None)
        y_shifted = librosa.effects.pitch_shift(y, sr=sr, n_steps=pitch_change)
        y_speed_changed = librosa.effects.time_stretch(y_shifted, rate=speed_change)
        y_reverb = apply_reverb(y_speed_changed, reverb_change)

        output_file = 'modified_output.wav'
        sf.write(output_file, y_reverb, sr, format='WAV')

        @stream_with_context
        def generate():
            try:
                with open(output_file, 'rb') as f:
                    data = f.read(4096)
                    while data:
                        yield data
                        data = f.read(4096)
            finally:
                os.remove(output_file)

        response = Response(generate(), mimetype="audio/wav")
        response.headers['Content-Disposition'] = 'attachment; filename=modified_output.wav'
        response.headers['Transfer-Encoding'] = 'chunked'

        os.remove(file_path)

        return response
    except Exception as e:
        logging.error("Error in apply_changes: %s", e)
        return Response(str(e), status=500)

def apply_reverb(y, reverb_amount):
    try:
        if reverb_amount == 0:
            return y
        impulse_response = np.zeros(int(reverb_amount * len(y)))
        impulse_response[::reverb_amount] = 1
        y_reverb = fftconvolve(y, impulse_response, mode='full')[:len(y)]
        return y_reverb
    except Exception as e:
        logging.error("Error in apply_reverb: %s", e)
        raise e


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)