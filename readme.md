# MH-Shō

An adaptation of the Japanese mouth-organ Shō (笙) in `SuperCollider`. 

## The reeds
There are 15 playable reeds of fixed pitch, and form the following scale:

![The reeds](contents/reeds.png)

## The aitake (合竹)
There are 11 _aitake_ in total, as well as number seven (Jū) being featured twice due to it having one version with the note F# and one without -- these two versions are named `ju` and `ju_II` in SuperCollider.

Here are the aitake:

![Aitake of Gagagku](contents/sho-aitake.png)

## The te-utsuri
Moving between the _aitake_ different _te-utsuri_ are used. These are fixed and known by every Shō player. 

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

