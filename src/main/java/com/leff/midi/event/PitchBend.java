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

public class PitchBend extends ChannelEvent
{
    public PitchBend(long tick, int channel, int lsb, int msb)
    {
        super(tick, ChannelEvent.PITCH_BEND, channel, lsb, msb);
    }

    public PitchBend(long tick, long delta, int channel, int lsb, int msb)
    {
        super(tick, delta, ChannelEvent.PITCH_BEND, channel, lsb, msb);
    }

    public int getLeastSignificantBits()
    {
        return mValue1;
    }

    public int getMostSignificantBits()
    {
        return mValue2;
    }

    public int getBendAmount()
    {
        int y = (mValue2 & 0x7F) << 7;
        int x = (mValue1);

        return y + x;
    }

    public void setLeastSignificantBits(int p)
    {
        mValue1 = p & 0x7F;
    }

    public void setMostSignificantBits(int p)
    {
        mValue2 = p & 0x7F;
    }

    public void setBendAmount(int amount)
    {
        amount = amount & 0x3FFF;
        mValue1 = (amount & 0x7F);
        mValue2 = amount >> 7;
    }
}
