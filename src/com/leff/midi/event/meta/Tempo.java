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
import com.leff.midi.util.MidiUtil;
import com.leff.midi.util.VariableLengthInt;

public class Tempo extends MetaEvent
{
    public static final float DEFAULT_BPM = 120.0f;
    public static final int DEFAULT_MPQN = (int) (60000000 / DEFAULT_BPM);

    private int mMPQN;
    private float mBPM;

    public Tempo()
    {
        this(0, 0, DEFAULT_MPQN);
    }

    public Tempo(long tick, long delta, int mpqn)
    {
        super(tick, delta, MetaEvent.TEMPO, new VariableLengthInt(3));

        setMpqn(mpqn);
    }

    public int getMpqn()
    {
        return mMPQN;
    }

    public float getBpm()
    {
        return mBPM;
    }

    public void setMpqn(int m)
    {
        mMPQN = m;
        mBPM = 60000000.0f / mMPQN;
    }

    public void setBpm(float b)
    {
        mBPM = b;
        mMPQN = (int) (60000000 / mBPM);
    }

    @Override
    protected int getEventSize()
    {
        return 6;
    }

    @Override
    public void writeToFile(OutputStream out) throws IOException
    {
        super.writeToFile(out);

        out.write(3);
        out.write(MidiUtil.intToBytes(mMPQN, 3));
    }

    public static MetaEvent parseTempo(long tick, long delta, MetaEventData info)
    {
        if(info.length.getValue() != 3)
        {
            return new GenericMetaEvent(tick, delta, info);
        }

        int mpqn = MidiUtil.bytesToInt(info.data, 0, 3);

        return new Tempo(tick, delta, mpqn);
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

        if(!(other instanceof Tempo))
        {
            return 1;
        }

        Tempo o = (Tempo) other;

        if(mMPQN != o.mMPQN)
        {
            return mMPQN < o.mMPQN ? -1 : 1;
        }
        return 0;
    }
}
