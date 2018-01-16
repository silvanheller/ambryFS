package ch.unibas.dmi.dbis.sndd.ambryfs;

import java.io.IOException;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author silvan on 16.01.18.
 */
public class AmbryFileSystem extends FileSystem {

  private final String host;
  private final int port;
  private final AmbryFileSystemProvider provider;
  public static final String AMBRY_SEPARATOR = "/";
  private volatile boolean open = true;

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public AmbryFileSystem(String host, int port, AmbryFileSystemProvider provider) {
    this.host = host;
    this.port = port;
    this.provider = provider;
  }

  @Override
  public FileSystemProvider provider() {
    return provider;
  }

  @Override
  public void close() throws IOException {
    //TODO close rest?
    open = false;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  public boolean isReadOnly() {
    return true;
  }

  @Override
  public String getSeparator() {
    return AMBRY_SEPARATOR;
  }

  @Override
  public Iterable<Path> getRootDirectories() {
    Path root = this.getPath("/");
    List<Path> els = new ArrayList<>(1);
    els.add(root);
    return els;
  }

  @Override
  public Iterable<FileStore> getFileStores() {
    if (!this.isOpen()) {
      throw new ClosedFileSystemException();
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<String> supportedFileAttributeViews() {
    return new HashSet<>();
  }

  @Override
  public Path getPath(String first, String... more) {
    if (first.equals(AMBRY_SEPARATOR) && more.length == 0) {
      return new AmbryPath(AMBRY_SEPARATOR, this);
    }
    if (first.equals(AMBRY_SEPARATOR) && more.length > 1) {
      throw new IllegalArgumentException(first + ", " + Arrays.toString(more));
    }
    if (first.equals(AMBRY_SEPARATOR) && more.length == 1) {
      return new AmbryPath(AMBRY_SEPARATOR + more[0].replaceAll("/", ""), this);
    }
    if (!first.equals(AMBRY_SEPARATOR) && more.length > 1) {
      throw new IllegalArgumentException(first + ", " + Arrays.toString(more));
    }
    if (!first.equals(AMBRY_SEPARATOR) && more.length == 0) {
      return new AmbryPath(first, this);
    }
    throw new IllegalArgumentException(first + ", " + Arrays.toString(more));
  }

  @Override
  public PathMatcher getPathMatcher(String syntaxAndPattern) {
    throw new UnsupportedOperationException();
  }

  @Override
  public UserPrincipalLookupService getUserPrincipalLookupService() {
    throw new UnsupportedOperationException();
  }

  @Override
  public WatchService newWatchService() throws IOException {
    throw new UnsupportedOperationException();
  }

  public byte[] get(String ambryPath) {
    if (!this.isOpen()) {
      throw new ClosedFileSystemException();
    }

    //TODO GET Call
    return null;
  }
}
