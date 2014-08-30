Gladtronome {
	*new {
		SynthDef(\gladtronome_default_synth, {
			var pathToAudioSample = this.class.filenameSymbol.asString.dirname +/+ "Audio/hihat.wav";
			var buf = Buffer.read(Server.default, pathToAudioSample);
			var signal = PlayBuf.ar(2, buf.bufnum, BufRateScale.kr(buf.bufnum));
			Out.ar(0, signal);
		}).add;

		^super.new;
	}

	start { arg starting_tempo, beats_before_changing_tempo, tempo_change_amount, synthSymbol = \gladtronome_default_synth;
		Task({
			var duration, current_beat, current_tempo;

			current_beat = 0;
			current_tempo = starting_tempo;
			duration = this.duration_of_beat(current_tempo);

			while ( {duration > 0.001}, {
				Synth(synthSymbol);
				duration.wait;
				current_beat = current_beat + 1;
				if(current_beat >= beats_before_changing_tempo) {
					current_beat = 0;
					current_tempo = current_tempo + tempo_change_amount;
					duration = this.duration_of_beat(current_tempo);
					("new tempo: " + current_tempo).postln;
				}
			});

			"stopped".postln;
		}).play;
	}

	duration_of_beat { arg bpm;
		^60 / bpm;
	}
}