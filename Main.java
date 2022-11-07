import java.io.File;
import java.io.IOException;

public class Main {

  public static final int SAMPLES_PER_SECOND = 44100;

  public static void main(String[] args) {
    double[] sampleBuffer = getSampleBufferFromFile("voice.wav");

    // Amplify so the highest magnitude single sample just under 1.0 or just above -1.0
    /*
    for (int i = 0; i < sampleBuffer.length; i++) {
      sampleBuffer[i] *= 2.53917309289;
    }
    */ // End of amplify

    // Compression / distortion
    /*
    for (int i = 0; i < sampleBuffer.length; i++) {
      double sampleMagnitude = Math.abs(sampleBuffer[i]);
      if (sampleMagnitude >= 0.5) {
        sampleBuffer[i] *= 0.5;
      } else if (sampleMagnitude > 0.3) {
        sampleBuffer[i] *= 0.7;
      } else if (sampleMagnitude > 0.1) {
        sampleBuffer[i] *= 0.85;
      }
    }
    */ // End of Compression

    // Increase playback speed 2x
    /*
    for (int i = 0; i < sampleBuffer.length; i++) {
      if (i < sampleBuffer.length / 2) {
        // Set each sample in the first half of the file to be the one double its own distance to the end
        sampleBuffer[i] = sampleBuffer[i * 2];
      } else {
        // zero out the rest of the samples
        sampleBuffer[i] = 0;
      }
    }
    */ // End of speedup

    // Decrease playback speed 2x
    /*
    for (int i = sampleBuffer.length - 1; i >= 0; i--) {
      sampleBuffer[i] = sampleBuffer[i / 2];
    }
    */ // End of slowdown

    // Fade in/out envelope
    /*
    double amplitudeChangePerSample = 1.0 / SAMPLES_PER_SECOND;
    for (int i = 0; i < sampleBuffer.length; i++) {
      // don't change the first second
      if (i < SAMPLES_PER_SECOND) {
        continue;
      } else if (i < SAMPLES_PER_SECOND * 2) {
        // fade out between seconds 1 and 2
        int numberOfSamplesIntoFade = i - SAMPLES_PER_SECOND;
        double amplitudeScale = 1 - numberOfSamplesIntoFade * amplitudeChangePerSample;
        sampleBuffer[i] *= amplitudeScale;
      } else if (i < SAMPLES_PER_SECOND * 3) {
        // fade in between seconds 2 and 3
        int numberOfSamplesIntoFade = i - SAMPLES_PER_SECOND * 2;
        double amplitudeScale = numberOfSamplesIntoFade * amplitudeChangePerSample;
        sampleBuffer[i] *= amplitudeScale;
      }
    }
    */

    // Amplitude (Ring) modulation
    /*
    double oscillationFrequency = 4.0;
    double samplesPerOscillation = SAMPLES_PER_SECOND / oscillationFrequency;
    for (int i = 0; i < sampleBuffer.length; i++) {
      // this variable will go from 0 to 1 each second
      double currentEnvelopeProgress = Math.abs(((i % samplesPerOscillation) - samplesPerOscillation) / samplesPerOscillation);
      // this variable will go from 0 to PI each second
      double progressInPi = currentEnvelopeProgress * Math.PI * 2;
      // scale output to between 0 and 1 instead of -1 and 1
      double amplitudeScale = Math.cos(progressInPi) * 0.5 + 0.5;
      sampleBuffer[i] *= amplitudeScale;
    }
    */

    writeSamplesToFile("output.wav", sampleBuffer);
  }

  private static void writeSamplesToFile(String filename, double[] samples) {
    File file = new File(filename);
    try {
      WavFile wavFile = WavFile.newWavFile(file, /* numChannels= */ 1, /* numSamples= */ samples.length, /* validBits= */ 16, /* sampleRate= */ 44100);
      wavFile.writeFrames(samples, samples.length);
    } catch (IOException | WavFileException e) {
      throw new RuntimeException(e);
    }
  }

  private static double[] getSampleBufferFromFile(String filename) {
    try {
      WavFile wavFile = WavFile.openWavFile(new File(filename));
      double[] buffer = new double[(int) wavFile.getNumFrames()];
      wavFile.readFrames(buffer, (int) wavFile.getNumFrames());
      wavFile.close();

      return buffer;
    } catch (IOException | WavFileException e) {
      throw new RuntimeException(e);
    }
  }
}
