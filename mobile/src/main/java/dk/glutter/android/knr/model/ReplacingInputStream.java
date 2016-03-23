package dk.glutter.android.knr.model;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by U321424 on 22-03-2016.
 */
public class ReplacingInputStream extends FilterInputStream
{
    /**
     * Constructs a new {@code FilterInputStream} with the specified input
     * stream as source.
     * <p/>
     * <p><strong>Warning:</strong> passing a null source creates an invalid
     * {@code FilterInputStream}, that fails on every method that is not
     * overridden. Subclasses should check for null in their constructors.
     *
     * @param in the input stream to filter reads on.
     */
    protected ReplacingInputStream(InputStream in) {
        super(in);
    }

    public int read() throws IOException
    {
        int read = super.read();
        if (read!=-1 && read<0x20 && !(read==0x9 || read==0xA || read==0xB))
            read = 0x20;
        return read;
    }
}
