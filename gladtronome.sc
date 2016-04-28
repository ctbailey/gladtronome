Gladtronome {
	var synth;
	var listener;

	*new {
		^super.new;
	}

	start_beats { arg starting_tempo, beats_before_changing_tempo, tempo_change_amount, synthSymbol = \gladtronome_default_synth;
		Task({
			var duration, current_beat, current_tempo;

			current_beat = 0;
			current_tempo = starting_tempo;
			duration = this.duration_of_beat(current_tempo);

			while ( {duration > 0.001}, {
				var synth = Synth(synthSymbol);
				duration.wait;
				synth.free;
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

	startSeconds { |startingTempo = 120, changeAmount = 10, secondsBetweenChange = 5|
		// register listener for tempo changes
		listener = OSCFunc({ |msg, time|
			("New tempo: " + msg[3]).postln;
		},'/tr', Server.local.addr);

		synth = SynthDef('gladtronomeStartSeconds', {
			var numberOfTempoChanges = PulseCount.kr(Impulse.kr(1/secondsBetweenChange)) - 1;
			var clickBpm = startingTempo + (numberOfTempoChanges * changeAmount);
			var clickFrequency = clickBpm / 60;

			// send current bpm to client when tempo changes
			var sendTrigger = SendTrig.kr(Changed.kr(clickBpm), 0, clickBpm);

			var signal = Ringz.ar(Impulse.ar(clickFrequency), 440, 0.2);
			Out.ar([0, 1], signal);
		}).add.play;
	}

	free {
		synth.free;
		listener.free;
	}

	/*start_seconds{ arg starting_tempo, seconds_before_changing_tempo, tempo_change_amount, synthSymbol = \gladtronome_default_synth;

		Task({
			var duration, time_of_last_tempo_increase, current_tempo;

			time_of_last_tempo_increase = Main.elapsedTime;
			current_tempo = starting_tempo;
			duration = this.duration_of_beat(current_tempo);

			while ( {duration > 0.001}, {
				var synth = Synth(synthSymbol);
				duration.wait;
				synth.free;
				if((Main.elapsedTime - time_of_last_tempo_increase) >= seconds_before_changing_tempo) {
					time_of_last_tempo_increase = Main.elapsedTime;
					current_tempo = current_tempo + tempo_change_amount;
					duration = this.duration_of_beat(current_tempo);
					("new tempo: " + current_tempo).postln;
				}
			});

			"stopped".postln;
		}).play;
	}*/

	duration_of_beat { arg bpm;
		^60 / bpm;
	}
}