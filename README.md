Gladtronome
===========

Gladtronome is a tempo-changing metronome written in SuperCollider. 
It's useful for doing speed drills when practicing an instrument.

It can be configured to go faster or slower over time.

===========

Sample usage:

// Make a metronome that starts at 120 bpm
// and increases the tempo by 10 bpm every 8 beats.
var metronome = Gladtronome.new;
metronome.start(starting_tempo:120, beats_before_changing_tempo: 8, tempo_change_amount:10);

// Make a metronome that starts at 100 bpm
// and decreases the tempo by 5 bpm every 16 beats.
var sadtronome = Gladtronome.new;
sadtronome.start(starting_tempo:100, beats_before_changing_tempo: 16, tempo_change_amount:-5);

// Make a metronome that uses a different click sound.
SynthDef(\alternate_click, {
			var signal, clickosc, clickenv;
			clickosc = {LPF.ar(WhiteNoise.ar(1), 10000)};
			clickenv = {Line.ar(1, 0, 0.02)};
			signal = clickosc * clickenv;
			Out.ar(0, Pan2.ar(signal));
		}).add;

var alternate_click_metronome = Gladtronome.new;
alternate_click_metronome.start(starting_tempo:100, beats_before_changing_tempo: 16, tempo_change_amount:-5, synthSymbol:\alternate_click);

===========

Installation:

Make sure the gladtronome directory, including the audio sub-directory, is in
one of SuperCollider's extension directories, then reopen SuperCollider or
recompile the class library.

Platform Specific Directories

User-specific
OSX			~/Library/Application Support/SuperCollider/Extensions/
Linux		~/share/SuperCollider/Extensions/

// Evaluate the follow line in SuperCollider to find your user extension directory
Platform.userExtensionDir;

System-wide (apply to all users)
OSX			/Library/Application Support/SuperCollider/Extensions/
Linux		/usr/local/share/SuperCollider/Extensions/

// Evaluate the following line in SuperCollider to find your system extension directory
Platform.systemExtensionDir;