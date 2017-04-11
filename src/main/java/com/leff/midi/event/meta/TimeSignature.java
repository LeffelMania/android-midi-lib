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

public class TimeSignature extends MetaEvent
{
    public static final int METER_EIGHTH = 12;
    public static final int METER_QUARTER = 24;
    public static final int METER_HALF = 48;
    public static final int METER_WHOLE = 96;

    public static final int DEFAULT_METER = METER_QUARTER;
    public static final int DEFAULT_DIVISION = 8;

    private int mNumerator;
    private int mDenominator;
    private int mMeter;
    private int mDivision;

    public TimeSignature()
    {
        this(0, 0, 4, 4, DEFAULT_METER, DEFAULT_DIVISION);
    }

    public TimeSignature(long tick, long delta, int num, int den, int meter, int div)
    {
        super(tick, delta, MetaEvent.TIME_SIGNATURE, new VariableLengthInt(4));

        setTimeSignature(num, den, meter, div);
    }

    public void setTimeSignature(int num, int den, int meter, int div)
    {
        mNumerator = num;
        mDenominator = log2(den);
        mMeter = meter;
        mDivision = div;
    }

    public int getNumerator()
    {
        return mNumerator;
    }

    public int getDenominatorValue()
    {
        return mDenominator;
    }

    public int getRealDenominator()
    {
        return (int) Math.pow(2, mDenominator);
    }

    public int getMeter()
    {
        return mMeter;
    }

    public int getDivision()
    {
        return mDivision;
    }

    @Override
    protected int getEventSize()
    {
        return 7;
    }

    @Override
    public void writeToFile(OutputStream out) throws IOException
    {
        super.writeToFile(out);

        out.write(4);
        out.write(mNumerator);
        out.write(mDenominator);
        out.write(mMeter);
        out.write(mDivision);
    }

    public static MetaEvent parseTimeSignature(long tick, long delta, MetaEventData info)
    {
        if(info.length.getValue() != 4)
        {
            return new GenericMetaEvent(tick, delta, info);
        }

        int num = info.data[0];
        int den = info.data[1];
        int met = info.data[2];
        int fps = info.data[3];

        den = (int) Math.pow(2, den);

        return new TimeSignature(tick, delta, num, den, met, fps);
    }

    private int log2(int den)
    {
        switch(den)
        {
            case 2:
                return 1;
            case 4:
                return 2;
            case 8:
                return 3;
            case 16:
                return 4;
            case 32:
                return 5;
        }
        return 0;
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + mNumerator + "/" + getRealDenominator();
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

        if(!(other instanceof TimeSignature))
        {
            return 1;
        }

        TimeSignature o = (TimeSignature) other;

        if(mNumerator != o.mNumerator)
        {
            return mNumerator < o.mNumerator ? -1 : 1;
        }
        if(mDenominator != o.mDenominator)
        {
            return mDenominator < o.mDenominator ? -1 : 1;
        }
        return 0;
    }
}
