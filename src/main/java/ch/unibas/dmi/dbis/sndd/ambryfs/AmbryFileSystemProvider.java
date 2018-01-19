package ch.unibas.dmi.dbis.sndd.ambryfs;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author silvan on 16.01.18.
 */
public class AmbryFileSystemProvider extends FileSystemProvider {

  private static String DEFAULT_HOST = "localhost";
  private static int DEFAULT_PORT = 1174;
  private Map<String, AmbryFileSystem> fs = new HashMap<>();

  private void createFS(String host, int port, String url) {
    fs.put(url, new AmbryFileSystem(host, port, this));
  }

  private AmbryFileSystem getSystem(URI uri, boolean create) {
    String host = uri.getHost();
    int port = uri.getPort();
    if (host == null) {
      host = DEFAULT_HOST;
    }
    if (port == -1) {
      port = DEFAULT_PORT;
    }
    String url = "http://" + host + ":" + port;
    if (create) {
      createFS(host, port, url);
    }
    if (!fs.containsKey(url)) {
      createFS(host, port, url);
    }
    if (fs.containsKey(url)) {
      return fs.get(url);
    }
    throw new IllegalArgumentException(
        "No Filesystem for the given URI " + uri.toString() + " exists");
  }

  @Override
  public String getScheme() {
    return "ambry";
  }

  @Override
  public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
    return getSystem(uri, true);
  }

  @Override
  public FileSystem getFileSystem(URI uri) {
    return getSystem(uri, false);
  }

  @Override
  public Path getPath(URI uri) {
    String ambryID = uri.getPath(); //Since we have a completely flat system, everything after the /
    return getFileSystem(uri).getPath(ambryID);
  }

  @Override
  public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options,
      FileAttribute<?>... attrs) throws IOException {
    if (!(path instanceof AmbryPath)) {
      throw new ProviderMismatchException();
    }
    return new SeekableAmbryByteChannel(((AmbryPath) path).fetchData());
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter)
      throws IOException {
    if (!(dir instanceof AmbryPath)) {
      throw new ProviderMismatchException();
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(Path path) throws IOException {
    if (!(path instanceof AmbryPath)) {
      throw new ProviderMismatchException();
    }
    ((AmbryPath) path).delete();
  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {
    throw new UnsupportedOperationException();  //Implementable
  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {
    throw new UnsupportedOperationException();  //Implementable
  }

  @Override
  public boolean isSameFile(Path path, Path path2) throws IOException {
    return path.toAbsolutePath().equals(path2.toAbsolutePath());
  }

  @Override
  public boolean isHidden(Path path) throws IOException {
    return false;
  }

  @Override
  public FileStore getFileStore(Path path) throws IOException {
    return null;  //?
  }

  @Override
  public void checkAccess(Path path, AccessMode... modes) throws IOException {
    //Ignore
  }

  @Override
  public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type,
      LinkOption... options) {
    return null; //?
  }

  @Override
  @SuppressWarnings("unchecked")
  public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type,
      LinkOption... options) throws IOException {
    if (!(path instanceof AmbryPath)) {
      throw new ProviderMismatchException();
    }
    return (A) ((AmbryPath) path).getReadAttributes();
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options)
      throws IOException {
    return null;
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options)
      throws IOException {
    throw new ReadOnlyFileSystemException();
  }
}
