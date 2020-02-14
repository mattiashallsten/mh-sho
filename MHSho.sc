/*
##### TODO #####

- Change the routines into functions, only call Routine(function) in the teutsuri method
*/

MHSho {
	var <root, <>tempo, numPartials;
	var <aitake = \kotsu, aitakeList, teutsuriList, notes, reeds, isPlaying=false;
	var reedStatus;

	*new {|root = 240, tempo = 1, numPartials = 8|
		^super.newCopyArgs(root, tempo, numPartials).initSho
	}

	initSho {

		fork {
		
			SynthDef(\mh_sho, {|gate=0, freq=440, atk=3, rel=0.3, amp=0.2, out=0, harmonics=0|
				var env = EnvGen.kr(Env.asr(atk, 1, rel), gate);
				var sig = Mix.ar(numPartials.collect{|i|
					SinOsc.ar(freq.lag(0.1) * (i+1)) / (i + 1);
				});

				sig = sig * env * amp.lag(1.0) * 0.7;

				Out.ar(out,sig!2)
			}).add;

			wait(0.1);

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
				2, // 14: d
				9/4 // 15: e
			];

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

			reeds = notes.collect{|item, i|
				Synth(\mh_sho, [
					\freq, root * notes[i],
					\gate, 0,
					\amp, 0.1,
					\out, 0,
					\atk, 0.3,
					\rel, 0.5,
				])
			};

			reedStatus = reeds.collect{
				0
			};

			teutsuriList = (
				ichi: (
					bo: Routine {
						wait(5/16 * tempo);
						this.reedGate(1, 0);
				
						wait(1/8 * tempo);
						this.reedGate(13, 0);

						this.aitake_(\bo);
					},
					kotsu: Routine {
						wait(5/16 * tempo);
						this.reedGate(1, 0);
						this.reedGate(3, 0);

						wait(1/8 * tempo);
						this.reedGate(0, 1);
						this.reedGate(13, 1);

						this.aitake_(\kotsu);
					},
					otsu: Routine {
						wait(5/16 * tempo);
						this.reedGate(1, 0);
						this.reedGate(3, 0);
						
						wait(1/8 * tempo);
						this.reedGate(12, 0);
						this.reedGate(13, 0);

						this.aitake_(\otsu);
					},
					ku: Routine {
						wait(5/16 * tempo);
						this.reedGate(1, 0);
						this.reedGate(14, 0);

						wait(1/8 * tempo);
						this.reedGate(7, 1);
						this.reedGate(2, 1);

						this.aitake_(\ku);
					},
					gyo: Routine {
						wait(5/16 * tempo);
						this.reedGate(1, 0);
						this.reedGate(3, 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate(12, 1);
						this.reedGate(13, 1);

						this.aitake_(\gyo);
					},
					hi: Routine {
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
					ge: Routine {
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
					bi: Routine {
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
					ju_II: Routine {
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
					ju: Routine {
						wait(1/4 * tempo);
						this.reedGate(14, 0);

						wait(1/16 * tempo);
						this.reedGate([1, 3], 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate([12, 13, 6], 1);

						this.aitake_(\ju);
					},
					kotsu: Routine {
						wait(5/16 * tempo);
						this.reedGate(3, 0);

						wait(1/8 * tempo);
						this.reedGate(1, 0);
						this.reedGate([0, 13], 1);

						this.aitake_(\kotsu);
					},
				),
				kotsu: (
					bo: Routine {
						wait(5/16 * tempo);
						this.reedGate(0, 0);

						wait(1/8 * tempo);
						this.reedGate(3, 1);

						this.aitake_(\bo);
					},
					otsu: Routine {
						wait(5/16 * tempo);
						this.reedGate(0, 0);

						wait(1/8 * tempo);
						this.reedGate(12, 1);

						this.aitake_(\otsu);
					},
					ichi: Routine {
						wait(5/16 * tempo);
						this.reedGate([0, 13], 0);

						wait(1/8 * tempo);
						this.reedGate([1,3], 1);

						this.aitake_(\ichi);
					},
					hi: Routine {
						wait(5/16 * tempo);
						this.reedGate(0, 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate([12, 10], 1);

						this.aitake_(\hi);
					},
					ge: Routine {
						wait(5/16 * tempo);
						this.reedGate([0, 13], 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate([5, 7, 12], 1);

						this.aitake_(\ge);
					},
					bi: Routine {
						wait(5/16 * tempo);
						this.reedGate([0, 13], 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate([7, 10, 12], 1);

						this.aitake_(\bi)
					},
					
				),
				ku: (
					bo: Routine {
						wait(5/16 * tempo);
						this.reedGate(7, 0);

						wait(1/8 * tempo);
						this.reedGate(2, 0);
						this.reedGate(13, 1);

						wait(5/16 * tempo);
						this.reedGate(14, 1);

						this.aitake_(\bo);
					},
					ichi: Routine {
						wait(5/16 * tempo);
						this.reedGate(7, 0);

						wait(1/8 * tempo);
						this.reedGate(2, 0);
						this.reedGate(1, 1);

						wait(5/16 * tempo);
						this.reedGate(14, 1);

						this.aitake_(\ichi);
					},
					otsu: Routine {
						wait(5/16 * tempo);
						this.reedGate([3, 7], 0);

						wait(1/8 * tempo);
						this.reedGate(2, 0);
						this.reedGate([12, 13], 1);

						wait(5/16 * tempo);
						this.reedGate(14, 1);

						this.aitake_(\otsu);
					},
					kotsu: Routine {
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
					ge: Routine {
						wait(5/16 * tempo);
						this.reedGate([3, 2], 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate([12, 5, 14], 1);

						this.aitake_(\ge);
					},
					bi: Routine {
						wait(5/16 * tempo);
						this.reedGate([3, 2], 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate([12, 10, 14], 1);

						this.aitake_(\bi);
					},
					hi: Routine {
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
					bi: Routine {
						wait(7/16 * tempo);
						this.reedGate(5, 0);
						this.reedGate(10, 0);

						this.aitake_(\bi);
					},
					ju_II: Routine {
						wait(5/16 * tempo);
						this.reedGate([7, 14], 0);

						wait(1/8 * tempo);
						this.reedGate([6, 13], 1);

						this.aitake_(\ju_II);
					},
					otsu: Routine {
						wait(5/16 * tempo);
						this.reedGate(5, 0);

						wait(1/8 * tempo);
						this.reedGate(7, 0);
						this.reedGate(4, 1);

						wait(5/16 * tempo);
						this.reedGate(13, 1);

						this.aitake_(\otsu);
					},
					hi: Routine {
						wait(5/16 * tempo);
						this.reedGate(7, 0);

						wait(1/8 * tempo);
						this.reedGate(5, 0);
						this.reedGate([10, 13], 1);

						this.aitake_(\hi);
					},
					gyo: Routine {
						wait(7/16 * tempo);
						this.reedGate([5, 7], 0);
						this.reedGate(13, 1);
						
						this.aitake_(\gyo);
					},
					kotsu: Routine {
						wait(5/16 * tempo);
						this.reedGate([5, 12], 0);

						wait(1/8 * tempo);
						this.reedGate(7, 0);
						this.reedGate([0, 4], 1);

						wait(5/16 * tempo);
						this.reedGate(13, 1);

						this.aitake_(\kotsu);
					},
					bo: Routine {
						wait(5/16 * tempo);
						this.reedGate([5, 12], 0);

						wait(1/8 * tempo);
						this.reedGate(7, 0);
						this.reedGate([3, 4], 1);

						wait(5/16 * tempo);
						this.reedGate(13, 1);

						this.aitake_(\bo);
					},
					ichi: Routine {
						wait(1/4 * tempo);
						this.reedGate(12, 0);
						
						wait(1/16 * tempo);
						this.reedGate([7, 5], 0);

						wait(1/8 * tempo);
						this.reedGate([1, 3, 4], 1);

						this.aitake_(\ichi);
					},
					ku: Routine {
						wait(1/4 * tempo);
						this.reedGate(14, 0);

						wait(1/16 * tempo);
						this.reedGate([12, 5], 0);

						wait(1/8 * tempo);
						this.reedGate([3, 1, 4], 1);

						this.aitake_(\ku);
					},
					ju: Routine {
						wait(5/16 * tempo);
						this.reedGate([7, 14], 0);

						wait(1/8 * tempo);
						this.reedGate(5, 0);
						this.reedGate([6, 13], 1);

						this.aitake_(\ju)
					},
				),
				ju_II: (
					ju: Routine {
						wait(7/16 * tempo);
						this.reedGate(5, 0);

						this.aitake_(\ju);
					},
					otsu: Routine {
						wait(5/16 * tempo);
						this.reedGate(5, 0);

						wait(1/8 * tempo);
						this.reedGate(6, 0);
						this.reedGate([4, 14], 1);

						this.aitake_(\otsu);
					},
					hi: Routine {
						wait(5/16 * tempo);
						this.reedGate(5, 0);

						wait(1/8 * tempo);
						this.reedGate(6, 0);
						this.reedGate([10, 14], 1);

						this.aitake_(\hi);
					},
					ge: Routine {
						wait(5/16 * tempo);
						this.reedGate(13, 0);

						wait(1/8 * tempo);
						this.reedGate(6, 0);
						this.reedGate([7, 14], 1);

						this.aitake_(\ge);
					},
					kotsu: Routine {
						wait(5/16 * tempo);
						this.reedGate([5, 12], 0);

						wait(1/8 * tempo);
						this.reedGate(6, 0);
						this.reedGate([0, 4, 14], 1);

						this.aitake_(\kotsu);
					},
					bo: Routine {
						wait(5/16 * tempo);
						this.reedGate([12, 5], 0);

						wait(1/8 * tempo);
						this.reedGate(6, 0);
						this.reedGate([3, 4, 14], 1);

						this.aitake_(\bo);
					},
					ichi: Routine {
						wait(5/16 * tempo);
						this.reedGate([5, 12], 0);

						wait(1/8 * tempo);
						this.reedGate(6, 0);
						this.reedGate([1, 3, 4, 14], 1);

						this.aitake_(\ichi)
					},
					ku: Routine {
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
					ju_II: Routine {
						wait(7/16 * tempo);
						this.reedGate(5, 1);

						this.aitake_(\ju_II);
					},
					otsu: Routine {
						wait(5/16 * tempo);
						this.reedGate(6, 0);

						wait(1/8 * tempo);
						this.reedGate([4, 14], 1);

						this.aitake_(\otsu);
					},
					kotsu: Routine {
						wait(5/16 * tempo);
						this.reedGate(12, 0);

						wait(1/8 * tempo);
						this.reedGate(6, 0);
						this.reedGate([0, 4, 14], 1);

						this.aitake_(\kotsu);
					},
					bo: Routine {
						wait(5/16 * tempo);
						this.reedGate(12, 0);

						wait(1/8 * tempo);
						this.reedGate(6, 0);
						this.reedGate([3, 4, 14], 1);

						this.aitake_(\bo);
					},
					ichi: Routine {
						wait(5/16 * tempo);
						this.reedGate([12, 13], 0);

						wait(1/8 * tempo);
						this.reedGate(6, 0);
						this.reedGate([1, 3, 4, 14], 1);

						this.aitake_(\ichi)
					},
					hi: Routine {
						wait(5/16 * tempo);
						this.reedGate(6, 0);

						wait(1/8 * tempo);
						this.reedGate([10, 14], 1);

						this.aitake_(\hi);
					},
					ge: Routine {
						wait(5/16 * tempo);
						this.reedGate(13, 0);

						wait(1/8 * tempo);
						this.reedGate(6, 0);
						this.reedGate([6, 5, 14], 1);

						this.aitake_(\ge)
					},
				),
				bi: (
					ge: Routine {
						wait(5/16 * tempo);
						this.reedGate(10, 0);

						wait(1/8 * tempo);
						this.reedGate(5, 1);

						this.aitake_(\ge);
					},
					otsu: Routine {
						wait(5/16 * tempo);
						this.reedGate(10, 0);

						wait(1/8 * tempo);
						this.reedGate(7, 0);
						this.reedGate(4, 1);

						wait(5/16 * tempo);
						this.reedGate(13, 1);

						this.aitake_(\otsu);
					},
					gyo: Routine {
						wait(5/16 * tempo);
						this.reedGate(7, 0);

						wait(1/8 * tempo);
						this.reedGate(10, 0);
						this.reedGate(13, 1);

						this.aitake_(\gyo);
					},
					bo: Routine {
						wait(5/16 * tempo);
						this.reedGate([12, 10], 0);

						wait(1/8 * tempo);
						this.reedGate(7, 0);
						this.reedGate([3, 4], 1);

						wait(5/16 * tempo);
						this.reedGate(13, 1);

						this.aitake_(\bo);
					},
					kotsu: Routine {
						wait(5/16 * tempo);
						this.reedGate([10, 12], 0);

						wait(1/8 * tempo);
						this.reedGate(7, 0);
						this.reedGate([0, 4], 1);

						wait(5/16 * tempo);
						this.reedGate(13, 1);

						this.aitake_(\kotsu);
					},
					ju_II: Routine {
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
					bi: Routine {
						wait(5/16 * tempo);
						this.reedGate([3, 13], 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate([7, 12, 10], 1);
						
						this.aitake_(\bi);
					},
					ju: Routine {
						wait(5/16 * tempo);
						this.reedGate([3, 14], 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate([12, 6], 1);

						this.aitake_(\ju);
					},
					ju_II: Routine {
						wait(5/16 * tempo);
						this.reedGate([3, 14], 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate([12, 5, 6], 1);

						this.aitake_(\ju_II);
					},
					gyo: Routine {
						wait(5/16 * tempo);
						this.reedGate(3, 0);

						wait(1/8 * tempo);
						this.reedGate(4, 0);
						this.reedGate(12, 1);

						this.aitake_(\gyo);
					},
				),
				hi: (
					otsu: Routine {
						wait(5/16 * tempo);
						this.reedGate(10, 0);

						wait(1/8 * tempo);
						this.reedGate(4, 1);

						this.aitake_(\otsu);
					},
					ju_II: Routine {
						wait(5/16 * tempo);
						this.reedGate([10, 14], 0);

						wait(1/8 * tempo);
						this.reedGate([5, 6], 1);

						this.aitake_(\ju_II)
					},
					ju: Routine {
						wait(5/16 * tempo);
						this.reedGate([10, 14], 0);

						wait(1/8 * tempo);
						this.reedGate(6, 1);

						this.aitake_(\ju)
					},
				)	
			)
		}

	}

	aitake_ {|newAitake = \kotsu|
		if(aitakeList[newAitake].notNil, {
			aitake = newAitake;
			this.setGate(1);
			isPlaying = true;
		}, {
			"Aitake does not exist!".postln
		});
	}

	teutsuri {|newAitake = \kotsu|
		if(isPlaying,
			{
				if(teutsuriList[aitake].notNil,
					{
						if(teutsuriList[aitake][newAitake].notNil,
							{
								fork {
									// make sure everything's correct with the selected aitake
									this.aitake_(aitake);
									
									// play the routine for changing to the new aitake
									teutsuriList[aitake][newAitake].play;

									// reset all the routines
									teutsuriList.do{|item|
										if(item.notNil,
											{
												item.do{|routine|
													if(routine.notNil,
														{
															routine.reset
														}
													);
												}
											}
										);
									};
								}
							},
							{
								"Te-utsuri not implemented yet/not possible".postln
							}
						)
					},
					{
						"Te-utsuri not implemented yet/not possible.".postln
					}
				);
			},
			{
				this.aitake_(newAitake)
			}
		);
	}

	reedGate {|reed = 0, gate = 0|
		var array = reed.asArray;

		array.collect{|item|
			reeds[item].set(\gate, gate);
			reedStatus[item] = gate;
		};
	}
	
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

	root_ {|newRoot|
		root = newRoot; 
		reeds.do{|item, i|
			item.set(\freq, root * notes[i])};
	}
		
}