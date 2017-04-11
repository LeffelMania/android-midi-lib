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

package com.leff.midi.event.meta;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.leff.midi.event.MidiEvent;
import com.leff.midi.util.VariableLengthInt;

public abstract class MetaEvent extends MidiEvent
{
    protected int mType;
    protected VariableLengthInt mLength;

    protected MetaEvent(long tick, long delta, int type, VariableLengthInt length)
    {
        super(tick, delta);

        mType = type & 0xFF;
        mLength = length;
    }

    protected abstract int getEventSize();

    @Override
    public void writeToFile(OutputStream out, boolean writeType) throws IOException
    {
        writeToFile(out);
    }

    protected void writeToFile(OutputStream out) throws IOException
    {
        super.writeToFile(out, true);
        out.write(0xFF);
        out.write(mType);
    }

    public static MetaEvent parseMetaEvent(long tick, long delta, InputStream in) throws IOException
    {
        MetaEventData eventData = new MetaEventData(in);

        boolean isText = false;
        switch(eventData.type)
        {
            case SEQUENCE_NUMBER:
            case MIDI_CHANNEL_PREFIX:
            case END_OF_TRACK:
            case TEMPO:
            case SMPTE_OFFSET:
            case TIME_SIGNATURE:
            case KEY_SIGNATURE:
                break;
            case TEXT_EVENT:
            case COPYRIGHT_NOTICE:
            case TRACK_NAME:
            case INSTRUMENT_NAME:
            case LYRICS:
            case MARKER:
            case CUE_POINT:
            case SEQUENCER_SPECIFIC: // Not technically text, but follows same
                                     // structure
            default: // Also not technically text, but it should follow
                isText = true;
                break;
        }

        if(isText)
        {
            String text = new String(eventData.data);

            switch(eventData.type)
            {
                case TEXT_EVENT:
                    return new Text(tick, delta, text);
                case COPYRIGHT_NOTICE:
                    return new CopyrightNotice(tick, delta, text);
                case TRACK_NAME:
                    return new TrackName(tick, delta, text);
                case INSTRUMENT_NAME:
                    return new InstrumentName(tick, delta, text);
                case LYRICS:
                    return new Lyrics(tick, delta, text);
                case MARKER:
                    return new Marker(tick, delta, text);
                case CUE_POINT:
                    return new CuePoint(tick, delta, text);
                case SEQUENCER_SPECIFIC:
                    return new SequencerSpecificEvent(tick, delta, eventData.data);
                default:
                    return new GenericMetaEvent(tick, delta, eventData);
            }
        }

        switch(eventData.type)
        {
            case SEQUENCE_NUMBER:
                return SequenceNumber.parseSequenceNumber(tick, delta, eventData);
            case MIDI_CHANNEL_PREFIX:
                return MidiChannelPrefix.parseMidiChannelPrefix(tick, delta, eventData);
            case END_OF_TRACK:
                return new EndOfTrack(tick, delta);
            case TEMPO:
                return Tempo.parseTempo(tick, delta, eventData);
            case SMPTE_OFFSET:
                return SmpteOffset.parseSmpteOffset(tick, delta, eventData);
            case TIME_SIGNATURE:
                return TimeSignature.parseTimeSignature(tick, delta, eventData);
            case KEY_SIGNATURE:
                return KeySignature.parseKeySignature(tick, delta, eventData);
        }
        System.out.println("Completely broken in MetaEvent.parseMetaEvent()");
        return null;
    }

    protected static class MetaEventData
    {
        public final int type;
        public final VariableLengthInt length;
        public final byte[] data;

        public MetaEventData(InputStream in) throws IOException
        {
            type = in.read();
            length = new VariableLengthInt(in);
            data = new byte[length.getValue()];
            if(length.getValue() > 0)
            {
                in.read(data);
            }
        }
    }

    public static final int SEQUENCE_NUMBER = 0;
    public static final int TEXT_EVENT = 1;
    public static final int COPYRIGHT_NOTICE = 2;
    public static final int TRACK_NAME = 3;
    public static final int INSTRUMENT_NAME = 4;
    public static final int LYRICS = 5;
    public static final int MARKER = 6;
    public static final int CUE_POINT = 7;
    public static final int MIDI_CHANNEL_PREFIX = 0x20;
    public static final int END_OF_TRACK = 0x2F;
    public static final int TEMPO = 0x51;
    public static final int SMPTE_OFFSET = 0x54;
    public static final int TIME_SIGNATURE = 0x58;
    public static final int KEY_SIGNATURE = 0x59;
    public static final int SEQUENCER_SPECIFIC = 0x7F;
}
