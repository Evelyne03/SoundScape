from flask import Flask, request, Response, stream_with_context
import librosa
import soundfile as sf
from pydub import AudioSegment
import os

app = Flask(__name__)

@app.route('/change_pitch', methods=['POST'])
def change_pitch():
    file = request.files['file']
    pitch_change = int(request.form['pitch_change'])

    file_path = 'temp_input_file'
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
    response.headers['Transfer-Encoding'] = 'chunked'  # Ensure the client knows it's chunked

    os.remove(file_path)

    return response

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
