#!/usr/bin/env bash

which aplay >/dev/null || sudo apt install alsa-utils -y && javac *.java && java Main && aplay output.wav
