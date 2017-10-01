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
import java.io.OutputStream;

import com.leff.midi.event.MidiEvent;
import com.leff.midi.util.VariableLengthInt;

public class SmpteOffset extends MetaEvent
{
    public static final int FRAME_RATE_24 = 0;
    public static final int FRAME_RATE_25 = 1;
    public static final int FRAME_RATE_30_DROP = 2;
    public static final int FRAME_RATE_30 = 3;

    private FrameRate mFrameRate;
    private int mHours;
    private int mMinutes;
    private int mSeconds;
    private int mFrames;
    private int mSubFrames;

    public SmpteOffset(long tick, long delta, FrameRate fps, int hour, int min, int sec, int fr, int subfr)
    {
        super(tick, delta, MetaEvent.SMPTE_OFFSET, new VariableLengthInt(5));

        mFrameRate = fps;
        mHours = hour;
        mMinutes = min;
        mSeconds = sec;
        mFrames = fr;
        mSubFrames = subfr;
    }

    public void setFrameRate(FrameRate fps)
    {
        mFrameRate = fps;
    }

    public FrameRate getFrameRate()
    {
        return mFrameRate;
    }

    public void setHours(int h)
    {
        mHours = h;
    }

    public int getHours()
    {
        return mHours;
    }

    public void setMinutes(int m)
    {
        mMinutes = m;
    }

    public int getMinutes()
    {
        return mMinutes;
    }

    public void setSeconds(int s)
    {
        mSeconds = s;
    }

    public int getSeconds()
    {
        return mSeconds;
    }

    public void setFrames(int f)
    {
        mFrames = f;
    }

    public int getFrames()
    {
        return mFrames;
    }

    public void setSubFrames(int s)
    {
        mSubFrames = s;
    }

    public int getSubFrames()
    {
        return mSubFrames;
    }

    @Override
    protected int getEventSize()
    {
        return 8;
    }

    @Override
    public void writeToFile(OutputStream out) throws IOException
    {
        super.writeToFile(out);

        out.write(5);
        out.write(mHours);
        out.write(mMinutes);
        out.write(mSeconds);
        out.write(mFrames);
        out.write(mSubFrames);
    }

    public static MetaEvent parseSmpteOffset(long tick, long delta, MetaEventData info)
    {
        if(info.length.getValue() != 5)
        {
            return new GenericMetaEvent(tick, delta, info);
        }

        int rrHours = info.data[0];

        int rr = rrHours >> 5;
        FrameRate fps = FrameRate.fromInt(rr);
        int hour = rrHours & 0x1F;

        int min = info.data[1];
        int sec = info.data[2];
        int frm = info.data[3];
        int sub = info.data[4];

        return new SmpteOffset(tick, delta, fps, hour, min, sec, frm, sub);
    }

    public enum FrameRate
    {
        FRAME_RATE_24(0x00), FRAME_RATE_25(0x01), FRAME_RATE_30_DROP(0x02), FRAME_RATE_30(0x03);

        public final int value;

        private FrameRate(int v)
        {
            value = v;
        }

        public static FrameRate fromInt(int val)
        {
            switch(val)
            {
                case 0:
                    return FRAME_RATE_24;
                case 1:
                    return FRAME_RATE_25;
                case 2:
                    return FRAME_RATE_30_DROP;
                case 3:
                    return FRAME_RATE_30;
            }
            return null;
        }
    }

    @Override
    public int compareTo(MidiEvent other)
    {
        if(mTick != other.getTick())
        {
            return mTick < other.getTick() ? -1 : 1;
        }
        if(mDelta.getValue() != other.getDelta())
        {
            return mDelta.getValue() < other.getDelta() ? 1 : -1;
        }

        if(!(other instanceof SmpteOffset))
        {
            return 1;
        }

        return 0;
    }
}
