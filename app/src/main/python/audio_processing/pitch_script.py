import librosa
from librosa import effects

def process_audio(file_path):
    y, sr = librosa.load(file_path)  # Load the file with librosa
    y_shifted = effects.pitch_shift(y, sr, n_steps=2)  # Shift pitch by 2 semitones

    # Save the processed file
    output_path = file_path.replace(".wav", ".wav")  # Modify the file name
    librosa.output.write_wav(output_path, y_shifted, sr)
    return output_path
