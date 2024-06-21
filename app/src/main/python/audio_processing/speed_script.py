import librosa
import soundfile as sf

def process_audio_speed(file_path, speed_change):
    y, sr = librosa.load(file_path, sr=None)
    y_changed = librosa.effects.time_stretch(y, speed_change)

    output_path = file_path.replace(".wav", "_speed_changed.wav")
    sf.write(output_path, y_changed, sr, format='WAV')
    return output_path
