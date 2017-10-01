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

public class ChannelAftertouch extends ChannelEvent
{
    public ChannelAftertouch(long tick, int channel, int amount)
    {
        super(tick, ChannelEvent.CHANNEL_AFTERTOUCH, channel, amount, 0);
    }

    public ChannelAftertouch(long tick, long delta, int channel, int amount)
    {
        super(tick, delta, ChannelEvent.CHANNEL_AFTERTOUCH, channel, amount, 0);
    }

    public int getAmount()
    {
        return mValue1;
    }

    public void setAmount(int p)
    {
        mValue1 = p;
    }
}
