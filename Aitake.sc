Aitake {
	bySymbol {|symbol|
		^switch(symbol,
			\ju, {this.ju},
			\ju_ii, {this.ju_II},
			\ge, {this.ge},
			\otsu, {this.otsu},
			\ku, {this.ku},
			\bi, {this.bi},
			\ichi, {this.ichi},
			\gyo, {this.gyo},
			\bo, {this.bo},
			\kotsu, {this.kotsu},
			\hi, {this.hi}
		)
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
	
	ju {
		^[
			32/27, // 6: g
			4/3, // 8: a
			3/2, // 9: b
			16/9, // 12: d
			9/4 // 15: e
		]
	}
	ju_II {
		^[
			9/8, // 5: f#
			32/27, // 6: g
			4/3, // 8: a
			3/2, // 9: b
			16/9, // 12: d
			9/4 // 15: e
		]
	}
	ge {
		^[
			9/8, // 5: f#
			81/64, // 7: g#
			4/3, // 8: a
			3/2, // 9: b
			16/9, // 12: d
			9/4 // 15: f#
		]
	}
	otsu {
		^[
			1, // 4: e
			4/3, // 8: a
			3/2, // 9: b
			16/9, // 12: d
			2, // 14: e
			9/4 // 15: f#
		]
	}
	ku {
		^[
			27/32, // 2: c#
			8/9, // 3: d
			1, // 4: e
			81/64, // 7: g#
			4/3, // 8: a
			3/2, // 9: b
		]
	}
	bi {
		^[
			81/64, // 7: g#
			4/3, // 8: a
			3/2, // 9: b
			128/81, // 10: c
			16/9, // 12: d
			9/4 // 15: f#
		]
	}
	ichi {
		^[
			3/4, // 1: b
			8/9, // 3: d
			1, // 4: e
			4/3, // 8: a
			3/2, // 9: b
			9/4 // 15: f#
		]
	}
	gyo {
		^[
			4/3, // 8: a
			3/2, // 9: b
			16/9, // 12: d
			2, // 14: e
			9/4 // 15: f#
		]
	}
	bo {
		^[
			8/9, // 3: d
			1, // 4: e
			4/3, // 8: a
			3/2, // 9: b
			2, // 14: e
			9/4 // 15: f#
		]
	}
	kotsu {
		^[
			2/3, // 0: a
			1, // 4: e
			4/3, // 8: a
			3/2, // 9: b
			2, // 14: e
			9/4 // 15: f#
		]
	}
	hi {
		^[
			4/3, // 8: a
			3/2, // 9: b
			128/81, // 10: c
			16/9, // 12: d
			2, // 14: e
			9/4 // 15: f#
		]
	}
	
}