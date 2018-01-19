package ch.unibas.dmi.dbis.sndd;

import ch.unibas.dmi.dbis.sndd.ambryfs.AmbryFileSystemProvider;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import javax.imageio.ImageIO;

class Main {

  private static final String ambryID = "AAMAAf__AAAAAQAAAAAAAAAAAAAAJGUxMDJjYjkxLTU4M2MtNDliMS05MTlmLWRjN2M3ODQyN2M0OQ";

  public static void main(String[] args) throws URISyntaxException, IOException {
    AmbryFileSystemProvider provider = new AmbryFileSystemProvider();
    FileSystem one = provider.newFileSystem(new URI("ambry:localhost:1174"), new HashMap<>());
    FileSystem two = provider.getFileSystem(new URI("ambry://localhost:1174"));
    FileSystem three = provider.getFileSystem(new URI("ambry://localhost:1174/"));
    FileSystem four = provider.getFileSystem(new URI("ambry:localhost:1174/"));
    FileSystem five = provider.getFileSystem(new URI("ambry://localhost:1174/" + ambryID));
    FileSystem six = provider.getFileSystem(new URI("ambry:localhost:1174/" + ambryID));
    if (!(one == two)) {
      throw new RuntimeException();
    }
    if (!(three == two)) {
      throw new RuntimeException();
    }
    if (!(four == three)) {
      throw new RuntimeException();
    }
    if (!(five == four)) {
      throw new RuntimeException();
    }
    if (!(six == five)) {
      throw new RuntimeException();
    }
    Path path = one.getPath("/");
    System.out.println(path.toString());
    System.out.println(path.resolve(ambryID));
    Path idPath = one.getPath(ambryID);
    System.out.println("IDPath: " + idPath);
    System.out.println("IDPath abs: " + idPath.toAbsolutePath());

    InputStream is = Files.newInputStream(idPath, StandardOpenOption.READ);
    System.out.println("Reading File");
    BufferedImage input = ImageIO.read(is);
    System.out.println("Writing file");
    ImageIO.write(input, "png", new File("output.png"));
  }
}