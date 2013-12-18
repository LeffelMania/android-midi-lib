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

public class KeySignature extends MetaEvent
{
    public static final int SCALE_MAJOR = 0;
    public static final int SCALE_MINOR = 1;

    private int mKey;
    private int mScale;

    public KeySignature(long tick, long delta, int key, int scale)
    {
        super(tick, delta, MetaEvent.KEY_SIGNATURE, new VariableLengthInt(2));

        this.setKey(key);
        mScale = scale;
    }

    public void setKey(int key)
    {
        mKey = (byte) key;

        if(mKey < -7)
            mKey = -7;
        else if(mKey > 7)
            mKey = 7;
    }

    public int getKey()
    {
        return mKey;
    }

    public void setScale(int scale)
    {
        mScale = scale;
    }

    public int getScale()
    {
        return mScale;
    }

    @Override
    protected int getEventSize()
    {
        return 5;
    }

    @Override
    public void writeToFile(OutputStream out) throws IOException
    {
        super.writeToFile(out);

        out.write(2);
        out.write(mKey);
        out.write(mScale);
    }

    public static MetaEvent parseKeySignature(long tick, long delta, MetaEventData info)
    {
        if(info.length.getValue() != 2)
        {
            return new GenericMetaEvent(tick, delta, info);
        }

        int key = info.data[0];
        int scale = info.data[1];

        return new KeySignature(tick, delta, key, scale);
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

        if(!(other instanceof KeySignature))
        {
            return 1;
        }

        KeySignature o = (KeySignature) other;
        if(mKey != o.mKey)
        {
            return mKey < o.mKey ? -1 : 1;
        }

        if(mScale != o.mScale)
        {
            return mKey < o.mScale ? -1 : 1;
        }

        return 0;
    }
}
