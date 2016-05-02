Gladtronome {
	var beatsDef;
	var beatsSynth;

	var secondsDef;
	var secondsSynth;

	var listener;

	*new {
		^super.new;
	}

	startBeats { |startingTempo = 120, changeAmount = 10, beatsBetweenChange = 8, filter = true|
		// register listener for tempo changes
		listener = OSCFunc({ |msg, time|
			("New tempo: " + msg[3]).postln;
		},'/tr', Server.local.addr);

		beatsSynth = this.getBeatsDef(startingTempo, changeAmount, beatsBetweenChange, filter).play;
	}

	getBeatsDef { |startingTempo = 120, changeAmount = 10, beatsBetweenChange = 8, filter = true|
		beatsDef = SynthDef('gladtronomeBeats', {
			// feed the click signal back into the beginning of the graph
			var clickFeedback = LocalIn.ar(1);
			var numberOfTempoChanges = (PulseCount.ar(clickFeedback) / beatsBetweenChange).floor;
			var clickBpm = startingTempo + (numberOfTempoChanges * changeAmount);
			var clickFrequency = clickBpm / 60;

			var click = Impulse.ar(clickFrequency);

			// send current bpm to client when tempo changes
			var sendTrigger = SendTrig.ar(Changed.ar(clickBpm), 0, clickBpm);

			var signal;
			if (filter) {
				signal = Ringz.ar(click, 440, 0.2);
			} {
				signal = click;
			};
			LocalOut.ar(click);
			Out.ar([0, 1], signal);
		});
		^beatsDef;
	}

	startSeconds { |startingTempo = 120, changeAmount = 10, secondsBetweenChange = 5, filter = true|
		// register listener for tempo changes
		listener = OSCFunc({ |msg, time|
			("New tempo: " + msg[3]).postln;
		},'/tr', Server.local.addr);

		secondsSynth = this.getSecondsDef(startingTempo, changeAmount, secondsBetweenChange, filter).play;
	}

	getSecondsDef { |startingTempo = 120, changeAmount = 10, secondsBetweenChange = 5, filter = true|

		secondsDef = SynthDef('gladtronomeSeconds', {
			var numberOfTempoChanges = PulseCount.kr(Impulse.kr(1/secondsBetweenChange)) - 1;
			var clickBpm = startingTempo + (numberOfTempoChanges * changeAmount);
			var clickFrequency = clickBpm / 60;

			var click = Impulse.ar(clickFrequency);

			// send current bpm to client when tempo changes
			var sendTrigger = SendTrig.kr(Changed.kr(clickBpm), 0, clickBpm);
			var signal;
			if (filter) {
				signal = Ringz.ar(click, 440, 0.2);
			} {
				signal = click;
			};
			Out.ar([0, 1], signal);
		});

		^secondsDef;
	}

	free {
		secondsSynth.free;
		beatsSynth.free;
		listener.free;
	}
}