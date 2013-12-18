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

public class SequencerSpecificEvent extends MetaEvent
{
    private byte[] mData;

    public SequencerSpecificEvent(long tick, long delta, byte[] data)
    {
        super(tick, delta, MetaEvent.SEQUENCER_SPECIFIC, new VariableLengthInt(data.length));

        mData = data;
    }

    public void setData(byte[] data)
    {
        mData = data;
        mLength.setValue(mData.length);
    }

    public byte[] getData()
    {
        return mData;
    }

    protected int getEventSize()
    {
        return 1 + 1 + mLength.getByteCount() + mData.length;
    }

    @Override
    public void writeToFile(OutputStream out) throws IOException
    {
        super.writeToFile(out);

        out.write(mLength.getBytes());
        out.write(mData);
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

        if(!(other instanceof SequencerSpecificEvent))
        {
            return 1;
        }

        SequencerSpecificEvent o = (SequencerSpecificEvent) other;

        if(MidiUtil.bytesEqual(mData, o.mData, 0, mData.length))
        {
            return 0;
        }
        return 1;
    }
}
