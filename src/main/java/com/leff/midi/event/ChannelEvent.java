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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class ChannelEvent extends MidiEvent
{
    protected int mType;
    protected int mChannel;
    protected int mValue1;
    protected int mValue2;

    private static HashMap<Integer, Integer> mOrderMap;

    protected ChannelEvent(long tick, int type, int channel, int param1, int param2)
    {
        this(tick, 0, type, channel, param1, param2);
    }

    protected ChannelEvent(long tick, long delta, int type, int channel, int param1, int param2)
    {
        super(tick, delta);

        mType = type & 0x0F;
        mChannel = channel & 0x0F;
        mValue1 = param1 & 0xFF;
        mValue2 = param2 & 0xFF;
    }

    public int getType()
    {
        return mType;
    }

    public void setChannel(int c)
    {
        if(c < 0)
        {
            c = 0;
        }
        else if(c > 15)
        {
            c = 15;
        }
        mChannel = c;
    }

    public int getChannel()
    {
        return mChannel;
    }

    protected int getEventSize()
    {
        switch(mType)
        {
            case PROGRAM_CHANGE:
            case CHANNEL_AFTERTOUCH:
                return 2;
            default:
                return 3;
        }
    }

    @Override
    public int compareTo(MidiEvent other)
    {
        if(mTick != other.getTick())
        {
            return mTick < other.getTick() ? -1 : 1;
        }
        if(mDelta.getValue() != other.mDelta.getValue())
        {
            return mDelta.getValue() < other.mDelta.getValue() ? 1 : -1;
        }

        if(!(other instanceof ChannelEvent))
        {
            return 1;
        }

        ChannelEvent o = (ChannelEvent) other;
        if(mType != o.getType())
        {
            if(mOrderMap == null)
            {
                buildOrderMap();
            }

            int order1 = mOrderMap.get(mType);
            int order2 = mOrderMap.get(o.getType());

            return order1 < order2 ? -1 : 1;
        }
        if(mValue1 != o.mValue1)
        {
            return mValue1 < o.mValue1 ? -1 : 1;
        }
        if(mValue2 != o.mValue2)
        {
            return mValue2 < o.mValue2 ? -1 : 1;
        }
        if(mChannel != o.getChannel())
        {
            return mChannel < o.getChannel() ? -1 : 1;
        }
        return 0;
    }

    @Override
    public boolean requiresStatusByte(MidiEvent prevEvent)
    {
        if(prevEvent == null)
        {
            return true;
        }
        if(!(prevEvent instanceof ChannelEvent))
        {
            return true;
        }

        ChannelEvent ce = (ChannelEvent) prevEvent;
        return !(mType == ce.getType() && mChannel == ce.getChannel());
    }

    @Override
    public void writeToFile(OutputStream out, boolean writeType) throws IOException
    {
        super.writeToFile(out, writeType);

        if(writeType)
        {
            int typeChannel = (mType << 4) + mChannel;
            out.write(typeChannel);
        }

        out.write(mValue1);
        if(mType != PROGRAM_CHANGE && mType != CHANNEL_AFTERTOUCH)
        {
            out.write(mValue2);
        }
    }

    public static ChannelEvent parseChannelEvent(long tick, long delta, int type, int channel, InputStream in) throws IOException
    {
        int val1 = in.read();
        int val2 = 0;
        if(type != PROGRAM_CHANGE && type != CHANNEL_AFTERTOUCH)
        {
            val2 = in.read();
        }

        switch(type)
        {
            case NOTE_OFF:
                return new NoteOff(tick, delta, channel, val1, val2);
            case NOTE_ON:
                return new NoteOn(tick, delta, channel, val1, val2);
            case NOTE_AFTERTOUCH:
                return new NoteAftertouch(tick, delta, channel, val1, val2);
            case CONTROLLER:
                return new Controller(tick, delta, channel, val1, val2);
            case PROGRAM_CHANGE:
                return new ProgramChange(tick, delta, channel, val1);
            case CHANNEL_AFTERTOUCH:
                return new ChannelAftertouch(tick, delta, channel, val1);
            case PITCH_BEND:
                return new PitchBend(tick, delta, channel, val1, val2);
            default:
                return new ChannelEvent(tick, delta, type, channel, val1, val2);
        }
    }

    private static void buildOrderMap()
    {

        mOrderMap = new HashMap<Integer, Integer>();

        mOrderMap.put(PROGRAM_CHANGE, 0);
        mOrderMap.put(CONTROLLER, 1);
        mOrderMap.put(NOTE_ON, 2);
        mOrderMap.put(NOTE_OFF, 3);
        mOrderMap.put(NOTE_AFTERTOUCH, 4);
        mOrderMap.put(CHANNEL_AFTERTOUCH, 5);
        mOrderMap.put(PITCH_BEND, 6);
    }

    public static final int NOTE_OFF = 0x8;
    public static final int NOTE_ON = 0x9;
    public static final int NOTE_AFTERTOUCH = 0xA;
    public static final int CONTROLLER = 0xB;
    public static final int PROGRAM_CHANGE = 0xC;
    public static final int CHANNEL_AFTERTOUCH = 0xD;
    public static final int PITCH_BEND = 0xE;
}
