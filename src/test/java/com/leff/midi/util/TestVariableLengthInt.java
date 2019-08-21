package com.leff.midi.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.Assert.*;

public class TestVariableLengthInt
{
    @Test
    public void testMinimumOneByteValueFromInt() {
        VariableLengthInt vli = new VariableLengthInt(0x00);
        assertEquals(0x00, vli.getValue());
        assertEquals(1, vli.getByteCount());
        assertArrayEquals(new byte[] { 0x00 }, vli.getBytes());
    }
    
    @Test
    public void testMinimumOneByteValueFromStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[] { 0x00 });
        VariableLengthInt vli = new VariableLengthInt(stream);
        assertEquals(0x00, vli.getValue());
        assertEquals(1, vli.getByteCount());
        assertArrayEquals(new byte[] { 0x00 }, vli.getBytes());
    }
    
    @Test
    public void testMaximumOneByteValueFromInt() {
        VariableLengthInt vli = new VariableLengthInt(0x7F);
        assertEquals(0x7F, vli.getValue());
        assertEquals(1, vli.getByteCount());
        assertArrayEquals(new byte[] { 0x7F }, vli.getBytes());
    }

    @Test
    public void testMaximumOneByteValueFromStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[] {0x7F});
        VariableLengthInt vli = new VariableLengthInt(stream);
        assertEquals(0x7F, vli.getValue());
        assertEquals(1, vli.getByteCount());
        assertArrayEquals(new byte[] { 0x7F }, vli.getBytes());
    }
    
    @Test
    public void testMinimumTwoByteValueFromInt() {
        VariableLengthInt vli = new VariableLengthInt(0x80);
        assertEquals(0x80, vli.getValue());
        assertEquals(2, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0x81, 0x00 }, vli.getBytes());
    }

    @Test
    public void testMinimumTwoByteValueFromStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[] { (byte) 0x81, 0x00 });
        VariableLengthInt vli = new VariableLengthInt(stream);
        assertEquals(0x80, vli.getValue());
        assertEquals(2, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0x81, 0x00 }, vli.getBytes());
    }
    
    @Test
    public void testMaximumTwoByteValueFromInt() {
        VariableLengthInt vli = new VariableLengthInt(0x3F_FF);
        assertEquals(0x3F_FF, vli.getValue());
        assertEquals(2, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0xFF, 0x7F }, vli.getBytes());
    }
    
    @Test
    public void testMaximumTwoByteValueFromStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[] { (byte) 0xFF, 0x7F });
        VariableLengthInt vli = new VariableLengthInt(stream);
        assertEquals(0x3F_FF, vli.getValue());
        assertEquals(2, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0xFF, 0x7F }, vli.getBytes());
    }
    
    @Test
    public void testMinimumThreeByteValueFromInt() {
        VariableLengthInt vli = new VariableLengthInt(0x40_00);
        assertEquals(0x40_00, vli.getValue());
        assertEquals(3, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0x81, (byte) 0x80, 0x00 }, vli.getBytes());
    }

    @Test
    public void testMinimumThreeByteValueFromStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[] { (byte) 0x81, (byte) 0x80, 0x00 });
        VariableLengthInt vli = new VariableLengthInt(stream);
        assertEquals(0x40_00, vli.getValue());
        assertEquals(3, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0x81, (byte) 0x80, 0x00 }, vli.getBytes());
    }
    
    @Test
    public void testMaximumThreeByteValueFromInt() {
        VariableLengthInt vli = new VariableLengthInt(0x1F_FF_FF);
        assertEquals(0x1F_FF_FF, vli.getValue());
        assertEquals(3, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF, 0x7F }, vli.getBytes());
    }

    @Test
    public void testMaximumThreeByteValueFromStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[] { (byte) 0xFF, (byte) 0xFF, 0x7F });
        VariableLengthInt vli = new VariableLengthInt(stream);
        assertEquals(0x1F_FF_FF, vli.getValue());
        assertEquals(3, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF, 0x7F }, vli.getBytes());
    }
    
    @Test
    public void testMinimumFourByteValueFromInt() {
        VariableLengthInt vli = new VariableLengthInt(0x20_00_00);
        assertEquals(0x20_00_00, vli.getValue());
        assertEquals(4, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0x81, (byte) 0x80, (byte) 0x80, 0x00 }, vli.getBytes());
    }

    @Test
    public void testMinimumFourByteValueFromStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[] { (byte) 0x81, (byte) 0x80, (byte) 0x80, 0x00 });
        VariableLengthInt vli = new VariableLengthInt(stream);
        assertEquals(0x20_00_00, vli.getValue());
        assertEquals(4, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0x81, (byte) 0x80, (byte) 0x80, 0x00 }, vli.getBytes());
    }
    
    @Test
    public void testMaximumFourByteValueFromInt() {
        VariableLengthInt vli = new VariableLengthInt(0x0F_FF_FF_FF);
        assertEquals(0x0F_FF_FF_FF, vli.getValue());
        assertEquals(4, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F }, vli.getBytes());
    }

    @Test
    public void testMaximumFourByteValueFromStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F });
        VariableLengthInt vli = new VariableLengthInt(stream);
        assertEquals(0x0F_FF_FF_FF, vli.getValue());
        assertEquals(4, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F }, vli.getBytes());
    }
    
    @Test
    public void testSetValue() {
        VariableLengthInt vli = new VariableLengthInt(0);
        
        vli.setValue(0x40);
        assertEquals(0x40, vli.getValue());
        assertEquals(1, vli.getByteCount());
        assertArrayEquals(new byte[] { 0x40 }, vli.getBytes());
        
        vli.setValue(0x08_00_00_00);
        assertEquals(0x08_00_00_00, vli.getValue());
        assertEquals(4, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0xC0, (byte) 0x80, (byte) 0x80, 0x00 }, vli.getBytes());
     
        vli.setValue(0x20_00);
        assertEquals(0x20_00, vli.getValue());
        assertEquals(2, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0xC0, 0x00 }, vli.getBytes());

        vli.setValue(0x10_00_00);
        assertEquals(0x10_00_00, vli.getValue());
        assertEquals(3, vli.getByteCount());
        assertArrayEquals(new byte[] { (byte) 0xC0, (byte) 0x80, 0x00 }, vli.getBytes());
    }
    
    @Test
    public void testToString() {
        assertEquals("00  (0)", new VariableLengthInt(0).toString());
        assertEquals("8F FF FF 7F  (33554431)", new VariableLengthInt(0x01_FF_FF_FF).toString());
    }
}
