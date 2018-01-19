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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author silvan on 16.01.18.
 */
public class AmbryFileSystem extends FileSystem {

  private final String host;
  private final int port;
  private final AmbryFileSystemProvider provider;
  private static final String AMBRY_SEPARATOR = "/";
  private volatile boolean open = true;
  private static final Logger LOGGER = LogManager.getLogger();
  private final OkHttpClient client = new OkHttpClient();

  String getHost() {
    return host;
  }

  int getPort() {
    return port;
  }

  AmbryFileSystem(String host, int port, AmbryFileSystemProvider provider) {
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

  byte[] get(String ambryPath) throws IOException {
    if (!this.isOpen()) {
      throw new ClosedFileSystemException();
    }

    String cleanPath = ambryPath.replace("/", "");
    Builder builder = new Builder()
        .url("http://" + this.getHost() + ":" + this.getPort() + "/" + cleanPath)
        .addHeader("ambry-id", cleanPath);
    Request request = builder.build();
    Response response = client.newCall(request).execute();
    if (response.code() != 200) {
      LOGGER.error("Got response code {} for get", response.code());
      throw new IOException(response.message());
    }
    byte[] data = response.body().bytes();    //This is an awful idea for Large Files
    if (data == null) {
      throw new IOException();
    }
    System.out.println("Returning byte array");
    return data;
  }
}
