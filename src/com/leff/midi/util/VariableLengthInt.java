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

package com.leff.midi.util;

import java.io.IOException;
import java.io.InputStream;

public class VariableLengthInt
{
    private int mValue;
    private byte[] mBytes;
    private int mSizeInBytes;

    public VariableLengthInt(int value)
    {
        setValue(value);
    }

    public VariableLengthInt(InputStream in) throws IOException
    {
        parseBytes(in);
    }

    public void setValue(int value)
    {
        mValue = value;
        buildBytes();
    }

    public int getValue()
    {
        return mValue;
    }

    public int getByteCount()
    {
        return mSizeInBytes;
    }

    public byte[] getBytes()
    {
        return mBytes;
    }

    private void parseBytes(InputStream in) throws IOException
    {
        int[] ints = new int[4];

        mSizeInBytes = 0;
        mValue = 0;
        int shift = 0;

        int b = in.read();
        while(mSizeInBytes < 4)
        {
            mSizeInBytes++;
            
            boolean variable = (b & 0x80) > 0;
            if(!variable)
            {
                ints[mSizeInBytes - 1] = (b & 0x7F);
                break;
            }
            ints[mSizeInBytes - 1] = (b & 0x7F);

            b = in.read();
        }

        for(int i = 1; i < mSizeInBytes; i++)
        {
            shift += 7;
        }

        mBytes = new byte[mSizeInBytes];
        for(int i = 0; i < mSizeInBytes; i++)
        {
            mBytes[i] = (byte) ints[i];

            mValue += ints[i] << shift;
            shift -= 7;
        }
    }

    private void buildBytes()
    {
        if(mValue == 0)
        {
            mBytes = new byte[1];
            mBytes[0] = 0x00;
            mSizeInBytes = 1;
            return;
        }

        mSizeInBytes = 0;
        int[] vals = new int[4];
        int tmpVal = mValue;

        while(mSizeInBytes < 4 && tmpVal > 0)
        {
            vals[mSizeInBytes] = tmpVal & 0x7F;

            mSizeInBytes++;
            tmpVal = tmpVal >> 7;
        }

        for(int i = 1; i < mSizeInBytes; i++)
        {
            vals[i] |= 0x80;
        }

        mBytes = new byte[mSizeInBytes];
        for(int i = 0; i < mSizeInBytes; i++)
        {
            mBytes[i] = (byte) vals[mSizeInBytes - i - 1];
        }
    }

    @Override
    public String toString()
    {
        return MidiUtil.bytesToHex(mBytes) + " (" + mValue + ")";
    }
}
