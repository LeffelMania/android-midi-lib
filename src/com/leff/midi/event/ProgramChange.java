//////////////////////////////////////////////////////////////////////////////
//	Copyright 2011 Alex Leffelman
//	
//	Licensed under the Apache License, Version 2.0 (the "License");
//	you may not use this file except in compliance with the License.
//	You may obtain a copy of the License at
//	
//	http://www.apache.org/licenses/LICENSE-2.0
//	
//	Unless required by applicable law or agreed to in writing, software
//	distributed under the License is distributed on an "AS IS" BASIS,
//	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//	See the License for the specific language governing permissions and
//	limitations under the License.
//////////////////////////////////////////////////////////////////////////////

package com.leff.midi.event;

public class ProgramChange extends ChannelEvent
{
    public ProgramChange(long tick, int channel, int program)
    {
        super(tick, ChannelEvent.PROGRAM_CHANGE, channel, program, 0);
    }

    public ProgramChange(long tick, long delta, int channel, int program)
    {
        super(tick, delta, ChannelEvent.PROGRAM_CHANGE, channel, program, 0);
    }

    public int getProgramNumber()
    {
        return mValue1;
    }

    public void setProgramNumber(int p)
    {
        mValue1 = p;
    }

    public enum MidiProgram
    {
        ACOUSTIC_GRAND_PIANO, BRIGHT_ACOUSTIC_PIANO, ELECTRIC_GRAND_PIANO, HONKYTONK_PIANO, ELECTRIC_PIANO_1, ELECTRIC_PIANO_2, HARPSICHORD, CLAVINET, CELESTA, GLOCKENSPIEL, MUSIC_BOX, VIBRAPHONE, MARIMBA, XYLOPHONE, TUBULAR_BELLS, DULCIMER, DRAWBAR_ORGAN, PERCUSSIVE_ORGAN, ROCK_ORGAN, CHURCH_ORGAN, REED_ORGAN, ACCORDION, HARMONICA, TANGO_ACCORDION, ACOUSTIC_GUITAR_NYLON, ACOUSTIC_GUITAR_STEEL, ELECTRIC_GUITAR_JAZZ, ELECTRIC_GUITAR_CLEAN, ELECTRIC_GUITAR_MUTED, OVERDRIVEN_GUITAR, DISTORTION_GUITAR, GUITAR_HARMONICS, ACOUSTIC_BASS, ELECTRIC_BASS_FINGER, ELECTRIC_BASS_PICK, FRETLESS_BASS, SLAP_BASS_1, SLAP_BASS_2, SYNTH_BASS_1, SYNTH_BASS_2, VIOLIN, VIOLA, CELLO, CONTRABASS, TREMOLO_STRINGS, PIZZICATO_STRINGS, ORCHESTRAL_HARP, TIMPANI, STRING_ENSEMBLE_1, STRING_ENSEMBLE_2, SYNTH_STRINGS_1, SYNTH_STRINGS_2, CHOIR_AAHS, VOICE_OOHS, SYNTH_CHOIR, ORCHESTRA_HIT, TRUMPET, TROMBONE, TUBA, MUTED_TRUMPET, FRENCH_HORN, BRASS_SECTION, SYNTH_BRASS_1, SYNTH_BRASS_2, SOPRANO_SAX, ALTO_SAX, TENOR_SAX, BARITONE_SAX, OBOE, ENGLISH_HORN, BASSOON, CLARINET, PICCOLO, FLUTE, RECORDER, PAN_FLUTE, BLOWN_BOTTLE, SHAKUHACHI, WHISTLE, OCARINA, LEAD_1_SQUARE, LEAD_2_SAWTOOTH, LEAD_3_CALLIOPE, LEAD_4_CHIFF, LEAD_5_CHARANG, LEAD_6_VOICE, LEAD_7_FIFTHS, LEAD_8_BASS_AND_LEAD, PAD_1_NEW_AGE, PAD_2_WARM, PAD_3_POLYSYNTH, PAD_4_CHOIR, PAD_5_BOWED, PAD_6_METALLIC, PAD_7_HALO, PAD_8_SWEEP, FX_1_RAIN, FX_2_SOUNDTRACK, FX_3_CRYSTAL, FX_4_ATMOSPHERE, FX_5_BRIGHTNESS, FX_6_GOBLINS, FX_7_ECHOES, FX_8_SCIFI, SITAR, BANJO, SHAMISEN, KOTO, KALIMBA, BAGPIPE, FIDDLE, SHANAI, TINKLE_BELL, AGOGO, STEEL_DRUMS, WOODBLOCK, TAIKO_DRUM, MELODIC_TOM, SYNTH_DRUM, REVERSE_CYMBAL, GUITAR_FRET_NOISE, BREATH_NOISE, SEASHORE, BIRD_TWEET, TELEPHONE_RING, HELICOPTER, APPLAUSE, GUNSHOT;

        public int programNumber()
        {
            return this.ordinal() + 1;
        }
    }
}
