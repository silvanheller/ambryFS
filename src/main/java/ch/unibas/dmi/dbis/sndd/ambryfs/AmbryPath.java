package ch.unibas.dmi.dbis.sndd.ambryfs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author silvan on 16.01.18.
 */
public class AmbryPath implements Path {

  private final String ambryPath;
  private final AmbryFileSystem fs;
  private byte[] data = null;  //Ambry is immutable

  public String getAmbryPath() {
    return ambryPath;
  }

  public AmbryPath(String ambryPath, AmbryFileSystem fs) {
    this.ambryPath = ambryPath;
    this.fs = fs;
  }

  @Override
  public FileSystem getFileSystem() {
    return fs;
  }

  @Override
  public boolean isAbsolute() {
    return ambryPath.startsWith("/");
  }

  @Override
  public Path getRoot() {
    return fs.getPath("/");
  }

  @Override
  public Path getFileName() {
    return fs.getPath(ambryPath);
  }

  @Override
  public Path getParent() {
    if (!this.ambryPath.equals("/")) {
      return fs.getPath("/");
    }
    return null;  //Java API behavior
  }

  @Override
  public int getNameCount() {
    if (this.ambryPath.equals("/")) {
      return 0;
    }
    return 1;
  }

  @Override
  public Path getName(int index) {
    if (index != 0) {
      throw new IllegalArgumentException("Index " + index + " not supported");
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public Path subpath(int beginIndex, int endIndex) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean startsWith(Path other) {
    return startsWith(((AmbryPath) other).getAmbryPath());
  }

  @Override
  public boolean startsWith(String other) {
    if (other.equals("/")) {
      return true;
    }
    return false;
  }

  @Override
  public boolean endsWith(Path other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean endsWith(String other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path normalize() {
    return this;
  }

  @Override
  public Path resolve(Path other) {
    return resolve(((AmbryPath) other).getAmbryPath());
  }

  @Override
  public Path resolve(String other) {
    if (this.ambryPath.equals("/")) {
      return fs.getPath("/", other.replace("/", ""));
    }
    throw new IllegalArgumentException(other);
  }

  @Override
  public Path resolveSibling(Path other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path resolveSibling(String other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path relativize(Path other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public URI toUri() {
    String uri = "ambry:" + fs.getHost() + ":" + fs.getPort() + "/" + ambryPath.replaceAll("/", "");
    try {
      return new URI(uri);
    } catch (URISyntaxException e) {
      throw new RuntimeException("URI " + uri + " had invalid syntax");
    }
  }

  @Override
  public Path toAbsolutePath() {
    return fs.getPath("/".concat(this.ambryPath.replaceAll("/", "")));
  }

  @Override
  public Path toRealPath(LinkOption... options) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public File toFile() {
    throw new UnsupportedOperationException();
  }

  @Override
  public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers)
      throws IOException {
    return null;
  }

  @Override
  public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<Path> iterator() {
    if (this.ambryPath.equals("/")) {
      throw new IllegalArgumentException("Ambry cannot iterate over the root directory");
    }
    final List<Path> els = new ArrayList<>(1);
    els.add(this);
    return els.iterator();
  }

  @Override
  public int compareTo(Path other) {
    return Integer.compare(this.ambryPath.length(), ((AmbryPath) other).getAmbryPath().length());
  }

  byte[] fetchData() {
    if (data != null) {
      return data;
    }
    data = fs.get(ambryPath);
    return data;
  }

  void delete() {
    throw new UnsupportedOperationException();
    //Implementable
  }

  @Override
  public String toString() {
    return ambryPath;
  }

  public AmbryFileAttributes getReadAttributes() {
    return new AmbryFileAttributes(data.length);
  }
}
