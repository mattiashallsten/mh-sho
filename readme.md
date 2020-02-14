# MH-Shō

An adaptation of the japanese mouth-organ Shō (笙) in `SuperCollider`. 

## Installation

Clone this repository into you `sclang-extensions-dir`.

On MacOs:


``` sh
$ cd ~/Library/Application\ Support/SuperColldier/Extensions
$ git clone https://github.com/mattiashallsten/mh-sho
```

Then re-compile SuperCollider.

## Usage

Is described in detail in the help documentation. Basically:

``` sclang
s.boot;

x = MHSho(root: 500, tempo: 1, numPartials: 8);

x.aitake_(\ichi);

x.teutsuri(\kotsu);
```
