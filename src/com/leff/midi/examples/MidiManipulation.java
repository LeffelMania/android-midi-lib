package com.leff.midi.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;

public class MidiManipulation
{
    public static void main(String[] args)
    {
        // 1. Open up a MIDI file
        MidiFile mf = null;
        File input = new File("example.mid");

        try
        {
            mf = new MidiFile(input);
        }
        catch(IOException e)
        {
            System.err.println("Error parsing MIDI file:");
            e.printStackTrace();
            return;
        }

        // 2. Do some editing to the file
        // 2a. Strip out anything but notes from track 1
        MidiTrack T = mf.getTracks().get(1);

        // It's a bad idea to modify a set while iterating, so we'll collect
        // the events first, then remove them afterwards
        Iterator<MidiEvent> it = T.getEvents().iterator();
        ArrayList<MidiEvent> eventsToRemove = new ArrayList<MidiEvent>();

        while(it.hasNext())
        {
            MidiEvent E = it.next();

            if(!E.getClass().equals(NoteOn.class) && !E.getClass().equals(NoteOff.class))
            {
                eventsToRemove.add(E);
            }
        }

        for(MidiEvent E : eventsToRemove)
        {
            T.removeEvent(E);
        }

        // 2b. Completely remove track 2
        mf.removeTrack(2);

        // 2c. Reduce the tempo by half
        T = mf.getTracks().get(0);

        it = T.getEvents().iterator();
        while(it.hasNext())
        {
            MidiEvent E = it.next();

            if(E.getClass().equals(Tempo.class))
            {

                Tempo tempo = (Tempo) E;
                tempo.setBpm(tempo.getBpm() / 2);
            }
        }

        // 3. Save the file back to disk
        try
        {
            mf.writeToFile(input);
        }
        catch(IOException e)
        {
            System.err.println("Error writing MIDI file:");
            e.printStackTrace();
        }
    }
}
