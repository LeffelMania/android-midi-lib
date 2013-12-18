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

package com.leff.midi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.TreeSet;

import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.EndOfTrack;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;
import com.leff.midi.util.MidiUtil;
import com.leff.midi.util.VariableLengthInt;

public class MidiTrack
{
    private static final boolean VERBOSE = false;

    public static final byte[] IDENTIFIER = { 'M', 'T', 'r', 'k' };

    private int mSize;
    private boolean mSizeNeedsRecalculating;
    private boolean mClosed;
    private long mEndOfTrackDelta;

    private TreeSet<MidiEvent> mEvents;

    public static MidiTrack createTempoTrack()
    {
        MidiTrack T = new MidiTrack();

        T.insertEvent(new TimeSignature());
        T.insertEvent(new Tempo());

        return T;
    }

    public MidiTrack()
    {
        mEvents = new TreeSet<MidiEvent>();
        mSize = 0;
        mSizeNeedsRecalculating = false;
        mClosed = false;
        mEndOfTrackDelta = 0;
    }

    public MidiTrack(InputStream in) throws IOException
    {
        this();

        byte[] buffer = new byte[4];
        in.read(buffer);

        if(!MidiUtil.bytesEqual(buffer, IDENTIFIER, 0, 4))
        {
            System.err.println("Track identifier did not match MTrk!");
            return;
        }

        in.read(buffer);
        mSize = MidiUtil.bytesToInt(buffer, 0, 4);
        
        buffer = new byte[mSize];
        in.read(buffer);
        
        this.readTrackData(buffer);
    }
    
    private void readTrackData(byte[] data) throws IOException
    {
        InputStream in = new ByteArrayInputStream(data);

        long totalTicks = 0;
        
        while(in.available() > 0)
        {
            VariableLengthInt delta = new VariableLengthInt(in);
            totalTicks += delta.getValue();

            MidiEvent E = MidiEvent.parseEvent(totalTicks, delta.getValue(), in);
            if(E == null)
            {
                System.out.println("Event skipped!");
                continue;
            }

            if(VERBOSE)
            {
                System.out.println(E);
            }

            // Not adding the EndOfTrack event here allows the track to be
            // edited
            // after being read in from file.
            if(E.getClass().equals(EndOfTrack.class))
            {
                mEndOfTrackDelta = E.getDelta();
                break;
            }
            mEvents.add(E);
        }
    }

    public TreeSet<MidiEvent> getEvents()
    {
        return mEvents;
    }

    public int getEventCount()
    {
        return mEvents.size();
    }

    public int getSize()
    {
        if(mSizeNeedsRecalculating)
        {
            recalculateSize();
        }
        return mSize;
    }

    public long getLengthInTicks()
    {
        if(mEvents.size() == 0)
        {
            return 0;
        }

        MidiEvent E = mEvents.last();
        return E.getTick();
    }

    public long getEndOfTrackDelta()
    {
        return mEndOfTrackDelta;
    }

    public void setEndOfTrackDelta(long delta)
    {
        mEndOfTrackDelta = delta;
    }

    public void insertNote(int channel, int pitch, int velocity, long tick, long duration)
    {

        insertEvent(new NoteOn(tick, channel, pitch, velocity));
        insertEvent(new NoteOn(tick + duration, channel, pitch, 0));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insertEvent(MidiEvent newEvent)
    {
        if(newEvent == null)
        {
            return;
        }

        if(mClosed)
        {
            System.err.println("Error: Cannot add an event to a closed track.");
            return;
        }

        MidiEvent prev = null, next = null;

        // floor() and ceiling() are not supported on Android before API Level 9
        // (Gingerbread)
        try
        {
            Class treeSet = Class.forName("java.util.TreeSet");
            Method floor = treeSet.getMethod("floor", Object.class);
            Method ceiling = treeSet.getMethod("ceiling", Object.class);

            prev = (MidiEvent) floor.invoke(mEvents, newEvent);
            next = (MidiEvent) ceiling.invoke(mEvents, newEvent);

        }
        catch(Exception e)
        {
            // methods are not supported - must perform linear search
            Iterator<MidiEvent> it = mEvents.iterator();

            while(it.hasNext())
            {
                next = it.next();

                if(next.getTick() > newEvent.getTick())
                {
                    break;
                }

                prev = next;
                next = null;
            }
        }

        mEvents.add(newEvent);
        mSizeNeedsRecalculating = true;

        // Set its delta time based on the previous event (or itself if no
        // previous event exists)
        if(prev != null)
        {
            newEvent.setDelta(newEvent.getTick() - prev.getTick());
        }
        else
        {
            newEvent.setDelta(newEvent.getTick());
        }

        // Update the next event's delta time relative to the new event.
        if(next != null)
        {
            next.setDelta(next.getTick() - newEvent.getTick());
        }

        mSize += newEvent.getSize();

        if(newEvent.getClass().equals(EndOfTrack.class))
        {
            if(next != null)
            {
                throw new IllegalArgumentException("Attempting to insert EndOfTrack before an existing event. Use closeTrack() when finished with MidiTrack.");
            }
            mClosed = true;
        }
    }

    public boolean removeEvent(MidiEvent E)
    {
        Iterator<MidiEvent> it = mEvents.iterator();
        MidiEvent prev = null, curr = null, next = null;

        while(it.hasNext())
        {
            next = it.next();

            if(E.equals(curr))
            {
                break;
            }

            prev = curr;
            curr = next;
            next = null;
        }

        if(next == null)
        {
            // Either the event was not found in the track,
            // or this is the last event in the track.
            // Either way, we won't need to update any delta times
            return mEvents.remove(curr);
        }

        if(!mEvents.remove(curr))
        {
            return false;
        }

        if(prev != null)
        {
            next.setDelta(next.getTick() - prev.getTick());
        }
        else
        {
            next.setDelta(next.getTick());
        }
        return true;
    }

    public void closeTrack()
    {
        long lastTick = 0;
        if(mEvents.size() > 0)
        {
            MidiEvent last = mEvents.last();
            lastTick = last.getTick();
        }
        EndOfTrack eot = new EndOfTrack(lastTick + mEndOfTrackDelta, 0);
        insertEvent(eot);
    }

    public void dumpEvents()
    {
        Iterator<MidiEvent> it = mEvents.iterator();
        while(it.hasNext())
        {
            System.out.println(it.next());
        }
    }

    private void recalculateSize()
    {
        mSize = 0;

        Iterator<MidiEvent> it = mEvents.iterator();
        MidiEvent last = null;
        while(it.hasNext())
        {
            MidiEvent E = it.next();
            mSize += E.getSize();

            // If an event is of the same type as the previous event,
            // no status byte is written.
            if(last != null && !E.requiresStatusByte(last))
            {
                mSize--;
            }
            last = E;
        }

        mSizeNeedsRecalculating = false;
    }

    public void writeToFile(OutputStream out) throws IOException
    {
        if(!mClosed)
        {
            closeTrack();
        }

        if(mSizeNeedsRecalculating)
        {
            recalculateSize();
        }

        out.write(IDENTIFIER);
        out.write(MidiUtil.intToBytes(mSize, 4));

        Iterator<MidiEvent> it = mEvents.iterator();
        MidiEvent lastEvent = null;

        while(it.hasNext())
        {
            MidiEvent event = it.next();
            if(VERBOSE)
            {
                System.out.println("Writing: " + event);
            }

            event.writeToFile(out, event.requiresStatusByte(lastEvent));

            lastEvent = event;
        }
    }
}
