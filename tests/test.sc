GladtronomeTest {
	*test {
		var offset = 0.1; // seconds
		var duration = 6; // seconds
		var sampleRate = Server.local.sampleRate;

		var testsDir = thisProcess.nowExecutingPath.dirname;
		var audioPath = testsDir +/+ "audio" +/+ "secondsTest.wav";

		// metronome settings
		var tempo = 60;
		var changeAmount = 60; // bpm
		var changeAfter = 4;
		var expectedNoteDurations = [1, 1, 1, 1, 0.5, 0.5, 0.5, 0.5]; // seconds
		var synthesize, analyze, noteDurations, compareToExpected;

		noteDurations = { |impulseLocations|
			var timeDistances = impulseLocations.collect({ |impulseLocation, i|
				var previousLocation, sampleDistance, timeDistance;
				if (i != 0) {
					previousLocation = impulseLocations[i - 1];
					sampleDistance = impulseLocation - previousLocation;
					timeDistance = sampleDistance / sampleRate;
				}
			});
			// remove first element, which is always nil
			timeDistances.removeAt(0);
			("Note durations: " + timeDistances).postln;
			timeDistances;
		};

		compareToExpected = { |durations|
			var errorAmounts;
			errorAmounts = durations.collect({ |actual, i|
				var expected = expectedNoteDurations[i];
				(actual - expected) * 1000;
			});
			("Milliseconds of error: " + errorAmounts).postln;
		};

		// synthesize audio file
		synthesize = { |onComplete|
			var metronome, scorePath, score, options;
			"\nSynthesizing audio file...".postln;

			scorePath = testsDir +/+ "scores" +/+ "test.osc";

			Gladtronome.getSecondsDef(tempo, changeAmount, changeAfter, false).writeDefFile;
			// metronome.getBeatsDef(tempo, changeAmount, changeAfter, false).writeDefFile;

			TempoClock.default.tempo = 1;
			score = [
				[offset, [\s_new, \gladtronomeSeconds, 1000, 0, 0]],
				[offset + duration, [\c_set, 0, 0]]
			];
			options = ServerOptions.new.numOutputBusChannels = 1; // mono output
			Score.recordNRT(score, scorePath, audioPath, options: options, action: {
				"Done synthesizing audio file.".postln;
				onComplete.value;
			});
		};

		// analyze for correct note duration
		analyze = {
			var gotSamples, durations;
			"\nAnalyzing audio file...".postln;

			gotSamples = { |samples|
				var impulseLocations = [];
				for(0, samples.size - 1, { |i|
					var sampleValue = samples[i];
					if (sampleValue > 0) {
						impulseLocations = impulseLocations.add(i);
					}
				});
				("Impulse locations: " + impulseLocations).postln;
				durations = noteDurations.value(impulseLocations);
				compareToExpected.value(durations);
			};

			Buffer.read(Server.local, audioPath, action: { |buf| buf.loadToFloatArray(action: gotSamples); });
		};

		synthesize.value(analyze);
	}
}