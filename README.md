Gladtronome
===========

Gladtronome is a tempo-changing metronome written in SuperCollider. It can be configured to go faster or slower over time.

Sample usage
------------

```SuperCollider
// Make a metronome that starts at 120 bpm
// and increases the tempo by 10 bpm every 8 beats.
Gladtronome.beats(startingTempo:120, beatsBetweenChange: 8, changeAmount: 10);

// Make a metronome that starts at 120 bpm 
// and increases the tempo by 5 bpm every 30 seconds.
Gladtronome.beats(startingTempo:120, secondsBetweenChange: 30, changeAmount: 5);
```

Installation
------------

Make sure the directory for this repo is in
one of SuperCollider's extension directories, then reopen SuperCollider or
recompile the class library. You can recompile the class library through the menu with
`Language -> Recompile Class Library`.

```SuperCollider
// Evaluate the follow line in SuperCollider to find your user extension directory.
// This will install gladtronome for just one user.
Platform.userExtensionDir;

// Evaluate the following line in SuperCollider to find your system extension directory.
// This will install gladtronome for all users.
Platform.systemExtensionDir;
```

Testing
-------

Gladtronome comes with a simple test suite. After installing Gladtronome, you can run it with

```SuperCollider
GladtronomeTest.test;
```

This will run the metronome and record the results to an audio file on your hard drive.
The resulting audio file will be analyzed, and the analysis results will be printed to the console.

Sample output:

```
Synthesizing audio file...
// ...
Done synthesizing audio file.

Analyzing audio file...
// indexes of the samples where the metronome clicks occur
Impulse locations:  [ 4352, 48453, 92553, 136653, 180753, 202827, 224877, 246927, 268977 ]
// time between each click
Note durations:  [ 1.000022675737, 1, 1, 1, 0.50054421768707, 0.5, 0.5, 0.5 ]
// difference from expected note duration in milliseconds
Milliseconds of error:  [ 0.022675736961464, 0, 0, 0, 0.5442176870748, 0, 0, 0 ]
```
