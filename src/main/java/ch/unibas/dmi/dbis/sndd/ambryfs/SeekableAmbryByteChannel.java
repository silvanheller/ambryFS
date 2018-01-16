package ch.unibas.dmi.dbis.sndd.ambryfs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/**
 * @author silvan on 16.01.18.
 */
public class SeekableAmbryByteChannel implements SeekableByteChannel {

  private final byte[] data;
  private long position;

  SeekableAmbryByteChannel(byte[] data) {
    this.data = data;
  }

  @Override
  public int read(ByteBuffer dst) throws IOException {
    int l = (int) Math.min(dst.remaining(), size() - position);
    dst.put(data, (int) position, l);
    position += l;
    return l;
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public long position() throws IOException {
    return position;
  }

  @Override
  public SeekableByteChannel position(long newPosition) throws IOException {
    position = newPosition;
    return this;
  }

  @Override
  public long size() throws IOException {
    return data.length;
  }

  @Override
  public SeekableByteChannel truncate(long size) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public void close() throws IOException {

  }
}
