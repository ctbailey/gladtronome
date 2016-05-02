(
var offset = 0.1; // seconds
var duration = 8; // seconds
var sampleRate = s.sampleRate;

var testsDir = thisProcess.nowExecutingPath.dirname;
var audioPath = testsDir +/+ "audio" +/+ "secondsTest.wav";

// metronome settings
var tempo = 60;
var changeAmount = 60; // bpm
var changeAfter = 4;
var synthesize, analyze;

// synthesize audio file
synthesize = { |onComplete|
	var metronome, scorePath, score, options;
	"\nSynthesizing audio file...".postln;

	metronome = Gladtronome.new;
	scorePath = testsDir +/+ "scores" +/+ "test.osc";

	metronome.getSecondsDef(tempo, changeAmount, changeAfter, false).writeDefFile;
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

// analyze for currect note duration
analyze = {
	var gotSamples, impulseLocations;
	"\nAnalyzing audio file...".postln;

	impulseLocations = [];

	gotSamples = { |samples|
		for(0, samples.size - 1, { |i|
			var sample = samples[i];
			if (sample > 0.5) {
				impulseLocations = impulseLocations.add(i);
			}
		});
		impulseLocations.postln;
	};

	Buffer.read(s, audioPath, action: { |buf| buf.loadToFloatArray(action: gotSamples); });
};

synthesize.value(analyze);
)