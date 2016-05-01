Gladtronome {
	var synth;
	var listener;

	*new {
		^super.new;
	}

	startBeats { |startingTempo = 120, changeAmount = 10, beatsBetweenChange = 8|
		// register listener for tempo changes
		listener = OSCFunc({ |msg, time|
			("New tempo: " + msg[3]).postln;
		},'/tr', Server.local.addr);

		synth = SynthDef('gladtronomeStartBeats', {
			// feed the click signal back into the beginning of the graph
			var clickFeedback = LocalIn.ar(1);
			var numberOfTempoChanges = (PulseCount.ar(clickFeedback) / beatsBetweenChange).floor;
			var clickBpm = startingTempo + (numberOfTempoChanges * changeAmount);
			var clickFrequency = clickBpm / 60;

			var click = Impulse.ar(clickFrequency);

			// send current bpm to client when tempo changes
			var sendTrigger = SendTrig.ar(Changed.ar(clickBpm), 0, clickBpm);

			var signal = Ringz.ar(click, 440, 0.2);
			LocalOut.ar(click);
			Out.ar([0, 1], signal);
		}).add.play;
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
}