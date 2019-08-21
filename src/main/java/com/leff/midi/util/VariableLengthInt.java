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

/**
 * From the official "Standard MIDI File" specification:
 * <p>
 * Some numbers in MIDI Files are represented in
 * a form called a variable-length quantity. These numbers are represented 7 bits per byte, most significant
 * bits first. All bytes except the last have bit 7 (the most significant bit) set, and the last byte has bit
 * 7 clear. If the number is between 0 and 127, it is thus represented exactly as one byte.
 * <p>
 * Here are some examples of numbers represented as variable-length quantities:
 * <p>
 * Number (hex) = Representation (hex)<br>
 * <code>00 00 00 00 = 00 </code><br>
 * <code>00 00 00 40 = 40 </code><br>
 * <code>00 00 00 7F = 7F </code><br>
 * <code>00 00 00 80 = 81 00 </code><br>
 * <code>00 00 20 00 = C0 00 </code><br>
 * <code>00 00 3F FF = FF 7F </code><br>
 * <code>00 00 40 00 = 81 80 00 </code><br>
 * <code>00 10 00 00 = C0 80 00 </code><br>
 * <code>00 1F FF FF = FF FF 7F </code><br>
 * <code>00 20 00 00 = 81 80 80 00 </code><br>
 * <code>08 00 00 00 = C0 80 80 00 </code><br>
 * <code>0F FF FF FF = FF FF FF 7F </code><br>
 * <p>
 * The largest number which is allowed is 0FFFFFFF 
 */
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
            ints[mSizeInBytes - 1] = (b & 0xFF);

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

            mValue += (ints[i] & 0x7F) << shift;
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
