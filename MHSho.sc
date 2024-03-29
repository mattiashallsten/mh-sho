/*
################
##### TODO #####
################

- [x] Change the routines into functions, only call Routine(function) in the teutsuri method
- [ ] Implement breathing mechanism
- [x] Add amp argument 
- [x] Implement function to change between pythagorean and ET12 tuning

*/

// * Class: MHSho
MHSho {
	var <root, <>tempo, amp, tuning, ampLag, out, outmode, target;
	var server;
	var <aitake = \kotsu, aitakeList, <teutsuriList, notes, <reeds, <isPlaying=false;
	var <reedStatus, localSynth = \mh_sho_saw;

	// * Class method: *new
	*new {|root = 644.27204305697, tempo = 2, amp = 0.2, tuning='pyth', ampLag = 1.0, out = 0, outmode = 'mono', target = 0|
		^super.newCopyArgs(root, tempo, amp, tuning, ampLag, out, outmode, target).initSho
	}

	// * Instance method: initSho
	initSho {
		server = Server.default;

		if(outmode == 'split', {
			server.options.numOutputBusChannels = 16 // if in 'split' mode, make sure there are enough output channels
		});
		
		server.waitForBoot {
			// * SynthDef: mh_sho_saw
			SynthDef(\mh_sho_saw, {|gate=0, freq=440, atk=3, rel=0.3, amp=0.2, out=0, harmonics=0,finalgate=1|
				var env = EnvGen.kr(Env.asr(atk, 1, rel), gate);
				var sig = Saw.ar((freq.lag(0.1)).clip(20, 18000));

				sig = BLowPass.ar(sig, (freq * 8).clip(20, 18000));

				sig = sig * env * amp.lag(ampLag) * 0.7 * EnvGen.kr(Env.asr(),finalgate,doneAction:2);

				Out.ar(out,sig)
			}).add;

			server.sync;

			this.setNotes(tuning);
			this.loadAitakeList;

			// aitakeList = (
			// 	kotsu: [1,0,0,0,1,0,0,0,1,1,0,0,0,1,1],
			// 	ichi: [0,1,0,1,1,0,0,0,1,1,0,0,0,0,1],
			// 	ku: [0,0,1,1,1,0,0,1,1,1,0,0,0,0,0],
			// 	bo: [0,0,0,1,1,0,0,0,1,1,0,0,0,1,1],
			// 	otsu: [0,0,0,0,1,0,0,0,1,1,0,0,1,1,1],
			// 	ge: [0,0,0,0,0,1,0,1,1,1,0,0,1,1,1],
			// 	ju: [0,0,0,0,0,0,1,0,1,1,0,0,1,1,0],
			// 	ju_II: [0,0,0,0,0,1,1,0,1,1,0,0,1,1,0],
			// 	bi: [0,0,0,0,0,0,0,1,1,1,1,0,1,0,1],
			// 	gyo: [0,0,0,0,0,0,0,0,1,1,0,0,1,1,1],
			// 	hi: [0,0,0,0,0,0,0,0,1,1,1,0,1,1,1],
			// );

			this.loadReeds;



			this.loadTeutsuriList;
			

		}

	}
	// * Instance method: free
	free {
		reeds.do{|item|
			item.set(\finalgate, 0)
		};
	}

	// * Instance method: aitake_
	aitake_ {|newAitake = \kotsu, callback|
		if(aitakeList[newAitake].notNil, {
			aitake = newAitake;
			this.setGate(1);
			if(callback.isFunction, {
				callback.value(1);
			});
		}, {
			"Aitake does not exist!".postln;
			if(callback.isFunction, {
				callback.value(0);
			});
		});
	}

	// * Class method: teutsuri
	teutsuri {|newAitake = \kotsu, callback|
		if(isPlaying, {
			if(teutsuriList[aitake].notNil,	{
				if(teutsuriList[aitake][newAitake].notNil, {
					fork {
						// make sure everything's correct with the selected aitake
						this.aitake_(aitake);
						
						// play the routine for changing to the new aitake
						Routine(teutsuriList[aitake][newAitake]).play;

						if(callback.isFunction, {
							callback.value(1);
						});

						// // reset all the routines
						// teutsuriList.do{|item|
						// 	if(item.notNil,
						// 		{
						// 			item.do{|routine|
						// 				if(routine.notNil,
						// 					{
						// 						routine.reset
						// 					}
						// 				);
						// 			}
						// 		}
						// 	);
						// };
					}
				}, {
					"Te-utsuri not implemented yet/not possible".postln;
					if(callback.isFunction, {
						callback.value(0);
					});
				})
			}, {
				"Te-utsuri not implemented yet/not possible.".postln;
				if(callback.isFunction, {
					callback.value(0);
				});
			});
		}, {
			this.aitake_(newAitake);
			if(callback.isFunction, {
					callback.value(1);
			});
		});
	}

	// * Class method: loadReeds
	loadReeds {
		reeds = notes.collect{|item, i|
			var reed_output = switch(outmode,
				'mono', {out},
				'split', {out + i},
				{out}
			);
			Synth(localSynth, [
				\freq, root * notes[i],
				\gate, 0,
				\amp, amp,
				\out, reed_output,
				\atk, 0.3,
				\rel, 0.5,
			], target)
		};

		reedStatus = reeds.collect{
			0
		};
	}

	// * Class method: loadAitakeList
	loadAitakeList {
		aitakeList = (
			kotsu: [1,0,0,0,1,0,0,0,1,1,0,0,0,1,1],
			ichi: [0,1,0,1,1,0,0,0,1,1,0,0,0,0,1],
			ku: [0,0,1,1,1,0,0,1,1,1,0,0,0,0,0],
			bo: [0,0,0,1,1,0,0,0,1,1,0,0,0,1,1],
			otsu: [0,0,0,0,1,0,0,0,1,1,0,0,1,1,1],
			ge: [0,0,0,0,0,1,0,1,1,1,0,0,1,1,1],
			ju: [0,0,0,0,0,0,1,0,1,1,0,0,1,1,0],
			ju_II: [0,0,0,0,0,1,1,0,1,1,0,0,1,1,0],
			bi: [0,0,0,0,0,0,0,1,1,1,1,0,1,0,1],
			gyo: [0,0,0,0,0,0,0,0,1,1,0,0,1,1,1],
			hi: [0,0,0,0,0,0,0,0,1,1,1,0,1,1,1],
		);
	}

	// * Class method: loadTeutsuriList
	loadTeutsuriList {
		teutsuriList = (
			ichi: (
				bo: {
					wait(5/16 * tempo);
					this.reedGate(1, 0);
					
					wait(1/8 * tempo);
					this.reedGate(13, 0);

					this.aitake_(\bo);
				},
				kotsu: {
					wait(5/16 * tempo);
					this.reedGate(1, 0);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(0, 1);
					this.reedGate(13, 1);

					this.aitake_(\kotsu);
				},
				otsu: {
					wait(5/16 * tempo);
					this.reedGate(1, 0);
					this.reedGate(3, 0);
					
					wait(1/8 * tempo);
					this.reedGate(12, 0);
					this.reedGate(13, 0);

					this.aitake_(\otsu);
				},
				ku: {
					wait(5/16 * tempo);
					this.reedGate(1, 0);
					this.reedGate(14, 0);

					wait(1/8 * tempo);
					this.reedGate(7, 1);
					this.reedGate(2, 1);

					this.aitake_(\ku);
				},
				gyo: {
					wait(5/16 * tempo);
					this.reedGate(1, 0);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate(12, 1);
					this.reedGate(13, 1);

					this.aitake_(\gyo);
				},
				hi: {
					wait(5/16 * tempo);
					this.reedGate(1, 0);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate(10, 1);
					this.reedGate(12, 1);
					this.reedGate(13, 1);

					this.aitake_(\hi);
				},
				ge: {
					wait(5/16 * tempo);
					this.reedGate(1, 0);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate(6, 1);
					this.reedGate(12, 1);
					this.reedGate(5, 1);

					this.aitake_(\ge);
				},
				bi: {
					wait(5/16 * tempo);
					this.reedGate(1, 0);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate(7, 1);
					this.reedGate(12, 1);
					this.reedGate(10, 1);

					this.aitake_(\bi);
				},
				ju_II: {
					wait(1/4 * tempo);
					this.reedGate(14, 0);

					wait(1/16 * tempo);
					this.reedGate([1, 3], 0);
					//this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate(5, 1);
					this.reedGate(6, 1);
					this.reedGate(12, 1);
					this.reedGate(13, 1);

					this.aitake_(\ju_II);
				},
				ju: {
					wait(1/4 * tempo);
					this.reedGate(14, 0);

					wait(1/16 * tempo);
					this.reedGate([1, 3], 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([12, 13, 6], 1);

					this.aitake_(\ju);
				},
				kotsu: {
					wait(5/16 * tempo);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(1, 0);
					this.reedGate([0, 13], 1);

					this.aitake_(\kotsu);
				},
			),
			kotsu: (
				bo: {
					wait(5/16 * tempo);
					this.reedGate(0, 0);

					wait(1/8 * tempo);
					this.reedGate(3, 1);

					this.aitake_(\bo);
				},
				otsu: {
					wait(5/16 * tempo);
					this.reedGate(0, 0);

					wait(1/8 * tempo);
					this.reedGate(12, 1);

					this.aitake_(\otsu);
				},
				ichi: {
					wait(5/16 * tempo);
					this.reedGate([0, 13], 0);

					wait(1/8 * tempo);
					this.reedGate([1,3], 1);

					this.aitake_(\ichi);
				},
				hi: {
					wait(5/16 * tempo);
					this.reedGate(0, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([12, 10], 1);

					this.aitake_(\hi);
				},
				ge: {
					wait(5/16 * tempo);
					this.reedGate([0, 13], 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([5, 7, 12], 1);

					this.aitake_(\ge);
				},
				bi: {
					wait(5/16 * tempo);
					this.reedGate([0, 13], 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([7, 10, 12], 1);

					this.aitake_(\bi)
				},
				ku: {
					wait(1/4 * tempo);
					this.reedGate(14, 0);

					wait(1/32 * tempo);
					this.reedGate(13, 0);

					wait(1/32 * tempo);
					this.reedGate(0, 0);

					wait(1/8 * tempo);
					this.reedGate([2, 3, 7], 1);

					this.aitake_(\ku);
				},
				ju_II: {
					wait(9/32 * tempo);
					this.reedGate(14, 0);

					wait(1/32 * tempo);
					this.reedGate(0, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([12, 5, 6], 1);

					this.aitake_(\ju_II);
				},
				ju: {
					wait(9/32 * tempo);
					this.reedGate(14, 0);

					wait(1/32 * tempo);
					this.reedGate(0, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([12, 6], 1);

					this.aitake_(\ju);
				},
				gyo: {
					wait(5/16 * tempo);
					this.reedGate(0, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate(12, 1);

					this.aitake_(\gyo);
				},
			),
			ku: (
				bo: {
					wait(5/16 * tempo);
					this.reedGate(7, 0);

					wait(1/8 * tempo);
					this.reedGate(2, 0);
					this.reedGate(13, 1);

					wait(5/16 * tempo);
					this.reedGate(14, 1);

					this.aitake_(\bo);
				},
				ichi: {
					wait(5/16 * tempo);
					this.reedGate(7, 0);

					wait(1/8 * tempo);
					this.reedGate(2, 0);
					this.reedGate(1, 1);

					wait(5/16 * tempo);
					this.reedGate(14, 1);

					this.aitake_(\ichi);
				},
				otsu: {
					wait(5/16 * tempo);
					this.reedGate([3, 7], 0);

					wait(1/8 * tempo);
					this.reedGate(2, 0);
					this.reedGate([12, 13], 1);

					wait(5/16 * tempo);
					this.reedGate(14, 1);

					this.aitake_(\otsu);
				},
				kotsu: {
					wait(5/16 * tempo);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate([7, 2], 0);
					this.reedGate(0, 1);

					wait(5/16 * tempo);
					this.reedGate(13, 1);

					wait(1/4 * tempo);
					this.reedGate(14, 1);

					this.aitake_(\kotsu);
				},
				ge: {
					wait(5/16 * tempo);
					this.reedGate([3, 2], 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([12, 5, 14], 1);

					this.aitake_(\ge);
				},
				bi: {
					wait(5/16 * tempo);
					this.reedGate([3, 2], 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([12, 10, 14], 1);

					this.aitake_(\bi);
				},
				hi: {
					wait(1/4 * tempo);
					this.reedGate(3, 0);
					
					wait(1/16 * tempo);
					this.reedGate([7, 2], 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([12, 13, 10, 14], 1);

					this.aitake_(\hi);
				}
			),
			ge: (
				bi: {
					wait(7/16 * tempo);
					this.reedGate(5, 0);
					this.reedGate(10, 0);

					this.aitake_(\bi);
				},
				ju_II: {
					wait(5/16 * tempo);
					this.reedGate([7, 14], 0);

					wait(1/8 * tempo);
					this.reedGate([6, 13], 1);

					this.aitake_(\ju_II);
				},
				otsu: {
					wait(5/16 * tempo);
					this.reedGate(5, 0);

					wait(1/8 * tempo);
					this.reedGate(7, 0);
					this.reedGate(4, 1);

					wait(5/16 * tempo);
					this.reedGate(13, 1);

					this.aitake_(\otsu);
				},
				hi: {
					wait(5/16 * tempo);
					this.reedGate(7, 0);

					wait(1/8 * tempo);
					this.reedGate(5, 0);
					this.reedGate([10, 13], 1);

					this.aitake_(\hi);
				},
				gyo: {
					wait(7/16 * tempo);
					this.reedGate([5, 7], 0);
					this.reedGate(13, 1);
					
					this.aitake_(\gyo);
				},
				kotsu: {
					wait(5/16 * tempo);
					this.reedGate([5, 12], 0);

					wait(1/8 * tempo);
					this.reedGate(7, 0);
					this.reedGate([0, 4], 1);

					wait(5/16 * tempo);
					this.reedGate(13, 1);

					this.aitake_(\kotsu);
				},
				bo: {
					wait(5/16 * tempo);
					this.reedGate([5, 12], 0);

					wait(1/8 * tempo);
					this.reedGate(7, 0);
					this.reedGate([3, 4], 1);

					wait(5/16 * tempo);
					this.reedGate(13, 1);

					this.aitake_(\bo);
				},
				ichi: {
					wait(1/4 * tempo);
					this.reedGate(12, 0);
					
					wait(1/16 * tempo);
					this.reedGate([7, 5], 0);

					wait(1/8 * tempo);
					this.reedGate([1, 3, 4], 1);

					this.aitake_(\ichi);
				},
				ku: {
					wait(1/4 * tempo);
					this.reedGate(14, 0);

					wait(1/16 * tempo);
					this.reedGate([12, 5], 0);

					wait(1/8 * tempo);
					this.reedGate([3, 1, 4], 1);

					this.aitake_(\ku);
				},
				ju: {
					wait(5/16 * tempo);
					this.reedGate([7, 14], 0);

					wait(1/8 * tempo);
					this.reedGate(5, 0);
					this.reedGate([6, 13], 1);

					this.aitake_(\ju)
				},
			),
			ju_II: (
				ju: {
					wait(7/16 * tempo);
					this.reedGate(5, 0);

					this.aitake_(\ju);
				},
				otsu: {
					wait(5/16 * tempo);
					this.reedGate(5, 0);

					wait(1/8 * tempo);
					this.reedGate(6, 0);
					this.reedGate([4, 14], 1);

					this.aitake_(\otsu);
				},
				hi: {
					wait(5/16 * tempo);
					this.reedGate(5, 0);

					wait(1/8 * tempo);
					this.reedGate(6, 0);
					this.reedGate([10, 14], 1);

					this.aitake_(\hi);
				},
				ge: {
					wait(5/16 * tempo);
					this.reedGate(13, 0);

					wait(1/8 * tempo);
					this.reedGate(6, 0);
					this.reedGate([7, 14], 1);

					this.aitake_(\ge);
				},
				kotsu: {
					wait(5/16 * tempo);
					this.reedGate([5, 12], 0);

					wait(1/8 * tempo);
					this.reedGate(6, 0);
					this.reedGate([0, 4, 14], 1);

					this.aitake_(\kotsu);
				},
				bo: {
					wait(5/16 * tempo);
					this.reedGate([12, 5], 0);

					wait(1/8 * tempo);
					this.reedGate(6, 0);
					this.reedGate([3, 4, 14], 1);

					this.aitake_(\bo);
				},
				ichi: {
					wait(5/16 * tempo);
					this.reedGate([5, 12], 0);

					wait(1/8 * tempo);
					this.reedGate(6, 0);
					this.reedGate([1, 3, 4, 14], 1);

					this.aitake_(\ichi)
				},
				ku: {
					wait(1/4 * tempo);
					this.reedGate(13, 0);

					wait(1/16 * tempo);
					this.reedGate([12, 5, 6], 0);
					
					wait(1/8 * tempo);
					this.reedGate([3, 7, 2, 4], 1);

					this.aitake_(\ku)
				},
			),
			ju: (
				ju_II: {
					wait(7/16 * tempo);
					this.reedGate(5, 1);

					this.aitake_(\ju_II);
				},
				otsu: {
					wait(5/16 * tempo);
					this.reedGate(6, 0);

					wait(1/8 * tempo);
					this.reedGate([4, 14], 1);

					this.aitake_(\otsu);
				},
				kotsu: {
					wait(5/16 * tempo);
					this.reedGate(12, 0);

					wait(1/8 * tempo);
					this.reedGate(6, 0);
					this.reedGate([0, 4, 14], 1);

					this.aitake_(\kotsu);
				},
				bo: {
					wait(5/16 * tempo);
					this.reedGate(12, 0);

					wait(1/8 * tempo);
					this.reedGate(6, 0);
					this.reedGate([3, 4, 14], 1);

					this.aitake_(\bo);
				},
				ichi: {
					wait(5/16 * tempo);
					this.reedGate([12, 13], 0);

					wait(1/8 * tempo);
					this.reedGate(6, 0);
					this.reedGate([1, 3, 4, 14], 1);

					this.aitake_(\ichi)
				},
				hi: {
					wait(5/16 * tempo);
					this.reedGate(6, 0);

					wait(1/8 * tempo);
					this.reedGate([10, 14], 1);

					this.aitake_(\hi);
				},
				ge: {
					wait(5/16 * tempo);
					this.reedGate(13, 0);

					wait(1/8 * tempo);
					this.reedGate(6, 0);
					this.reedGate([6, 5, 14], 1);

					this.aitake_(\ge)
				},
			),
			bi: (
				ge: {
					wait(5/16 * tempo);
					this.reedGate(10, 0);

					wait(1/8 * tempo);
					this.reedGate(5, 1);

					this.aitake_(\ge);
				},
				otsu: {
					wait(5/16 * tempo);
					this.reedGate(10, 0);

					wait(1/8 * tempo);
					this.reedGate(7, 0);
					this.reedGate(4, 1);

					wait(5/16 * tempo);
					this.reedGate(13, 1);

					this.aitake_(\otsu);
				},
				gyo: {
					wait(5/16 * tempo);
					this.reedGate(7, 0);

					wait(1/8 * tempo);
					this.reedGate(10, 0);
					this.reedGate(13, 1);

					this.aitake_(\gyo);
				},
				bo: {
					wait(5/16 * tempo);
					this.reedGate([12, 10], 0);

					wait(1/8 * tempo);
					this.reedGate(7, 0);
					this.reedGate([3, 4], 1);

					wait(5/16 * tempo);
					this.reedGate(13, 1);

					this.aitake_(\bo);
				},
				kotsu: {
					wait(5/16 * tempo);
					this.reedGate([10, 12], 0);

					wait(1/8 * tempo);
					this.reedGate(7, 0);
					this.reedGate([0, 4], 1);

					wait(5/16 * tempo);
					this.reedGate(13, 1);

					this.aitake_(\kotsu);
				},
				ju_II: {
					wait(1/4 * tempo);
					this.reedGate(14, 0);

					wait(1/16 * tempo);
					this.reedGate([7, 10], 0);

					wait(1/8 * tempo);
					this.reedGate([13, 5, 6], 1);

					this.aitake_(\ju_II);
				},
			),
			bo: (
				bi: {
					wait(5/16 * tempo);
					this.reedGate([3, 13], 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([7, 12, 10], 1);
					
					this.aitake_(\bi);
				},
				ju: {
					wait(5/16 * tempo);
					this.reedGate([3, 14], 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([12, 6], 1);

					this.aitake_(\ju);
				},
				ju_II: {
					wait(5/16 * tempo);
					this.reedGate([3, 14], 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([12, 5, 6], 1);

					this.aitake_(\ju_II);
				},
				gyo: {
					wait(5/16 * tempo);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate(12, 1);

					this.aitake_(\gyo);
				},
				kotsu: {
					wait(5/16 * tempo);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(0, 1);

					this.aitake_(\kotsu);
				},
				otsu: {
					wait(5/16 * tempo);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(12, 1);

					this.aitake_(\otsu);
				},
				ichi: {
					wait(5/16 * tempo);
					this.reedGate(13, 0);

					wait(1/8 * tempo);
					this.reedGate(1, 1);

					this.aitake_(\ichi);
				},
				ku: {
					wait(5/16 * tempo);
					this.reedGate([13, 14], 0);

					wait(1/8 * tempo);
					this.reedGate([7, 2], 1);

					this.aitake_(\ku)
				},
				hi: {
					wait(5/16 * tempo);
					this.reedGate(3, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([10, 12], 1);

					this.aitake_(\hi);
				},
				ge: {
					wait(5/16 * tempo);
					this.reedGate([3, 13], 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([5, 7, 12], 1);

					this.aitake_(\ge);
				},
			),
			hi: (
				otsu: {
					wait(5/16 * tempo);
					this.reedGate(10, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 1);

					this.aitake_(\otsu);
				},
				ju_II: {
					wait(5/16 * tempo);
					this.reedGate([10, 14], 0);

					wait(1/8 * tempo);
					this.reedGate([5, 6], 1);

					this.aitake_(\ju_II)
				},
				ju: {
					wait(5/16 * tempo);
					this.reedGate([10, 14], 0);

					wait(1/8 * tempo);
					this.reedGate(6, 1);

					this.aitake_(\ju)
				},
			),
			otsu: (
				bo: {
					wait(5/16 * tempo);
					this.reedGate(12, 0);

					wait(1/8 * tempo);
					this.reedGate(3, 1);

					this.aitake_(\bo);
				},
				hi: {
					wait(7/16 * tempo);
					this.reedGate(4, 0);
					this.reedGate(10, 1);

					this.aitake_(\hi);
				},
				gyo: {
					wait(7/16 * tempo);
					this.reedGate(4, 0);

					this.aitake_(\gyo);
				},
				kotsu: {
					wait(5/16 * tempo);
					this.reedGate(12, 0);

					wait(1/8 * tempo);
					this.reedGate(3, 1);

					this.aitake_(\kotsu);
				},
				ju: {
					wait(5/16 * tempo);
					this.reedGate(14, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate(6, 1);

					this.aitake_(\ju)
				},
				ju_II: {
					wait(5/16 * tempo);
					this.reedGate(14, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([5, 6], 1);

					this.aitake_(\ju_II)
				},
				ichi: {
					wait(5/16 * tempo);
					this.reedGate([12, 13], 0);

					wait(1/8 * tempo);
					this.reedGate([1, 3], 1);

					this.aitake_(\ichi)
				},
				ge: {
					wait(5/16 * tempo);
					this.reedGate(13, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([5, 7], 1);

					this.aitake_(\ge)
				},
				bi: {
					wait(5/16 * tempo);
					this.reedGate(13, 0);

					wait(1/8 * tempo);
					this.reedGate(4, 0);
					this.reedGate([7, 10], 1);

					this.aitake_(\bi)
				},
				ku: {
					wait(1/4 * tempo);
					this.reedGate(14, 0);

					wait(1/16 * tempo);
					this.reedGate([12, 13], 0);

					wait(1/8 * tempo);
					this.reedGate([3, 7, 2], 1);

					this.aitake_(\ku)
				},
			),
			hi: (
				ge: {
					wait(9/32 * tempo);
					this.reedGate(13, 0);

					wait(1/32 * tempo);
					this.reedGate(10, 0);

					wait(1/8 * tempo);
					this.reedGate([5, 7], 1);

					this.aitake_(\ge);
				},
				kotsu: {
					wait(9/32 * tempo);
					this.reedGate(12, 0);

					wait(1/32 * tempo);
					this.reedGate(10, 0);

					wait(1/8 * tempo);
					this.reedGate([0, 4], 1);

					this.aitake_(\kotsu);
				},
				bo: {
					wait(9/32 * tempo);
					this.reedGate(12, 0);

					wait(1/32 * tempo);
					this.reedGate(10, 0);

					wait(1/8 * tempo);
					this.reedGate([3, 4], 1);

					this.aitake_(\bo)
				},
				ichi: {
					wait(9/32 * tempo);
					this.reedGate(12, 0);

					wait(1/32 * tempo);
					this.reedGate(10, 0);

					wait(1/8 * tempo);
					this.reedGate([1, 3, 4], 1);

					this.aitake_(\ichi)
				},
			),
			gyo: (
				otsu: {
					wait(7/16 * tempo);
					this.reedGate(4, 1);

					this.aitake_(\otsu);
				},
				bi: {
					wait(5/16 * tempo);
					this.reedGate(13, 0);

					wait(1/8 * tempo);
					this.reedGate([7, 10], 1);

					this.aitake_(\bi)
				},
				ge: {
					wait(5/16 * tempo);
					this.reedGate(13, 0);

					wait(1/8 * tempo);
					this.reedGate([5, 7], 1);

					this.aitake_(\ge);

				},
				ichi: {
					wait(9/32 * tempo);
					this.reedGate(13, 0);

					wait(1/32 * tempo);
					this.reedGate(12, 0);

					wait(1/8 * tempo);
					this.reedGate([1, 3, 4], 1);

					this.aitake_(\ichi)
				},
				bo: {
					wait(5/16 * tempo);
					this.reedGate(12, 0);

					wait(1/8 * tempo);
					this.reedGate([3, 4], 1);

					this.aitake_(\bo)
				}
			)
		)
	}

	// * Instance method: reedGate
	reedGate {|reed = 0, gate = 0|
		var array = reed.asArray;

		array.collect{|item|
			reeds[item].set(\gate, gate);
			reedStatus[item] = gate;
		};
	}

	// * Instance method: setGate
	setGate {|gate = 1|
		switch(gate,
			1, {
				reeds.do{|item, i|
					this.reedGate(i, aitakeList[aitake][i]);
				};
				isPlaying = true;
			},
			0, {
				reeds.do{|item, i|
					this.reedGate(i, 0);
				};
				isPlaying = false;
			}
		)
	}

	setNotes {
		switch(tuning,
			'pyth', {
				notes = [
					2/3, // 0: a
					3/4, // 1: b
					27/32, // 2: c#
					8/9, // 3: d
					1, // 4: e
					9/8, // 5: f#
					32/27, // 6: g
					81/64, // 7: g#
					4/3, // 8: a
					3/2, // 9: b
					128/81, // 10: c
					27/16, // 11: c#
					16/9, // 12: d
					2, // 14: e
					9/4 // 15: f#
				];
			},
			'et12', {
				notes = [
					(-7).midiratio, // 0: a
					(-5).midiratio, // 1: b
					(-3).midiratio, // 2: c#
					(-2).midiratio, // 3: d
					1, // 4: e
					2.midiratio, // 5: f#
					3.midiratio, // 6: g
					4.midiratio, // 7: g#
					5.midiratio, // 8: a
					7.midiratio, // 9: b
					8.midiratio, // 10: c
					9.midiratio, // 11: c#
					10.midiratio, // 12: d
					2, // 14: e
					14.midiratio // 15: f#
				]
			},
			{
				notes = [
					2/3, // 0: a
					3/4, // 1: b
					27/32, // 2: c#
					8/9, // 3: d
					1, // 4: e
					9/8, // 5: f#
					32/27, // 6: g
					81/64, // 7: g#
					4/3, // 8: a
					3/2, // 9: b
					128/81, // 10: c
					27/16, // 11: c#
					16/9, // 12: d
					2, // 14: e
					9/4 // 15: f#
				];
			}
		);
	}

	amp_ {|newAmp = 0.2|
		amp = newAmp;
		reeds.do(_.set(\amp, amp))
	}

	root_ {|newRoot|
		root = newRoot; 
		reeds.do{|item, i|
			item.set(\freq, root * notes[i])
		};
	}

	tuning_{|newTuning|
		tuning = newTuning;
		this.setNotes();
		reeds.do{|item, i|
			item.set(\freq, root * notes[i])
		}
	}
	
	set { arg ...args;
		if(args.size % 2 == 0, {
			var numPairs = (args.size / 2).asInteger;
			
			var pairs = numPairs.collect{|index|
				var obj = (key: args[index * 2], value: args[index * 2 + 1]);

				obj.postln;
				
				obj;
			};

			pairs.do{|item|
				switch(item.key,
					\gate, {
						"Setting gate".postln;
						this.setGate(item.value)
					},
					\aitake, {this.aitake_(item.value.asSymbol)},
					{("No such key, " ++ item.key).postln}
				)
			}
		}, {
			"Arguments should be key/value pairs!".postln
		});
	}

	aitakeList {
		var msg = "Available aitakes: kotsu, ichi, ku, bo, otsu, ge, ju, ju_II, bi, gyo, hi.";

		msg.postln;
	}

	aitakeRef {|withEmacs=false|
		var path = Platform.userExtensionDir ++ "/mh-sho/";
		var cmd = if(withEmacs, {
			"emacsclient -n"
		}, {
			"open"
		});

		Platform.case(
			\osx, {
				"Opening aitake reference".postln;
				(cmd ++ " " ++ (path ++ "contents/sho-aitake.png").shellQuote).unixCmd;
			},
			\window, {"Not implemented in Window!"},
			\linux, {"Not implemented in Linux!"}
		);
	}
}

// Local Variables:
// eval: (outshine-mode 1)
// End: